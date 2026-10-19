package com.logichat.personas.controller;

import com.logichat.personas.dto.ClienteDTO;
import com.logichat.personas.dto.ConductorDTO;
import com.logichat.personas.model.Cliente;
import com.logichat.personas.model.Conductor;
import com.logichat.personas.service.ClienteService;
import com.logichat.personas.service.ConductorService;
import java.util.Map;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Endpoints internos para Feign de ms-operaciones y ms-auth.
 * Respuesta cruda como en el ejemplo del profe (Feign consume sin ApiResponse).
 */
@RestController
@RequestMapping("/personas/interno")
public class InternoController {

    private final ClienteService clienteService;
    private final ConductorService conductorService;
    private final ModelMapper mapper;

    public InternoController(ClienteService clienteService, ConductorService conductorService, ModelMapper mapper) {
        this.clienteService = clienteService;
        this.conductorService = conductorService;
        this.mapper = mapper;
    }

    @GetMapping("/clientes/{id}")
    public ResponseEntity<ClienteDTO> cliente(@PathVariable Long id) {
        Cliente c = clienteService.obtener(id);
        return ResponseEntity.ok(mapper.map(c, ClienteDTO.class));
    }

    @GetMapping("/conductores/{id}")
    public ResponseEntity<ConductorDTO> conductor(@PathVariable Long id) {
        Conductor c = conductorService.obtener(id);
        return ResponseEntity.ok(mapper.map(c, ConductorDTO.class));
    }

    @PutMapping("/conductores/{id}/estado")
    public ResponseEntity<ConductorDTO> estadoConductor(@PathVariable Long id, @RequestBody Map<String, String> body) {
        Conductor c = conductorService.cambiarEstado(id, body.get("estado"));
        return ResponseEntity.ok(mapper.map(c, ConductorDTO.class));
    }
}
