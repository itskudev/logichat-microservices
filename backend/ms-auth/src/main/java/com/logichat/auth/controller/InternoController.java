package com.logichat.auth.controller;

import com.logichat.auth.dto.UsuarioResponse;
import com.logichat.auth.model.Usuario;
import com.logichat.auth.service.UsuarioService;
import java.util.Map;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Endpoints internos para Feign de ms-personas (valida/leer usuarios).
 * Respuesta cruda como en el ejemplo del profe (Feign consume sin ApiResponse).
 */
@RestController
@RequestMapping("/auth/interno")
public class InternoController {

    private final UsuarioService usuarioService;
    private final ModelMapper mapper;

    public InternoController(UsuarioService usuarioService, ModelMapper mapper) {
        this.usuarioService = usuarioService;
        this.mapper = mapper;
    }

    private UsuarioResponse toDTO(Usuario usuario) {
        UsuarioResponse dto = mapper.map(usuario, UsuarioResponse.class);
        dto.setRol(usuario.getRol() != null ? usuario.getRol().getNombre() : null);
        return dto;
    }

    @GetMapping("/usuarios/{id}")
    public ResponseEntity<UsuarioResponse> usuario(@PathVariable Long id) {
        return ResponseEntity.ok(toDTO(usuarioService.obtener(id)));
    }

    @PutMapping("/usuarios/{id}/rol")
    public ResponseEntity<UsuarioResponse> rol(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(toDTO(usuarioService.cambiarRol(id, body.get("rol"))));
    }
}
