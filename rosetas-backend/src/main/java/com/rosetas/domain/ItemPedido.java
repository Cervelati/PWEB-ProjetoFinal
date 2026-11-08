package com.rosetas.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "itens_pedido")
@Getter
@Setter
@NoArgsConstructor
public class ItemPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "modelo_id")
    private ModeloRoseta modelo;

    @Column(nullable = false)
    private int quantidade;

    @Column(length = 255)
    private String textoPersonalizado;

    /** Nulo em modelos sem franzido (ex.: fitas e bottons avulsos). */
    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Franzido franzido;

    @Column(nullable = false)
    private int gravacoesExtras;

    /** Preço cobrado por unidade no momento do pedido (franzido + gravações extras). */
    @Column(precision = 12, scale = 2)
    private BigDecimal precoUnitario;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id")
    private List<PersonalizacaoParte> personalizacoes = new ArrayList<>();
}
