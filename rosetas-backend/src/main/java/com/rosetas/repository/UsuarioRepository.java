package com.rosetas.repository;

import com.rosetas.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UsuarioRepository extends JpaRepository<Usuario, String> {
    List<Usuario> findAllByOrderByNome();
}
