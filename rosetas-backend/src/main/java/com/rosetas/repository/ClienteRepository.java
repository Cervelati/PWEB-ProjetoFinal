package com.rosetas.repository;

import com.rosetas.domain.Cliente;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Optional<Cliente> findByTelefone(String telefone);

    /** Busca por nome, e-mail ou telefone (parte dos dígitos). Texto vazio lista todos. */
    @Query("""
            select c from Cliente c
            where lower(c.nome) like lower(concat('%', :busca, '%'))
               or lower(coalesce(c.email, '')) like lower(concat('%', :busca, '%'))
               or c.telefone like concat('%', :digitos, '%')
            order by c.nome
            """)
    List<Cliente> buscar(@Param("busca") String busca, @Param("digitos") String digitos, Pageable pageable);
}
