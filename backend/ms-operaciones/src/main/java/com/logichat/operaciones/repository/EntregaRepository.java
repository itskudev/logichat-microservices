package com.logichat.operaciones.repository;

import com.logichat.operaciones.model.Entrega;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EntregaRepository extends JpaRepository<Entrega, Long> {
    Optional<Entrega> findByPedidoId(Long pedidoId);
    List<Entrega> findByConductorId(Long conductorId);
    List<Entrega> findByEstado(String estado);
}
