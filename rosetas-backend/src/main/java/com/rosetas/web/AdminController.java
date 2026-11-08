package com.rosetas.web;

import com.rosetas.domain.StatusPedido;
import com.rosetas.security.AuthUser;
import com.rosetas.service.CatalogoService;
import com.rosetas.service.FotoStorage;
import com.rosetas.service.PedidoService;
import com.rosetas.web.Dtos.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/** Administração (ADMIN e ROOT): pedidos, fitas/estoque e modelos. */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final PedidoService pedidos;
    private final CatalogoService catalogo;
    private final FotoStorage fotos;

    public AdminController(PedidoService pedidos, CatalogoService catalogo, FotoStorage fotos) {
        this.pedidos = pedidos;
        this.catalogo = catalogo;
        this.fotos = fotos;
    }

    // ---------- pedidos (RF05) ----------

    @GetMapping("/pedidos")
    public List<PedidoResp> pedidos(@RequestParam(required = false) StatusPedido status) {
        return pedidos.listarGestao(status);
    }

    @PostMapping("/pedidos/{id}/confirmar-pagamento")
    public PedidoResp confirmarPagamento(@AuthenticationPrincipal AuthUser user, @PathVariable Long id) {
        return pedidos.confirmarPagamento(id, user);
    }

    @PostMapping("/pedidos/{id}/aprovar")
    public PedidoResp aprovar(@AuthenticationPrincipal AuthUser user, @PathVariable Long id) {
        return pedidos.aprovar(id, user);
    }

    @PostMapping("/pedidos/{id}/recusar")
    public PedidoResp recusar(@AuthenticationPrincipal AuthUser user, @PathVariable Long id,
                              @Valid @RequestBody RecusarReq req) {
        return pedidos.recusar(id, req.motivo(), user);
    }

    @PostMapping("/pedidos/{id}/entregar")
    public PedidoResp entregar(@AuthenticationPrincipal AuthUser user, @PathVariable Long id) {
        return pedidos.entregar(id, user);
    }

    // ---------- materiais / estoque (RF04) ----------

    @GetMapping("/materiais")
    public List<MaterialResp> materiais() {
        return catalogo.todosMateriais();
    }

    @PostMapping("/materiais")
    @ResponseStatus(HttpStatus.CREATED)
    public MaterialResp criarMaterial(@Valid @RequestBody MaterialReq req) {
        return catalogo.criarMaterial(req);
    }

    @PutMapping("/materiais/{id}")
    public MaterialResp atualizarMaterial(@PathVariable Long id, @Valid @RequestBody MaterialReq req) {
        return catalogo.atualizarMaterial(id, req);
    }

    @PostMapping("/materiais/{id}/entrada")
    public MaterialResp entrada(@PathVariable Long id, @Valid @RequestBody EntradaEstoqueReq req) {
        return catalogo.entradaEstoque(id, req.quantidade());
    }

    // ---------- modelos / ficha técnica (RF03) ----------

    @GetMapping("/modelos")
    public List<ModeloResp> modelos() {
        return catalogo.todosModelos();
    }

    @PostMapping("/modelos")
    @ResponseStatus(HttpStatus.CREATED)
    public ModeloResp criarModelo(@Valid @RequestBody ModeloReq req) {
        return catalogo.criarModelo(req);
    }

    @PutMapping("/modelos/{id}")
    public ModeloResp atualizarModelo(@PathVariable Long id, @Valid @RequestBody ModeloReq req) {
        return catalogo.atualizarModelo(id, req);
    }

    @PostMapping(value = "/modelos/{id}/foto", consumes = "multipart/form-data")
    public ModeloResp enviarFoto(@PathVariable Long id, @RequestParam("arquivo") MultipartFile arquivo) {
        return catalogo.definirFoto(id, mid -> fotos.salvar(mid, arquivo));
    }

    @DeleteMapping("/modelos/{id}/foto")
    public ModeloResp removerFoto(@PathVariable Long id) {
        return catalogo.definirFoto(id, mid -> null);
    }

    @PutMapping("/modelos/{id}/preco")
    public ModeloResp atualizarPreco(@PathVariable Long id, @Valid @RequestBody PrecoReq req) {
        return catalogo.atualizarPreco(id, req);
    }

    @PostMapping("/modelos/{id}/partes")
    @ResponseStatus(HttpStatus.CREATED)
    public ModeloResp adicionarParte(@PathVariable Long id, @Valid @RequestBody ParteReq req) {
        return catalogo.adicionarParte(id, req);
    }

    @PutMapping("/modelos/partes/{parteId}")
    public ModeloResp atualizarParte(@PathVariable Long parteId, @Valid @RequestBody ParteReq req) {
        return catalogo.atualizarParte(parteId, req);
    }

    @DeleteMapping("/modelos/partes/{parteId}")
    public ModeloResp removerParte(@PathVariable Long parteId) {
        return catalogo.removerParte(parteId);
    }

    @PostMapping("/modelos/{id}/componentes")
    @ResponseStatus(HttpStatus.CREATED)
    public ModeloResp adicionarComponente(@PathVariable Long id, @Valid @RequestBody ComponenteReq req) {
        return catalogo.adicionarComponente(id, req);
    }

    @PutMapping("/modelos/componentes/{componenteId}")
    public ModeloResp atualizarComponente(@PathVariable Long componenteId, @Valid @RequestBody ComponenteReq req) {
        return catalogo.atualizarComponente(componenteId, req);
    }

    @DeleteMapping("/modelos/componentes/{componenteId}")
    public ModeloResp removerComponente(@PathVariable Long componenteId) {
        return catalogo.removerComponente(componenteId);
    }
}
