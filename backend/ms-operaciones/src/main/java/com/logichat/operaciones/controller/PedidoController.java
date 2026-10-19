package com.logichat.operaciones.controller;

import com.logichat.operaciones.dto.PedidoDTO;
import com.logichat.operaciones.model.Pedido;
import com.logichat.operaciones.repository.EntregaRepository;
import com.logichat.operaciones.service.PedidoService;
import com.logichat.operaciones.utils.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/operaciones/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;
    private final EntregaRepository entregaRepository;
    private final ModelMapper mapper;

    public PedidoController(PedidoService pedidoService, EntregaRepository entregaRepository, ModelMapper mapper) {
        this.pedidoService = pedidoService;
        this.entregaRepository = entregaRepository;
        this.mapper = mapper;
    }

    private PedidoDTO toDTO(Pedido p) {
        PedidoDTO dto = mapper.map(p, PedidoDTO.class);
        dto.setTieneEntrega(entregaRepository.findByPedidoId(p.getId()).isPresent());
        return dto;
    }

    @PreAuthorize("hasAnyRole('ADMIN','COORDINADOR','CLIENTE')")
    @GetMapping
    public ResponseEntity<ApiResponse<?>> listar() {
        List<PedidoDTO> lista = pedidoService.listar().stream()
                .map(p -> toDTO(p)).toList();
        ApiResponse<List<PedidoDTO>> response = new ApiResponse<>(true, "Listado de pedidos", lista);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN','COORDINADOR','CLIENTE')")
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<ApiResponse<?>> porCliente(@PathVariable Long clienteId) {
        List<PedidoDTO> lista = pedidoService.listarPorCliente(clienteId).stream()
                .map(p -> toDTO(p)).toList();
        ApiResponse<List<PedidoDTO>> response = new ApiResponse<>(true, "Pedidos del cliente", lista);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN','COORDINADOR','CLIENTE')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> obtener(@PathVariable Long id) {
        PedidoDTO bean = toDTO(pedidoService.obtener(id));
        ApiResponse<PedidoDTO> response = new ApiResponse<>(true, "Pedido existe", bean);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN','COORDINADOR','CLIENTE')")
    @PostMapping
    public ResponseEntity<ApiResponse<?>> crear(@Valid @RequestBody PedidoDTO dto) {
        Pedido p = mapper.map(dto, Pedido.class);
        PedidoDTO bean = toDTO(
                pedidoService.crear(p.getClienteId(), p.getDescripcion(), p.getDireccionEntrega()));
        ApiResponse<PedidoDTO> response = new ApiResponse<>(true, "Pedido registrado", bean);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('ADMIN','COORDINADOR')")
    @PutMapping("/{id}/estado")
    public ResponseEntity<ApiResponse<?>> estado(@PathVariable Long id, @RequestBody Map<String, String> body) {
        PedidoDTO bean = toDTO(pedidoService.actualizarEstado(id, body.get("estado")));
        ApiResponse<PedidoDTO> response = new ApiResponse<>(true, "Estado de pedido actualizado", bean);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN','COORDINADOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> eliminar(@PathVariable Long id) {
        pedidoService.eliminar(id);
        ApiResponse<Void> response = new ApiResponse<>(true, "Pedido eliminado", null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
