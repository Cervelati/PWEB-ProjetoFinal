package com.rosetas.web;

import com.rosetas.service.PedidoService;
import com.rosetas.web.Dtos.FichaResp;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Visão da equipe: apenas fichas de produção, sem valores financeiros. */
@RestController
@RequestMapping("/api/producao")
public class ProducaoController {

    private final PedidoService pedidos;

    public ProducaoController(PedidoService pedidos) {
        this.pedidos = pedidos;
    }

    @GetMapping("/pedidos")
    public List<FichaResp> emProducao() {
        return pedidos.fichasEmProducao();
    }

    @GetMapping("/pedidos/{id}")
    public FichaResp ficha(@PathVariable Long id) {
        return pedidos.ficha(id);
    }
}
