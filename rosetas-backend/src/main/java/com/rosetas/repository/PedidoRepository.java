package com.rosetas.repository;

import com.rosetas.domain.Pedido;
import com.rosetas.domain.StatusPedido;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByClienteIdOrderByDataDesc(Long clienteId);

    Optional<Pedido> findByCodigoAcompanhamento(String codigoAcompanhamento);

    long countByClienteIdAndStatus(Long clienteId, StatusPedido status);

    List<Pedido> findByStatusOrderByData(StatusPedido status);

    List<Pedido> findAllByOrderByDataDesc();

    @Query("select p.id from Pedido p where p.status = :status and p.data < :limite")
    List<Long> findIdsByStatusAndDataBefore(@Param("status") StatusPedido status,
                                            @Param("limite") OffsetDateTime limite);

    /**
     * Trava o pedido (SELECT ... FOR UPDATE) antes de qualquer mudança de status ou de estoque.
     * Ordem de locks do sistema: pedido -> pagamento -> materiais (por id). Use como PRIMEIRA leitura do pedido
     * na transação, para não trabalhar com uma cópia em memória desatualizada.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Pedido p where p.id = :id")
    Optional<Pedido> lockById(@Param("id") Long id);

    @Query("select p.id from Pedido p where p.codigoAcompanhamento = :codigo")
    Optional<Long> findIdByCodigo(@Param("codigo") String codigo);
}
