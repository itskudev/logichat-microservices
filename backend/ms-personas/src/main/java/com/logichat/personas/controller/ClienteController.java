package com.logichat.personas.controller;

import com.logichat.personas.client.AuthClient;
import com.logichat.personas.client.UsuarioRefDTO;
import com.logichat.personas.dto.ClienteDTO;
import com.logichat.personas.model.Cliente;
import com.logichat.personas.service.ClienteService;
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
@RequestMapping("/personas/clientes")
public class ClienteController {

    private final ClienteService clienteService;
    private final AuthClient authClient;
    private final ModelMapper mapper;

    public ClienteController(ClienteService clienteService, AuthClient authClient, ModelMapper mapper) {
        this.clienteService = clienteService;
        this.authClient = authClient;
        this.mapper = mapper;
    }

    private Map<Long, UsuarioRefDTO> usuarios(List<Cliente> lista) {
        Map<Long, UsuarioRefDTO> map = new HashMap<>();
        lista.stream().map(Cliente::getUsuarioId).distinct().forEach(id -> {
            try { map.put(id, authClient.getUsuario(id)); } catch (Exception ignored) { }
        });
        return map;
    }

    private ClienteDTO toDTO(Cliente c, Map<Long, UsuarioRefDTO> users) {
        ClienteDTO dto = mapper.map(c, ClienteDTO.class);
        UsuarioRefDTO u = users.get(c.getUsuarioId());
        if (u == null) {
            try { u = authClient.getUsuario(c.getUsuarioId()); } catch (Exception ignored) { }
        }
        if (u != null) {
            dto.setNombre(u.nombreCompleto());
            dto.setEmail(u.getEmail());
        }
        return dto;
    }

    @PreAuthorize("hasRole('CLIENTE')")
    @GetMapping("/mio")
    public ResponseEntity<ApiResponse<?>> mio() {
        var auth = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        Long uid = ((Number) auth.getTokenAttributes().get("uid")).longValue();
        ClienteDTO bean = toDTO(clienteService.obtenerPorUsuarioId(uid), new HashMap<>());
        ApiResponse<ClienteDTO> response = new ApiResponse<>(true, "Mi perfil de cliente", bean);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN','COORDINADOR')")
    @GetMapping
    public ResponseEntity<ApiResponse<?>> listar() {
        var lista = clienteService.listar();
        var users = usuarios(lista);
        List<ClienteDTO> dto = lista.stream().map(c -> toDTO(c, users)).toList();
        ApiResponse<List<ClienteDTO>> response = new ApiResponse<>(true, "Listado de clientes", dto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN','COORDINADOR')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> obtener(@PathVariable Long id) {
        ClienteDTO bean = toDTO(clienteService.obtener(id), new HashMap<>());
        ApiResponse<ClienteDTO> response = new ApiResponse<>(true, "Cliente existe", bean);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<?>> crear(@Valid @RequestBody ClienteDTO dto) {
        Cliente c = mapper.map(dto, Cliente.class);
        ClienteDTO bean = toDTO(
                clienteService.crear(c.getUsuarioId(), c.getTelefono(), c.getDireccion()), new HashMap<>());
        ApiResponse<ClienteDTO> response = new ApiResponse<>(true, "Cliente registrado", bean);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> actualizar(@PathVariable Long id, @RequestBody ClienteDTO dto) {
        Cliente c = mapper.map(dto, Cliente.class);
        ClienteDTO bean = toDTO(clienteService.actualizar(id, c.getTelefono(), c.getDireccion()), new HashMap<>());
        ApiResponse<ClienteDTO> response = new ApiResponse<>(true, "Cliente actualizado", bean);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> eliminar(@PathVariable Long id) {
        clienteService.eliminar(id);
        ApiResponse<Void> response = new ApiResponse<>(true, "Cliente eliminado", null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
