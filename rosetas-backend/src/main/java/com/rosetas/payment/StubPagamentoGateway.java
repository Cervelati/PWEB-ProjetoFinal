package com.rosetas.payment;

import com.rosetas.domain.StatusPagamento;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Implementação de desenvolvimento: gera uma transação fictícia e nunca confirma sozinha.
 * Para testar, chame POST /api/pagamentos/webhook simulando o gateway.
 */
@Component
public class StubPagamentoGateway implements PagamentoGateway {

    @Override
    public Cobranca criarCobrancaPix(Long pedidoId, BigDecimal valor) {
        String tx = "stub-" + UUID.randomUUID();
        return new Cobranca(tx, "PIX-STUB-" + tx);
    }

    @Override
    public StatusPagamento consultar(String transacaoId) {
        return StatusPagamento.PENDENTE;
    }
}
