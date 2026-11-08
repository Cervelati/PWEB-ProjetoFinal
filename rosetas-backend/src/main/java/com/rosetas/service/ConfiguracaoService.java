package com.rosetas.service;

import com.rosetas.domain.Configuracao;
import com.rosetas.repository.ConfiguracaoRepository;
import com.rosetas.web.Dtos.PixConfigReq;
import com.rosetas.web.Dtos.PixConfigResp;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

/** Dados do recebedor do Pix (chave da dona da empresa), editáveis pelo ERP. */
@Service
public class ConfiguracaoService {

    static final String PIX_CHAVE = "pix.chave";
    static final String PIX_NOME = "pix.nome";
    static final String PIX_CIDADE = "pix.cidade";

    static final String PRECO_GRAVACAO_EXTRA = "preco.gravacao_extra";
    /** Adicional de gravação por unidade (lista de preços 2026), usado enquanto não for alterado no ERP. */
    static final BigDecimal GRAVACAO_EXTRA_PADRAO = new BigDecimal("1.20");

    private final ConfiguracaoRepository repo;

    public ConfiguracaoService(ConfiguracaoRepository repo) {
        this.repo = repo;
    }

    @Transactional(readOnly = true)
    public PixConfigResp pix() {
        String chave = valor(PIX_CHAVE).orElse("");
        String nome = valor(PIX_NOME).orElse("");
        String cidade = valor(PIX_CIDADE).orElse("");
        return new PixConfigResp(chave, nome, cidade, !chave.isBlank() && !nome.isBlank() && !cidade.isBlank());
    }

    @Transactional
    public PixConfigResp salvarPix(PixConfigReq req) {
        salvar(PIX_CHAVE, req.chave().trim());
        salvar(PIX_NOME, req.nome().trim());
        salvar(PIX_CIDADE, req.cidade().trim());
        return pix();
    }

    private Optional<String> valor(String chave) {
        return repo.findById(chave).map(Configuracao::getValor);
    }

    private void salvar(String chave, String valor) {
        Configuracao c = repo.findById(chave).orElseGet(() -> new Configuracao(chave, valor));
        c.setValor(valor);
        repo.save(c);
    }

    @Transactional(readOnly = true)
    public BigDecimal precoGravacaoExtra() {
        return valor(PRECO_GRAVACAO_EXTRA).map(BigDecimal::new).orElse(GRAVACAO_EXTRA_PADRAO);
    }

    @Transactional
    public BigDecimal salvarPrecoGravacaoExtra(BigDecimal preco) {
        salvar(PRECO_GRAVACAO_EXTRA, preco.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString());
        return precoGravacaoExtra();
    }
}
