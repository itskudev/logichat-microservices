package com.logichat.operaciones.dto;

import java.time.LocalDateTime;

public class EntregaDTO {
    private Long id;
    private Long pedidoId;
    private String pedidoDescripcion;
    private String pedidoDireccionEntrega;
    private Long conductorId;
    private Long vehiculoId;
    private String vehiculoPlaca;
    private LocalDateTime fechaAsignacion;
    private LocalDateTime fechaSalida;
    private LocalDateTime fechaEntrega;
    private String estado;

    public EntregaDTO() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getPedidoId() { return pedidoId; }
    public void setPedidoId(Long pedidoId) { this.pedidoId = pedidoId; }
    public String getPedidoDescripcion() { return pedidoDescripcion; }
    public void setPedidoDescripcion(String pedidoDescripcion) { this.pedidoDescripcion = pedidoDescripcion; }
    public String getPedidoDireccionEntrega() { return pedidoDireccionEntrega; }
    public void setPedidoDireccionEntrega(String pedidoDireccionEntrega) { this.pedidoDireccionEntrega = pedidoDireccionEntrega; }
    public Long getConductorId() { return conductorId; }
    public void setConductorId(Long conductorId) { this.conductorId = conductorId; }
    public Long getVehiculoId() { return vehiculoId; }
    public void setVehiculoId(Long vehiculoId) { this.vehiculoId = vehiculoId; }
    public String getVehiculoPlaca() { return vehiculoPlaca; }
    public void setVehiculoPlaca(String vehiculoPlaca) { this.vehiculoPlaca = vehiculoPlaca; }
    public LocalDateTime getFechaAsignacion() { return fechaAsignacion; }
    public void setFechaAsignacion(LocalDateTime fechaAsignacion) { this.fechaAsignacion = fechaAsignacion; }
    public LocalDateTime getFechaSalida() { return fechaSalida; }
    public void setFechaSalida(LocalDateTime fechaSalida) { this.fechaSalida = fechaSalida; }
    public LocalDateTime getFechaEntrega() { return fechaEntrega; }
    public void setFechaEntrega(LocalDateTime fechaEntrega) { this.fechaEntrega = fechaEntrega; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
