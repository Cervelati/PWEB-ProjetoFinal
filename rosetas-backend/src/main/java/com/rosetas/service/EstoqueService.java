package com.rosetas.service;

import com.rosetas.domain.*;
import com.rosetas.repository.MaterialRepository;
import com.rosetas.repository.MovimentoEstoqueRepository;
import com.rosetas.web.ApiException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Modelo de estoque: {@code Material.saldo} é o saldo disponível (metros para fitas, unidades para os demais).
 * ENTRADA soma; RESERVA subtrai (pedido criado); ESTORNO devolve a reserva (pedido cancelado);
 * BAIXA registra o consumo definitivo na aprovação (o saldo já foi descontado na reserva).
 *
 * Concorrência (RNF12): todo débito/crédito é um UPDATE atômico no banco (saldo = saldo - n ... where saldo >= n),
 * aplicado em ordem crescente de id (evita deadlock). Não se confia no saldo em memória.
 * BAIXA e ESTORNO são idempotentes: repetir a chamada não duplica o efeito.
 */
@Service
public class EstoqueService {

    private final MaterialRepository materiais;
    private final MovimentoEstoqueRepository movimentos;

    public EstoqueService(MaterialRepository materiais, MovimentoEstoqueRepository movimentos) {
        this.materiais = materiais;
        this.movimentos = movimentos;
    }

    @Transactional
    public void entrada(Long materialId, BigDecimal quantidade) {
        if (!materiais.existsById(materialId)) {
            throw ApiException.notFound("Material não encontrado");
        }
        materiais.creditar(materialId, quantidade);
        registrar(materialId, quantidade, TipoMovimento.ENTRADA, null);
    }

    /** Reserva tudo ou nada: se faltar qualquer material, lança 409 e a transação inteira é desfeita. */
    @Transactional
    public void reservar(Pedido pedido, Map<Long, BigDecimal> necessidade) {
        for (Map.Entry<Long, BigDecimal> e : new TreeMap<>(necessidade).entrySet()) {
            if (materiais.debitar(e.getKey(), e.getValue()) == 0) {
                String nome = materiais.findById(e.getKey()).map(Material::getNome).orElse("#" + e.getKey());
                throw ApiException.conflict("Estoque insuficiente de '" + nome + "'");
            }
            registrar(e.getKey(), e.getValue(), TipoMovimento.RESERVA, pedido);
        }
    }

    @Transactional
    public void baixar(Pedido pedido) {
        List<MovimentoEstoque> movs = movimentos.findByPedidoId(pedido.getId());
        if (movs.stream().anyMatch(m -> m.getTipo() == TipoMovimento.BAIXA)) {
            return;
        }
        for (MovimentoEstoque r : movs) {
            if (r.getTipo() == TipoMovimento.RESERVA) {
                registrar(r.getMaterial().getId(), r.getQuantidade(), TipoMovimento.BAIXA, pedido);
            }
        }
    }

    /** Devolve ao saldo o que ainda está reservado para o pedido (reservas menos estornos já feitos). */
    @Transactional
    public void estornar(Pedido pedido) {
        Map<Long, BigDecimal> pendente = new TreeMap<>();
        for (MovimentoEstoque m : movimentos.findByPedidoId(pedido.getId())) {
            if (m.getTipo() == TipoMovimento.RESERVA) {
                pendente.merge(m.getMaterial().getId(), m.getQuantidade(), BigDecimal::add);
            } else if (m.getTipo() == TipoMovimento.ESTORNO) {
                pendente.merge(m.getMaterial().getId(), m.getQuantidade().negate(), BigDecimal::add);
            }
        }
        for (Map.Entry<Long, BigDecimal> e : pendente.entrySet()) {
            if (e.getValue().signum() > 0) {
                materiais.creditar(e.getKey(), e.getValue());
                registrar(e.getKey(), e.getValue(), TipoMovimento.ESTORNO, pedido);
            }
        }
    }

    private void registrar(Long materialId, BigDecimal qtd, TipoMovimento tipo, Pedido pedido) {
        MovimentoEstoque m = new MovimentoEstoque();
        m.setMaterial(materiais.getReferenceById(materialId));
        m.setQuantidade(qtd);
        m.setTipo(tipo);
        m.setPedido(pedido);
        m.setData(OffsetDateTime.now());
        movimentos.save(m);
    }
}
