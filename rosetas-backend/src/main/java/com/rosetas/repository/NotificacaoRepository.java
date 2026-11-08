package com.rosetas.repository;

import com.rosetas.domain.Notificacao;
import com.rosetas.domain.StatusEnvio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificacaoRepository extends JpaRepository<Notificacao, Long> {
    List<Notificacao> findByStatusEnvioOrderByData(StatusEnvio statusEnvio);
}
