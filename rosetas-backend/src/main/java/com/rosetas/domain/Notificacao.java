package com.rosetas.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity
@Table(name = "notificacoes")
@Getter
@Setter
@NoArgsConstructor
public class Notificacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CanalNotificacao canal;

    @Column(nullable = false, length = 80)
    private String template;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusEnvio statusEnvio;

    @Column(nullable = false)
    private OffsetDateTime data;
}
