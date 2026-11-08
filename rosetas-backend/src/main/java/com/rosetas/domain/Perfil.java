package com.rosetas.domain;

/** Perfis da equipe. Clientes não têm conta. */
public enum Perfil {
    FUNCIONARIA, ADMIN, ROOT;

    public boolean isGestao() {
        return this == ADMIN || this == ROOT;
    }
}
