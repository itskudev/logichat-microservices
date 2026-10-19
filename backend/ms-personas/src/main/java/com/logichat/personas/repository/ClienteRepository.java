package com.logichat.personas.repository;

import com.logichat.personas.model.Cliente;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByUsuarioId(Long usuarioId);
    boolean existsByUsuarioId(Long usuarioId);
}
