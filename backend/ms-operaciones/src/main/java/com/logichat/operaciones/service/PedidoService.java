package com.logichat.operaciones.service;

import com.logichat.operaciones.client.PersonasClient;
import com.logichat.operaciones.model.Pedido;
import com.logichat.operaciones.repository.EntregaRepository;
import com.logichat.operaciones.repository.PedidoRepository;
import com.logichat.operaciones.utils.BusinessException;
import com.logichat.operaciones.utils.ModeloNotFoundException;
import feign.FeignException;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final EntregaRepository entregaRepository;
    private final PersonasClient personasClient;

    public PedidoService(PedidoRepository pedidoRepository, EntregaRepository entregaRepository,
                         PersonasClient personasClient) {
        this.pedidoRepository = pedidoRepository;
        this.entregaRepository = entregaRepository;
        this.personasClient = personasClient;
    }

    public List<Pedido> listar() {
        return pedidoRepository.findAll();
    }

    public List<Pedido> listarPorCliente(Long clienteId) {
        return pedidoRepository.findByClienteId(clienteId);
    }

    public Pedido obtener(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new ModeloNotFoundException("Pedido no encontrado"));
    }

    // Valida existencia del cliente por Feign antes de guardar (sin FK cruzada).
    public Pedido crear(Long clienteId, String descripcion, String direccionEntrega) {
        try {
            personasClient.getCliente(clienteId);
        } catch (FeignException.NotFound e) {
            throw new ModeloNotFoundException("Cliente no encontrado");
        }
        if (descripcion == null || descripcion.isBlank()) throw new IllegalArgumentException("Descripción requerida");
        if (direccionEntrega == null || direccionEntrega.isBlank()) throw new IllegalArgumentException("Dirección requerida");
        Pedido p = new Pedido();
        p.setClienteId(clienteId);
        p.setDescripcion(descripcion);
        p.setDireccionEntrega(direccionEntrega);
        p.setEstado("PENDIENTE");
        return pedidoRepository.save(p);
    }

    // El pedido solo se maneja a mano antes de tener entrega en curso;
    // después lo mueve EntregaService (EN_ENTREGA/ENTREGADO/CANCELADO).
    private void exigirSinEntregaActiva(Long pedidoId) {
        boolean activa = entregaRepository.findByPedidoId(pedidoId)
                .map(e -> java.util.Set.of("PENDIENTE", "ASIGNADA", "EN_RUTA").contains(e.getEstado()))
                .orElse(false);
        if (activa) {
            throw new BusinessException("El pedido ya tiene una entrega en curso, gestiónalo desde Entregas");
        }
    }

    public Pedido actualizarEstado(Long id, String estado) {
        Pedido p = obtener(id);
        exigirSinEntregaActiva(id);
        if (!Set.of("PENDIENTE", "PREPARANDO", "LISTO", "CANCELADO").contains(estado)) {
            throw new IllegalArgumentException("Estado de pedido no válido");
        }
        p.setEstado(estado);
        return pedidoRepository.save(p);
    }

    public void eliminar(Long id) {
        obtener(id);
        exigirSinEntregaActiva(id);
        pedidoRepository.deleteById(id);
    }
}
