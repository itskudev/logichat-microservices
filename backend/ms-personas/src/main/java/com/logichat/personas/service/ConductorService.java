package com.logichat.personas.service;

import com.logichat.personas.client.AuthClient;
import com.logichat.personas.model.Conductor;
import com.logichat.personas.repository.ConductorRepository;
import com.logichat.personas.utils.BusinessException;
import com.logichat.personas.utils.ModeloNotFoundException;
import feign.FeignException;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ConductorService {

    private final ConductorRepository conductorRepository;
    private final AuthClient authClient;

    public ConductorService(ConductorRepository conductorRepository, AuthClient authClient) {
        this.conductorRepository = conductorRepository;
        this.authClient = authClient;
    }

    public List<Conductor> listar() {
        return conductorRepository.findAll();
    }

    public List<Conductor> listarDisponibles() {
        return conductorRepository.findByEstado("DISPONIBLE");
    }

    public Conductor obtener(Long id) {
        return conductorRepository.findById(id)
                .orElseThrow(() -> new ModeloNotFoundException("Conductor no encontrado"));
    }

    public Conductor obtenerPorUsuarioId(Long usuarioId) {
        return conductorRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new ModeloNotFoundException("No tienes perfil de conductor"));
    }

    public Conductor crear(Long usuarioId, String licencia, String telefono) {
        try {
            authClient.getUsuario(usuarioId);
        } catch (FeignException.NotFound e) {
            throw new ModeloNotFoundException("Usuario no encontrado");
        }
        if (conductorRepository.findByUsuarioId(usuarioId).isPresent()) {
            throw new BusinessException("El usuario ya tiene perfil de conductor");
        }
        if (conductorRepository.existsByLicencia(licencia)) {
            throw new BusinessException("La licencia ya está registrada");
        }
        if (licencia == null || licencia.isBlank()) {
            throw new IllegalArgumentException("Licencia requerida");
        }
        // Promueve el rol en ms-auth (dueño del dato).
        authClient.setRol(usuarioId, Map.of("rol", "CONDUCTOR"));
        Conductor c = new Conductor();
        c.setUsuarioId(usuarioId);
        c.setLicencia(licencia);
        c.setTelefono(telefono);
        c.setEstado("DISPONIBLE");
        return conductorRepository.save(c);
    }

    public Conductor actualizar(Long id, String licencia, String telefono, String estado) {
        Conductor c = obtener(id);
        if (licencia != null && !licencia.isBlank()) c.setLicencia(licencia);
        if (telefono != null) c.setTelefono(telefono);
        if (estado != null && !estado.isBlank()) c.setEstado(estado);
        return conductorRepository.save(c);
    }

    public Conductor cambiarEstado(Long id, String estado) {
        Conductor c = obtener(id);
        c.setEstado(estado);
        return conductorRepository.save(c);
    }

    public void eliminar(Long id) {
        conductorRepository.delete(obtener(id));
    }
}
