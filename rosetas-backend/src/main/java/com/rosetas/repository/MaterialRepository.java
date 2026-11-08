package com.rosetas.repository;

import com.rosetas.domain.Material;
import com.rosetas.domain.TipoMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface MaterialRepository extends JpaRepository<Material, Long> {

    List<Material> findByTipoAndAtivoTrueAndSaldoGreaterThanOrderByNome(TipoMaterial tipo, BigDecimal minimo);

    List<Material> findAllByOrderByTipoAscNomeAsc();

    /**
     * Débito ATÔMICO no banco (RNF12): só desconta se houver saldo, e a própria linha fica travada até o commit.
     * Não depende do valor que está em memória, então pedidos simultâneos nunca consomem além do disponível.
     * Retorna 0 linhas quando o saldo é insuficiente (ou o material está inativo).
     */
    @Modifying(flushAutomatically = true)
    @Query("update Material m set m.saldo = m.saldo - :qtd where m.id = :id and m.ativo = true and m.saldo >= :qtd")
    int debitar(@Param("id") Long id, @Param("qtd") BigDecimal qtd);

    @Modifying(flushAutomatically = true)
    @Query("update Material m set m.saldo = m.saldo + :qtd where m.id = :id")
    int creditar(@Param("id") Long id, @Param("qtd") BigDecimal qtd);
}
