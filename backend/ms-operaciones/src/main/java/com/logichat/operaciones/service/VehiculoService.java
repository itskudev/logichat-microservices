package com.logichat.operaciones.service;

import com.logichat.operaciones.model.Vehiculo;
import com.logichat.operaciones.repository.VehiculoRepository;
import com.logichat.operaciones.utils.BusinessException;
import com.logichat.operaciones.utils.ModeloNotFoundException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class VehiculoService {

    private final VehiculoRepository vehiculoRepository;

    public VehiculoService(VehiculoRepository vehiculoRepository) {
        this.vehiculoRepository = vehiculoRepository;
    }

    public List<Vehiculo> listar() {
        return vehiculoRepository.findAll();
    }

    public List<Vehiculo> listarDisponibles() {
        return vehiculoRepository.findByEstado("DISPONIBLE");
    }

    public Vehiculo obtener(Long id) {
        return vehiculoRepository.findById(id)
                .orElseThrow(() -> new ModeloNotFoundException("Vehículo no encontrado"));
    }

    public Vehiculo crear(String placa, String tipo, String marca, String modelo) {
        if (placa == null || placa.isBlank()) throw new IllegalArgumentException("Placa requerida");
        if (tipo == null || tipo.isBlank()) throw new IllegalArgumentException("Tipo requerido");
        if (vehiculoRepository.existsByPlaca(placa)) throw new BusinessException("La placa ya está registrada");
        Vehiculo v = new Vehiculo();
        v.setPlaca(placa);
        v.setTipo(tipo);
        v.setMarca(marca);
        v.setModelo(modelo);
        v.setEstado("DISPONIBLE");
        return vehiculoRepository.save(v);
    }

    public Vehiculo actualizar(Long id, String placa, String tipo, String marca, String modelo, String estado) {
        Vehiculo v = obtener(id);
        if (placa != null && !placa.isBlank()) v.setPlaca(placa);
        if (tipo != null && !tipo.isBlank()) v.setTipo(tipo);
        if (marca != null) v.setMarca(marca);
        if (modelo != null) v.setModelo(modelo);
        if (estado != null && !estado.isBlank()) v.setEstado(estado);
        return vehiculoRepository.save(v);
    }

    public void eliminar(Long id) {
        vehiculoRepository.delete(obtener(id));
    }
}
