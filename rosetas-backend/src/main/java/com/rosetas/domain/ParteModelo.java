package com.rosetas.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "partes_modelo")
@Getter
@Setter
@NoArgsConstructor
public class ParteModelo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "modelo_id")
    private ModeloRoseta modelo;

    /** Ex.: fita externa, fita interna, pontas. */
    @Column(nullable = false, length = 100)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private GrupoParte grupo = GrupoParte.CORPO;

    @Column(nullable = false, precision = 10, scale = 3)
    private BigDecimal metrosConsumidos;
}
