package com.rosetas.web;

import com.rosetas.security.AuthUser;
import com.rosetas.service.UsuarioService;
import com.rosetas.web.Dtos.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UsuarioController {

    private final UsuarioService usuarios;

    public UsuarioController(UsuarioService usuarios) {
        this.usuarios = usuarios;
    }

    @GetMapping("/api/usuarios/me")
    public UsuarioResp me(@AuthenticationPrincipal AuthUser user) {
        return usuarios.me(user);
    }

    // ---------- painel root (RF07) ----------

    @GetMapping("/api/admin/usuarios")
    public List<UsuarioResp> listar() {
        return usuarios.listar();
    }

    @PostMapping("/api/admin/usuarios")
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResp criar(@Valid @RequestBody CriarUsuarioReq req) {
        return usuarios.criar(req);
    }

    @PatchMapping("/api/admin/usuarios/{id}/perfil")
    public UsuarioResp perfil(@PathVariable String id, @Valid @RequestBody PerfilReq req) {
        return usuarios.alterarPerfil(id, req.perfil());
    }

    @PatchMapping("/api/admin/usuarios/{id}/ativo")
    public UsuarioResp ativo(@PathVariable String id, @RequestBody AtivoReq req) {
        return usuarios.alterarAtivo(id, req.ativo());
    }
}
