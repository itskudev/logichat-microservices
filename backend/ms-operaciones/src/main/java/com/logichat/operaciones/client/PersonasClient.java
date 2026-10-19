package com.logichat.operaciones.client;

import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * Feign obligatorio del proyecto: ms-operaciones -> ms-personas.
 * ms-personas expone estos paths en InternoController.
 * Sin auth por ahora. TODO(security): propagar JWT con RequestInterceptor.
 */
@FeignClient(name = "ms-personas")
public interface PersonasClient {

    @GetMapping("/personas/interno/clientes/{id}")
    ClienteRefDTO getCliente(@PathVariable("id") Long id);

    @GetMapping("/personas/interno/conductores/{id}")
    ConductorRefDTO getConductor(@PathVariable("id") Long id);

    @PutMapping("/personas/interno/conductores/{id}/estado")
    ConductorRefDTO setEstadoConductor(@PathVariable("id") Long id, @RequestBody Map<String, String> body);
}
