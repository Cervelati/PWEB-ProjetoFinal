package com.rosetas.repository;

import com.rosetas.domain.HistoricoStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistoricoStatusRepository extends JpaRepository<HistoricoStatus, Long> {
    List<HistoricoStatus> findByPedidoIdOrderByDataAscIdAsc(Long pedidoId);
}
