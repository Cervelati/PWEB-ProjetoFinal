package com.rosetas.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Item de estoque: fita (em metros), botton, argola, alfinete etc. (em unidades).
 * A tabela mantém o nome histórico "fitas".
 */
@Entity
@Table(name = "fitas")
@Getter
@Setter
@NoArgsConstructor
public class Material {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoMaterial tipo = TipoMaterial.FITA;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private UnidadeMaterial unidade = UnidadeMaterial.METRO;

    /** Só fitas têm cor (#RRGGBB). */
    @Column(length = 7)
    private String hex;

    /** Saldo DISPONÍVEL: já desconta o que está reservado para pedidos em aberto. */
    @Column(name = "metros_estoque", nullable = false, precision = 12, scale = 3)
    private BigDecimal saldo = BigDecimal.ZERO;

    @Column(length = 500)
    private String foto;

    @Column(name = "ativa", nullable = false)
    private boolean ativo = true;
}
