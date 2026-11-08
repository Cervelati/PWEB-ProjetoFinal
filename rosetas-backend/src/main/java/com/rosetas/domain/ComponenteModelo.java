package com.rosetas.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/** Material fixo consumido por unidade do modelo (ex.: 1 botton). Não é escolhido pelo cliente. */
@Entity
@Table(name = "componentes_modelo")
@Getter
@Setter
@NoArgsConstructor
public class ComponenteModelo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "modelo_id")
    private ModeloRoseta modelo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "material_id")
    private Material material;

    @Column(nullable = false, precision = 10, scale = 3)
    private BigDecimal quantidade;
}
