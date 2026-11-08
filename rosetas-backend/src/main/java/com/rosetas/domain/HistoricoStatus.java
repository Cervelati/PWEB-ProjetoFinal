package com.rosetas.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity
@Table(name = "historico_status")
@Getter
@Setter
@NoArgsConstructor
public class HistoricoStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private StatusPedido statusAnterior;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatusPedido statusNovo;

    /** uid do usuário, ou "gateway" / "sistema" para ações automáticas. */
    @Column(nullable = false, length = 128)
    private String alteradoPor;

    @Column(nullable = false)
    private OffsetDateTime data;
}
