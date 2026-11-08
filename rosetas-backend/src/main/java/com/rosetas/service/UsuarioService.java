package com.rosetas.service;

import com.rosetas.domain.Perfil;
import com.rosetas.domain.Usuario;
import com.rosetas.repository.UsuarioRepository;
import com.rosetas.security.AuthUser;
import com.rosetas.web.ApiException;
import com.rosetas.web.Dtos.CriarUsuarioReq;
import com.rosetas.web.Dtos.UsuarioResp;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Usuários são apenas a equipe (FUNCIONARIA, ADMIN, ROOT). */
@Service
public class UsuarioService {

    private final UsuarioRepository usuarios;

    public UsuarioService(UsuarioRepository usuarios) {
        this.usuarios = usuarios;
    }

    @Transactional(readOnly = true)
    public UsuarioResp me(AuthUser auth) {
        return usuarios.findById(auth.uid()).map(UsuarioResp::from)
                .orElseThrow(() -> ApiException.notFound("Usuário não cadastrado na equipe"));
    }

    // ---------- painel root (RF07) ----------

    @Transactional(readOnly = true)
    public List<UsuarioResp> listar() {
        return usuarios.findAllByOrderByNome().stream().map(UsuarioResp::from).toList();
    }

    /** O id é o uid da conta no Firebase (em dev, qualquer texto, ex.: dev-maria). */
    @Transactional
    public UsuarioResp criar(CriarUsuarioReq req) {
        if (req.perfil() == Perfil.ROOT) {
            throw ApiException.badRequest("O perfil ROOT não pode ser atribuído");
        }
        if (usuarios.existsById(req.id())) {
            throw ApiException.conflict("Já existe um usuário com este id");
        }
        Usuario u = new Usuario();
        u.setId(req.id().trim());
        u.setNome(req.nome().trim());
        u.setEmail(req.email().trim());
        u.setTelefone(req.telefone());
        u.setPerfil(req.perfil());
        u.setAtivo(true);
        return UsuarioResp.from(usuarios.save(u));
    }

    @Transactional
    public UsuarioResp alterarPerfil(String id, Perfil perfil) {
        Usuario u = alteravel(id);
        if (perfil == Perfil.ROOT) {
            throw ApiException.badRequest("O perfil ROOT não pode ser atribuído");
        }
        u.setPerfil(perfil);
        return UsuarioResp.from(u);
    }

    @Transactional
    public UsuarioResp alterarAtivo(String id, boolean ativo) {
        Usuario u = alteravel(id);
        u.setAtivo(ativo);
        return UsuarioResp.from(u);
    }

    private Usuario alteravel(String id) {
        Usuario u = usuarios.findById(id).orElseThrow(() -> ApiException.notFound("Usuário não encontrado"));
        if (u.getPerfil() == Perfil.ROOT) {
            throw ApiException.forbidden("A conta ROOT não pode ser alterada");
        }
        return u;
    }
}
