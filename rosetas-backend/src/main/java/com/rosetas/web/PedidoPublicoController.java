package com.rosetas.web;

import com.rosetas.service.PedidoService;
import com.rosetas.web.Dtos.CriarPedidoReq;
import com.rosetas.web.Dtos.PedidoPublicoResp;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/** Pedido feito pelo cliente sem login. O acompanhamento é feito pelo código devolvido na criação. */
@RestController
@RequestMapping("/api/publico/pedidos")
public class PedidoPublicoController {

    private final PedidoService pedidos;

    public PedidoPublicoController(PedidoService pedidos) {
        this.pedidos = pedidos;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PedidoPublicoResp criar(@Valid @RequestBody CriarPedidoReq req) {
        return pedidos.criar(req);
    }

    @GetMapping("/{codigo}")
    public PedidoPublicoResp detalhe(@PathVariable String codigo) {
        return pedidos.detalhePublico(codigo);
    }

    @PostMapping("/{codigo}/cancelar")
    public PedidoPublicoResp cancelar(@PathVariable String codigo) {
        return pedidos.cancelarPublico(codigo);
    }
}
