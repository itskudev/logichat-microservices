package com.logichat.personas.client;

import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * Feign ms-personas -> ms-auth. El dueño de usuario es auth;
 * personas lo valida y lee por red (token reenviado por FeignConfig).
 */
@FeignClient(name = "ms-auth")
public interface AuthClient {

    @GetMapping("/auth/interno/usuarios/{id}")
    UsuarioRefDTO getUsuario(@PathVariable("id") Long id);

    @PutMapping("/auth/interno/usuarios/{id}/rol")
    UsuarioRefDTO setRol(@PathVariable("id") Long id, @RequestBody Map<String, String> body);
}
