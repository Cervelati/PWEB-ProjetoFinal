package com.rosetas.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

/** Cadastro de contato do cliente (RF01). Não é uma conta de acesso. */
@Entity
@Table(name = "clientes")
@Getter
@Setter
@NoArgsConstructor
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nome;

    /** Somente dígitos, com DDD. Identifica o cliente entre pedidos. */
    @Column(nullable = false, unique = true, length = 20)
    private String telefone;

    @Column(length = 255)
    private String email;

    @Column(nullable = false)
    private OffsetDateTime criadoEm;
}
