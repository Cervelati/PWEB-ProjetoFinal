package com.rosetas.security;

import com.rosetas.domain.Perfil;

/**
 * Principal da requisição. {@code perfil} é null quando o token é válido mas o usuário
 * ainda não completou o cadastro (POST /api/usuarios/me) ou está inativo.
 */
public record AuthUser(String uid, String email, Perfil perfil) {

    public boolean isGestao() {
        return perfil != null && perfil.isGestao();
    }
}
