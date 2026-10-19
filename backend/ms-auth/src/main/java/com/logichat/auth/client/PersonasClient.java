package com.logichat.auth.client;

import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * Llamada server-to-server ms-auth -> ms-personas (opción A: register autocrea perfil).
 * El token se pasa explícito porque en /register no hay request entrante con Authorization.
 */
@FeignClient(name = "ms-personas")
public interface PersonasClient {

    @PostMapping("/personas/clientes")
    Map<String, Object> crearCliente(@RequestHeader("Authorization") String bearer,
                                     @RequestBody Map<String, Object> body);

    @PostMapping("/personas/conductores")
    Map<String, Object> crearConductor(@RequestHeader("Authorization") String bearer,
                                       @RequestBody Map<String, Object> body);
}
