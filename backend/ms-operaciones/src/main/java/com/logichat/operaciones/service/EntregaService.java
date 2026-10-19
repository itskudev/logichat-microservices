package com.logichat.operaciones.service;

import com.logichat.operaciones.client.ConductorRefDTO;
import com.logichat.operaciones.client.PersonasClient;
import com.logichat.operaciones.model.Entrega;
import com.logichat.operaciones.model.Pedido;
import com.logichat.operaciones.model.Vehiculo;
import com.logichat.operaciones.repository.EntregaRepository;
import com.logichat.operaciones.repository.PedidoRepository;
import com.logichat.operaciones.repository.VehiculoRepository;
import com.logichat.operaciones.utils.BusinessException;
import com.logichat.operaciones.utils.ModeloNotFoundException;
import feign.FeignException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EntregaService {

    private final EntregaRepository entregaRepository;
    private final PedidoRepository pedidoRepository;
    private final VehiculoRepository vehiculoRepository;
    private final PersonasClient personasClient;

    public EntregaService(EntregaRepository entregaRepository, PedidoRepository pedidoRepository,
                          VehiculoRepository vehiculoRepository, PersonasClient personasClient) {
        this.entregaRepository = entregaRepository;
        this.pedidoRepository = pedidoRepository;
        this.vehiculoRepository = vehiculoRepository;
        this.personasClient = personasClient;
    }

    public List<Entrega> listar() {
        return entregaRepository.findAll();
    }

    public List<Entrega> listarPorConductor(Long conductorId) {
        return entregaRepository.findByConductorId(conductorId);
    }

    public Entrega obtener(Long id) {
        return entregaRepository.findById(id)
                .orElseThrow(() -> new ModeloNotFoundException("Entrega no encontrada"));
    }

    public Entrega crear(Long pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new ModeloNotFoundException("Pedido no encontrado"));
        if (entregaRepository.findByPedidoId(pedidoId).isPresent()) {
            throw new BusinessException("Este pedido ya tiene una entrega");
        }
        Entrega e = new Entrega();
        e.setPedido(pedido);
        e.setEstado("PENDIENTE");
        // Cierra la fase manual del pedido; desde aquí lo mueve la entrega.
        pedido.setEstado("LISTO");
        pedidoRepository.save(pedido);
        return entregaRepository.save(e);
    }

    // Conductor se valida y muta por Feign (vive en personas_db). Vehículo es local.
    public Entrega asignar(Long idEntrega, Long idConductor, Long idVehiculo) {
        Entrega entrega = obtener(idEntrega);
        if (!"PENDIENTE".equals(entrega.getEstado())) {
            throw new BusinessException("Solo se puede asignar una entrega pendiente");
        }
        ConductorRefDTO conductor;
        try {
            conductor = personasClient.getConductor(idConductor);
        } catch (FeignException.NotFound e) {
            throw new ModeloNotFoundException("Conductor no encontrado");
        }
        Vehiculo vehiculo = vehiculoRepository.findById(idVehiculo)
                .orElseThrow(() -> new ModeloNotFoundException("Vehículo no encontrado"));
        if (!"DISPONIBLE".equals(conductor.getEstado()) || !"DISPONIBLE".equals(vehiculo.getEstado())) {
            throw new BusinessException("El conductor y el vehículo deben estar disponibles");
        }
        entrega.setConductorId(idConductor);
        entrega.setVehiculo(vehiculo);
        entrega.setFechaAsignacion(LocalDateTime.now());
        entrega.setEstado("ASIGNADA");
        vehiculo.setEstado("EN_USO");
        vehiculoRepository.save(vehiculo);
        personasClient.setEstadoConductor(idConductor, Map.of("estado", "OCUPADO"));
        return entregaRepository.save(entrega);
    }

    public Entrega cambiarEstado(Long id, String estado) {
        Entrega entrega = obtener(id);
        estado = estado.toUpperCase(Locale.ROOT);
        String anterior = entrega.getEstado();
        boolean valido = ("ASIGNADA".equals(anterior) && "EN_RUTA".equals(estado))
                || ("EN_RUTA".equals(anterior) && "ENTREGADA".equals(estado))
                || (!"ENTREGADA".equals(anterior) && !"CANCELADA".equals(anterior) && "CANCELADA".equals(estado));
        if (!valido) throw new BusinessException("Transición de entrega no válida");
        entrega.setEstado(estado);
        entrega.getPedido().setEstado("ENTREGADA".equals(estado) ? "ENTREGADO"
                : "CANCELADA".equals(estado) ? "CANCELADO" : "EN_ENTREGA");
        pedidoRepository.save(entrega.getPedido());
        if ("EN_RUTA".equals(estado)) entrega.setFechaSalida(LocalDateTime.now());
        if ("ENTREGADA".equals(estado) || "CANCELADA".equals(estado)) {
            if ("ENTREGADA".equals(estado)) entrega.setFechaEntrega(LocalDateTime.now());
            if (entrega.getConductorId() != null) {
                // Consistencia eventual: si falla aquí, reintentar; el conductor queda OCUPADO.
                personasClient.setEstadoConductor(entrega.getConductorId(), Map.of("estado", "DISPONIBLE"));
            }
            if (entrega.getVehiculo() != null) {
                entrega.getVehiculo().setEstado("DISPONIBLE");
                vehiculoRepository.save(entrega.getVehiculo());
            }
        }
        return entregaRepository.save(entrega);
    }

    public void eliminar(Long id) {
        Entrega entrega = obtener(id);
        if (!Set.of("PENDIENTE", "CANCELADA", "ENTREGADA").contains(entrega.getEstado())) {
            throw new BusinessException("No se puede eliminar una entrega activa");
        }
        entregaRepository.delete(entrega);
    }
}
