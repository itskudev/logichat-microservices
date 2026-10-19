package com.logichat.operaciones.repository;

import com.logichat.operaciones.model.Vehiculo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehiculoRepository extends JpaRepository<Vehiculo, Long> {
    boolean existsByPlaca(String placa);
    List<Vehiculo> findByEstado(String estado);
}
