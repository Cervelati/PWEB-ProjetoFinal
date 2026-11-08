package com.rosetas.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
public class Usuario {

    /** uid do Firebase. */
    @Id
    @Column(length = 128)
    private String id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(length = 30)
    private String telefone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Perfil perfil;

    @Column(nullable = false)
    private boolean ativo = true;
}
