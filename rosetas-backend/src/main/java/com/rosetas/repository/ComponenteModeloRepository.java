package com.rosetas.repository;

import com.rosetas.domain.ComponenteModelo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

public interface ComponenteModeloRepository extends JpaRepository<ComponenteModelo, Long> {

    /** Modelos que hoje não podem ser produzidos por falta (ou inativação) de um material fixo. */
    @Query("select distinct c.modelo.id from ComponenteModelo c "
            + "where c.material.ativo = false or c.material.saldo < c.quantidade")
    Set<Long> modelosSemComponentes();
}
