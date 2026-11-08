package com.rosetas.repository;

import com.rosetas.domain.ModeloRoseta;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ModeloRepository extends JpaRepository<ModeloRoseta, Long> {

    @EntityGraph(attributePaths = "partes")
    List<ModeloRoseta> findByAtivoTrueOrderByNome();

    @EntityGraph(attributePaths = "partes")
    List<ModeloRoseta> findAllByOrderByNome();

    boolean existsByReferenciaIgnoreCase(String referencia);

    boolean existsByReferenciaIgnoreCaseAndIdNot(String referencia, Long id);
}
