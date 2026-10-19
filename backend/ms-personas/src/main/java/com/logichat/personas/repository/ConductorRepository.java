package com.logichat.personas.repository;

import com.logichat.personas.model.Conductor;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConductorRepository extends JpaRepository<Conductor, Long> {
    Optional<Conductor> findByUsuarioId(Long usuarioId);
    boolean existsByLicencia(String licencia);
    List<Conductor> findByEstado(String estado);
}
