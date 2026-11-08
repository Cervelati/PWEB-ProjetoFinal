package com.rosetas.service;

import com.rosetas.domain.Pagamento;
import com.rosetas.domain.StatusPagamento;
import com.rosetas.domain.StatusPedido;
import com.rosetas.repository.PagamentoRepository;
import com.rosetas.repository.PedidoRepository;
import com.rosetas.web.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PagamentoService {

    private static final Logger log = LoggerFactory.getLogger(PagamentoService.class);

    private final PagamentoRepository pagamentos;
    private final PedidoRepository pedidos;
    private final PedidoStatusService status;

    public PagamentoService(PagamentoRepository pagamentos, PedidoRepository pedidos, PedidoStatusService status) {
        this.pagamentos = pagamentos;
        this.pedidos = pedidos;
        this.status = status;
    }

    /** Idempotente: webhook repetido ou reconciliação concorrente não reprocessa. */
    @Transactional
    public void processar(String transacaoId, StatusPagamento novo, String origem) {
        // Ordem de locks do sistema: pedido -> pagamento (a mesma de cancelar/expirar), evitando deadlock.
        Long pedidoId = pagamentos.findPedidoIdByTransacao(transacaoId)
                .orElseThrow(() -> ApiException.notFound("Transação desconhecida"));
        pedidos.lockById(pedidoId).orElseThrow(() -> ApiException.notFound("Pedido não encontrado"));
        Pagamento pg = pagamentos.lockByGatewayTransacaoId(transacaoId)
                .orElseThrow(() -> ApiException.notFound("Transação desconhecida"));
        if (pg.getStatus() != StatusPagamento.PENDENTE || novo == StatusPagamento.PENDENTE) {
            return;
        }
        pg.setStatus(novo);
        if (novo == StatusPagamento.APROVADO) {
            if (pg.getPedido().getStatus() == StatusPedido.AGUARDANDO_PAGAMENTO) {
                status.transicionar(pg.getPedido(), StatusPedido.AGUARDANDO_APROVACAO, origem);
            } else {
                log.warn("Pagamento {} aprovado para pedido {} em status {}: reembolso manual necessário",
                        transacaoId, pg.getPedido().getId(), pg.getPedido().getStatus());
            }
        }
    }
}
