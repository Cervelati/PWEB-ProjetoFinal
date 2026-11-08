package com.rosetas.repository;

import com.rosetas.domain.MovimentoEstoque;
import com.rosetas.domain.TipoMovimento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovimentoEstoqueRepository extends JpaRepository<MovimentoEstoque, Long> {
    List<MovimentoEstoque> findByPedidoIdAndTipo(Long pedidoId, TipoMovimento tipo);

    List<MovimentoEstoque> findByPedidoId(Long pedidoId);
}
