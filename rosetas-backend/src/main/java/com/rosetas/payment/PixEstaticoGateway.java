package com.rosetas.payment;

import com.rosetas.domain.StatusPagamento;
import com.rosetas.service.ConfiguracaoService;
import com.rosetas.web.Dtos.PixConfigResp;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Pix com a chave da empresa (configurada no ERP), com o valor do pedido embutido no código.
 * Não há confirmação automática: a gestão confere o recebimento no banco e confirma no ERP.
 * Sem chave configurada, cai no gateway de desenvolvimento (stub).
 */
@Component
@Primary
public class PixEstaticoGateway implements PagamentoGateway {

    private final ConfiguracaoService config;
    private final StubPagamentoGateway stub;

    public PixEstaticoGateway(ConfiguracaoService config, StubPagamentoGateway stub) {
        this.config = config;
        this.stub = stub;
    }

    @Override
    public Cobranca criarCobrancaPix(Long pedidoId, BigDecimal valor) {
        PixConfigResp c = config.pix();
        if (!c.configurado()) {
            return stub.criarCobrancaPix(pedidoId, valor);
        }
        String codigo = PixBrCode.gerar(c.chave(), c.nome(), c.cidade(), valor, "PED" + pedidoId);
        return new Cobranca("est-" + UUID.randomUUID(), codigo);
    }

    @Override
    public StatusPagamento consultar(String transacaoId) {
        return StatusPagamento.PENDENTE;
    }
}
