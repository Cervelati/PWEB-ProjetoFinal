package com.rosetas.service;

import com.rosetas.domain.Pagamento;
import com.rosetas.domain.StatusPagamento;
import com.rosetas.domain.StatusPedido;
import com.rosetas.payment.PagamentoGateway;
import com.rosetas.repository.PagamentoRepository;
import com.rosetas.repository.PedidoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

/**
 * RNF14: consulta periodicamente o gateway para pagamentos pendentes (caso o webhook falhe)
 * e libera o estoque reservado de pedidos que nunca foram pagos.
 */
@Component
public class PagamentoReconciliador {

    private static final Logger log = LoggerFactory.getLogger(PagamentoReconciliador.class);

    private final PagamentoRepository pagamentos;
    private final PedidoRepository pedidos;
    private final PagamentoGateway gateway;
    private final PagamentoService pagamentoService;
    private final PedidoService pedidoService;
    private final long expiracaoMinutos;

    public PagamentoReconciliador(PagamentoRepository pagamentos, PedidoRepository pedidos,
                                  PagamentoGateway gateway, PagamentoService pagamentoService,
                                  PedidoService pedidoService,
                                  @Value("${app.pagamento.expiracao-minutos}") long expiracaoMinutos) {
        this.pagamentos = pagamentos;
        this.pedidos = pedidos;
        this.gateway = gateway;
        this.pagamentoService = pagamentoService;
        this.pedidoService = pedidoService;
        this.expiracaoMinutos = expiracaoMinutos;
    }

    @Scheduled(fixedDelayString = "${app.pagamento.reconciliacao-ms}", initialDelayString = "${app.pagamento.reconciliacao-ms}")
    public void executar() {
        for (Pagamento pg : pagamentos.findByStatus(StatusPagamento.PENDENTE)) {
            try {
                StatusPagamento s = gateway.consultar(pg.getGatewayTransacaoId());
                if (s != StatusPagamento.PENDENTE) {
                    pagamentoService.processar(pg.getGatewayTransacaoId(), s, "reconciliacao");
                }
            } catch (Exception e) {
                log.warn("Falha ao reconciliar pagamento {}: {}", pg.getGatewayTransacaoId(), e.getMessage());
            }
        }
        OffsetDateTime limite = OffsetDateTime.now().minusMinutes(expiracaoMinutos);
        for (Long id : pedidos.findIdsByStatusAndDataBefore(StatusPedido.AGUARDANDO_PAGAMENTO, limite)) {
            try {
                pedidoService.expirar(id);
            } catch (Exception e) {
                log.warn("Falha ao expirar pedido {}: {}", id, e.getMessage());
            }
        }
    }
}
