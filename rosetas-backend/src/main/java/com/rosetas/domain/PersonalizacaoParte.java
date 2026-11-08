package com.rosetas.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "personalizacoes_parte")
@Getter
@Setter
@NoArgsConstructor
public class PersonalizacaoParte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id")
    private ItemPedido item;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "parte_id")
    private ParteModelo parte;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fita_id")
    private Material fita;
}
