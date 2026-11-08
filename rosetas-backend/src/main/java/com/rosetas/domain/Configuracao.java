package com.rosetas.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Par chave/valor de configuração do sistema, editável pelo ERP. */
@Entity
@Table(name = "configuracoes")
@Getter
@Setter
@NoArgsConstructor
public class Configuracao {

    @Id
    @Column(length = 50)
    private String chave;

    @Column(nullable = false, length = 500)
    private String valor;

    public Configuracao(String chave, String valor) {
        this.chave = chave;
        this.valor = valor;
    }
}
