package com.logichat.personas.controller;

import com.logichat.personas.client.AuthClient;
import com.logichat.personas.client.UsuarioRefDTO;
import com.logichat.personas.dto.ConductorDTO;
import com.logichat.personas.model.Conductor;
import com.logichat.personas.service.ConductorService;
import com.logichat.personas.utils.ApiResponse;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/personas/conductores")
public class ConductorController {

    private final ConductorService conductorService;
    private final AuthClient authClient;
    private final ModelMapper mapper;

    public ConductorController(ConductorService conductorService, AuthClient authClient, ModelMapper mapper) {
        this.conductorService = conductorService;
        this.authClient = authClient;
        this.mapper = mapper;
    }

    private Map<Long, UsuarioRefDTO> usuarios(List<Conductor> lista) {
        Map<Long, UsuarioRefDTO> map = new HashMap<>();
        lista.stream().map(Conductor::getUsuarioId).distinct().forEach(id -> {
            try { map.put(id, authClient.getUsuario(id)); } catch (Exception ignored) { }
        });
        return map;
    }

    private ConductorDTO toDTO(Conductor c, Map<Long, UsuarioRefDTO> users) {
        ConductorDTO dto = mapper.map(c, ConductorDTO.class);
        UsuarioRefDTO u = users.get(c.getUsuarioId());
        if (u == null) {
            try { u = authClient.getUsuario(c.getUsuarioId()); } catch (Exception ignored) { }
        }
        if (u != null) dto.setNombre(u.nombreCompleto());
        return dto;
    }

    @PreAuthorize("hasRole('CONDUCTOR')")
    @GetMapping("/mio")
    public ResponseEntity<ApiResponse<?>> mio() {
        var auth = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        Long uid = ((Number) auth.getTokenAttributes().get("uid")).longValue();
        ConductorDTO bean = toDTO(conductorService.obtenerPorUsuarioId(uid), new HashMap<>());
        ApiResponse<ConductorDTO> response = new ApiResponse<>(true, "Mi perfil de conductor", bean);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN','COORDINADOR')")
    @GetMapping
    public ResponseEntity<ApiResponse<?>> listar() {
        var lista = conductorService.listar();
        var users = usuarios(lista);
        List<ConductorDTO> dto = lista.stream().map(c -> toDTO(c, users)).toList();
        ApiResponse<List<ConductorDTO>> response = new ApiResponse<>(true, "Listado de conductores", dto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN','COORDINADOR')")
    @GetMapping("/disponibles")
    public ResponseEntity<ApiResponse<?>> disponibles() {
        var lista = conductorService.listarDisponibles();
        var users = usuarios(lista);
        List<ConductorDTO> dto = lista.stream().map(c -> toDTO(c, users)).toList();
        ApiResponse<List<ConductorDTO>> response = new ApiResponse<>(true, "Conductores disponibles", dto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN','COORDINADOR')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> obtener(@PathVariable Long id) {
        ConductorDTO bean = toDTO(conductorService.obtener(id), new HashMap<>());
        ApiResponse<ConductorDTO> response = new ApiResponse<>(true, "Conductor existe", bean);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<?>> crear(@Valid @RequestBody ConductorDTO dto) {
        Conductor c = mapper.map(dto, Conductor.class);
        ConductorDTO bean = toDTO(
                conductorService.crear(c.getUsuarioId(), c.getLicencia(), c.getTelefono()), new HashMap<>());
        ApiResponse<ConductorDTO> response = new ApiResponse<>(true, "Conductor registrado", bean);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> actualizar(@PathVariable Long id, @RequestBody ConductorDTO dto) {
        Conductor c = mapper.map(dto, Conductor.class);
        ConductorDTO bean = toDTO(
                conductorService.actualizar(id, c.getLicencia(), c.getTelefono(), c.getEstado()), new HashMap<>());
        ApiResponse<ConductorDTO> response = new ApiResponse<>(true, "Conductor actualizado", bean);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> eliminar(@PathVariable Long id) {
        conductorService.eliminar(id);
        ApiResponse<Void> response = new ApiResponse<>(true, "Conductor eliminado", null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
