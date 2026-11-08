package com.rosetas.web;

import com.rosetas.service.ConfiguracaoService;
import com.rosetas.web.Dtos.OpcoesResp;
import com.rosetas.web.Dtos.PixConfigReq;
import com.rosetas.web.Dtos.PrecoGravacaoReq;
import com.rosetas.web.Dtos.PixConfigResp;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/** Configuração da chave Pix da empresa (ADMIN e ROOT). */
@RestController
@RequestMapping("/api/admin/configuracoes")
public class ConfiguracaoController {

    private final ConfiguracaoService config;

    public ConfiguracaoController(ConfiguracaoService config) {
        this.config = config;
    }

    @GetMapping("/pix")
    public PixConfigResp pix() {
        return config.pix();
    }

    @PutMapping("/pix")
    public PixConfigResp salvarPix(@Valid @RequestBody PixConfigReq req) {
        return config.salvarPix(req);
    }

    @GetMapping("/gravacao")
    public OpcoesResp gravacao() {
        return new OpcoesResp(config.precoGravacaoExtra());
    }

    @PutMapping("/gravacao")
    public OpcoesResp salvarGravacao(@Valid @RequestBody PrecoGravacaoReq req) {
        return new OpcoesResp(config.salvarPrecoGravacaoExtra(req.preco()));
    }
}
