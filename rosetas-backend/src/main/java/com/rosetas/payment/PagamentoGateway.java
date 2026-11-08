package com.rosetas.payment;

import com.rosetas.domain.StatusPagamento;

import java.math.BigDecimal;

/** Abstração do gateway de pagamento (Pix). Troque a implementação pelo gateway real. */
public interface PagamentoGateway {

    record Cobranca(String transacaoId, String pixCopiaCola) {
    }

    Cobranca criarCobrancaPix(Long pedidoId, BigDecimal valor);

    /** Usado pela rotina de reconciliação (RNF14) quando o webhook não chega. */
    StatusPagamento consultar(String transacaoId);
}
