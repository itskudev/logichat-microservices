package com.logichat.operaciones.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public class PedidoDTO {
    private Long id;
    @NotNull(message = "Cliente requerido")
    private Long clienteId;
    @NotBlank(message = "Descripción requerida")
    @Size(max = 255, message = "Descripción máximo 255 caracteres")
    private String descripcion;
    @NotBlank(message = "Dirección requerida")
    @Size(max = 250, message = "Dirección máximo 250 caracteres")
    private String direccionEntrega;
    private LocalDateTime fechaPedido;
    private String estado;
    // Solo lectura: indica si ya tiene entrega (entonces el estado lo mueve la entrega).
    private boolean tieneEntrega;

    public PedidoDTO() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getDireccionEntrega() { return direccionEntrega; }
    public void setDireccionEntrega(String direccionEntrega) { this.direccionEntrega = direccionEntrega; }
    public LocalDateTime getFechaPedido() { return fechaPedido; }
    public void setFechaPedido(LocalDateTime fechaPedido) { this.fechaPedido = fechaPedido; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public boolean isTieneEntrega() { return tieneEntrega; }
    public void setTieneEntrega(boolean tieneEntrega) { this.tieneEntrega = tieneEntrega; }
}
