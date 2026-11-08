package com.rosetas.web;

import com.rosetas.service.ClienteService;
import com.rosetas.service.PedidoService;
import com.rosetas.web.Dtos.ClienteReq;
import com.rosetas.web.Dtos.ClienteResp;
import com.rosetas.web.Dtos.PedidoResp;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Gestão de clientes (RF01): consulta, edição e histórico de compras. */
@RestController
@RequestMapping("/api/admin/clientes")
public class ClienteController {

    private final ClienteService clientes;
    private final PedidoService pedidos;

    public ClienteController(ClienteService clientes, PedidoService pedidos) {
        this.clientes = clientes;
        this.pedidos = pedidos;
    }

    @GetMapping
    public List<ClienteResp> buscar(@RequestParam(required = false) String busca) {
        return clientes.buscar(busca);
    }

    @PutMapping("/{id}")
    public ClienteResp atualizar(@PathVariable Long id, @Valid @RequestBody ClienteReq req) {
        return clientes.atualizar(id, req);
    }

    @GetMapping("/{id}/pedidos")
    public List<PedidoResp> pedidos(@PathVariable Long id) {
        return pedidos.pedidosDoCliente(id);
    }
}
