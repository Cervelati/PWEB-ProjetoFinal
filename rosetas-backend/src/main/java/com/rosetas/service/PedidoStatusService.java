package com.rosetas.service;

import com.rosetas.domain.*;
import com.rosetas.repository.HistoricoStatusRepository;
import com.rosetas.repository.NotificacaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

/** Único ponto de mudança de status: grava histórico e enfileira a notificação. */
@Service
public class PedidoStatusService {

    private final HistoricoStatusRepository historicos;
    private final NotificacaoRepository notificacoes;

    public PedidoStatusService(HistoricoStatusRepository historicos, NotificacaoRepository notificacoes) {
        this.historicos = historicos;
        this.notificacoes = notificacoes;
    }

    /** Primeiro registro do pedido (status anterior nulo). */
    @Transactional
    public void registrarCriacao(Pedido pedido, String quem) {
        registrar(pedido, null, pedido.getStatus(), quem);
    }

    @Transactional
    public void transicionar(Pedido pedido, StatusPedido novo, String quem) {
        StatusPedido anterior = pedido.getStatus();
        pedido.setStatus(novo);
        registrar(pedido, anterior, novo, quem);
    }

    private void registrar(Pedido pedido, StatusPedido anterior, StatusPedido novo, String quem) {
        HistoricoStatus h = new HistoricoStatus();
        h.setPedido(pedido);
        h.setStatusAnterior(anterior);
        h.setStatusNovo(novo);
        h.setAlteradoPor(quem);
        h.setData(OffsetDateTime.now());
        historicos.save(h);

        Notificacao n = new Notificacao();
        n.setPedido(pedido);
        n.setCanal(CanalNotificacao.EMAIL);
        n.setTemplate("PEDIDO_" + novo.name());
        n.setStatusEnvio(StatusEnvio.PENDENTE);
        n.setData(OffsetDateTime.now());
        notificacoes.save(n);
    }
}
