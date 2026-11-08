package com.rosetas.web;

import com.rosetas.service.PagamentoService;
import com.rosetas.web.Dtos.WebhookPagamentoReq;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * Endpoint chamado pelo gateway. Aqui a autenticação é um segredo compartilhado no header
 * X-Webhook-Secret; ao integrar um gateway real, valide a assinatura oficial dele.
 */
@RestController
@RequestMapping("/api/pagamentos")
public class PagamentoWebhookController {

    private final PagamentoService pagamentos;
    private final byte[] segredo;

    public PagamentoWebhookController(PagamentoService pagamentos,
                                      @Value("${app.pagamento.webhook-secret}") String segredo) {
        this.pagamentos = pagamentos;
        this.segredo = segredo.getBytes(StandardCharsets.UTF_8);
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> webhook(@RequestHeader(value = "X-Webhook-Secret", required = false) String header,
                                        @Valid @RequestBody WebhookPagamentoReq req) {
        if (header == null || !MessageDigest.isEqual(segredo, header.getBytes(StandardCharsets.UTF_8))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        pagamentos.processar(req.gatewayTransacaoId(), req.status(), "gateway");
        return ResponseEntity.noContent().build();
    }
}
