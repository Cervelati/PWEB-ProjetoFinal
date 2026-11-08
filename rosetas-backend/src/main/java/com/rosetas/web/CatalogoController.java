package com.rosetas.web;

import com.rosetas.service.CatalogoService;
import com.rosetas.web.Dtos.FitaResp;
import com.rosetas.web.Dtos.OpcoesResp;
import com.rosetas.web.Dtos.ModeloResp;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Catálogo público (RF02). */
@RestController
@RequestMapping("/api/catalogo")
public class CatalogoController {

    private final CatalogoService catalogo;

    public CatalogoController(CatalogoService catalogo) {
        this.catalogo = catalogo;
    }

    @GetMapping("/modelos")
    public List<ModeloResp> modelos() {
        return catalogo.modelosAtivos();
    }

    @GetMapping("/modelos/{id}")
    public ModeloResp modelo(@PathVariable Long id) {
        return catalogo.modeloAtivo(id);
    }

    @GetMapping("/fitas")
    public List<FitaResp> fitas() {
        return catalogo.fitasDisponiveis();
    }

    /** Valores usados na personalização (ex.: adicional de gravação). */
    @GetMapping("/opcoes")
    public OpcoesResp opcoes() {
        return catalogo.opcoes();
    }
}
