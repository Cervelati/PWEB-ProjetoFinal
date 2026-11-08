package com.rosetas.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.List;

@Entity
@Table(name = "modelos_roseta")
@Getter
@Setter
@NoArgsConstructor
public class ModeloRoseta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Código da lista de preços (ex.: 12.30.3 T). */
    @Column(length = 40)
    private String referencia;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(length = 1000)
    private String descricao;

    /** Seção da lista (1 volta, 2 voltas, Especiais, Grande, Diversos...). */
    @Column(length = 40)
    private String categoria;

    /** estrela, esp., esp.D, esp.E, L, XL, XXL... */
    @Column(length = 20)
    private String formato;

    @Column(precision = 5, scale = 1)
    private BigDecimal diametroCm;

    /** Nulo quando a lista não informa as voltas. */
    private Integer voltas;

    @Column(nullable = false)
    private int rabiolaQtd;

    @Column(precision = 6, scale = 1)
    private BigDecimal rabiolaComprimentoCm;

    @Column(precision = 4, scale = 1)
    private BigDecimal larguraFitaCm;

    /** Preço base. Para modelos com franzido é o menor dos preços ("a partir de"). */
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal preco;

    /** Preço do franzido americano; nulo = não oferecido. */
    @Column(precision = 12, scale = 2)
    private BigDecimal precoAmericano;

    /** Preço do franzido normal; nulo = não oferecido. */
    @Column(precision = 12, scale = 2)
    private BigDecimal precoNormal;

    /** Gravações já incluídas no preço; as demais são cobradas à parte. */
    @Column(nullable = false)
    private int gravacoesInclusas;

    @Column(length = 500)
    private String imagemBase;

    @Column(nullable = false)
    private boolean ativo = true;

    @OneToMany(mappedBy = "modelo", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id")
    private List<ParteModelo> partes = new ArrayList<>();

    // Carregado em lote (1 consulta para vários modelos); evita juntar duas coleções no mesmo SELECT.
    @OneToMany(mappedBy = "modelo", cascade = CascadeType.ALL, orphanRemoval = true)
    @org.hibernate.annotations.BatchSize(size = 200)
    private Set<ComponenteModelo> componentes = new LinkedHashSet<>();

    public boolean temFranzido() {
        return precoAmericano != null || precoNormal != null;
    }
}
