package com.logichat.operaciones.controller;

import com.logichat.operaciones.dto.VehiculoDTO;
import com.logichat.operaciones.model.Vehiculo;
import com.logichat.operaciones.service.VehiculoService;
import com.logichat.operaciones.utils.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/operaciones/vehiculos")
public class VehiculoController {

    private final VehiculoService vehiculoService;
    private final ModelMapper mapper;

    public VehiculoController(VehiculoService vehiculoService, ModelMapper mapper) {
        this.vehiculoService = vehiculoService;
        this.mapper = mapper;
    }

    @PreAuthorize("hasAnyRole('ADMIN','COORDINADOR','CONDUCTOR')")
    @GetMapping
    public ResponseEntity<ApiResponse<?>> listar() {
        List<VehiculoDTO> lista = vehiculoService.listar().stream()
                .map(v -> mapper.map(v, VehiculoDTO.class)).toList();
        ApiResponse<List<VehiculoDTO>> response = new ApiResponse<>(true, "Listado de vehículos", lista);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN','COORDINADOR','CONDUCTOR')")
    @GetMapping("/disponibles")
    public ResponseEntity<ApiResponse<?>> disponibles() {
        List<VehiculoDTO> lista = vehiculoService.listarDisponibles().stream()
                .map(v -> mapper.map(v, VehiculoDTO.class)).toList();
        ApiResponse<List<VehiculoDTO>> response = new ApiResponse<>(true, "Vehículos disponibles", lista);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN','COORDINADOR','CONDUCTOR')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> obtener(@PathVariable Long id) {
        VehiculoDTO bean = mapper.map(vehiculoService.obtener(id), VehiculoDTO.class);
        ApiResponse<VehiculoDTO> response = new ApiResponse<>(true, "Vehículo existe", bean);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN','COORDINADOR')")
    @PostMapping
    public ResponseEntity<ApiResponse<?>> crear(@Valid @RequestBody VehiculoDTO dto) {
        Vehiculo v = mapper.map(dto, Vehiculo.class);
        VehiculoDTO bean = mapper.map(
                vehiculoService.crear(v.getPlaca(), v.getTipo(), v.getMarca(), v.getModelo()), VehiculoDTO.class);
        ApiResponse<VehiculoDTO> response = new ApiResponse<>(true, "Vehículo registrado", bean);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('ADMIN','COORDINADOR')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> actualizar(@PathVariable Long id, @RequestBody VehiculoDTO dto) {
        Vehiculo v = mapper.map(dto, Vehiculo.class);
        VehiculoDTO bean = mapper.map(vehiculoService.actualizar(
                id, v.getPlaca(), v.getTipo(), v.getMarca(), v.getModelo(), v.getEstado()), VehiculoDTO.class);
        ApiResponse<VehiculoDTO> response = new ApiResponse<>(true, "Vehículo actualizado", bean);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN','COORDINADOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> eliminar(@PathVariable Long id) {
        vehiculoService.eliminar(id);
        ApiResponse<Void> response = new ApiResponse<>(true, "Vehículo eliminado", null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
