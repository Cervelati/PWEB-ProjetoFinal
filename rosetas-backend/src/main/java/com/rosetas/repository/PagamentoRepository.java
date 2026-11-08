package com.rosetas.repository;

import com.rosetas.domain.Pagamento;
import com.rosetas.domain.StatusPagamento;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {

    Optional<Pagamento> findFirstByPedidoIdOrderByIdDesc(Long pedidoId);

    List<Pagamento> findByStatus(StatusPagamento status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Pagamento p where p.gatewayTransacaoId = :tx")
    Optional<Pagamento> lockByGatewayTransacaoId(@Param("tx") String tx);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Pagamento p where p.pedido.id = :pedidoId")
    Optional<Pagamento> lockByPedidoId(@Param("pedidoId") Long pedidoId);

    @Query("select p.pedido.id from Pagamento p where p.gatewayTransacaoId = :tx")
    Optional<Long> findPedidoIdByTransacao(@Param("tx") String tx);
}
