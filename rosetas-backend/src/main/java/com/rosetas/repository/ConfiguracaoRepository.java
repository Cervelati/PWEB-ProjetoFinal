package com.rosetas.repository;

import com.rosetas.domain.Configuracao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfiguracaoRepository extends JpaRepository<Configuracao, String> {
}
