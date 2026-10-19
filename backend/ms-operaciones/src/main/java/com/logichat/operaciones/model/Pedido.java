package com.logichat.operaciones.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "pedido")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pedido")
    private Long id;

    // Referencia a personas_db sin FK. Se valida por Feign.
    @Column(name = "id_cliente", nullable = false)
    private Long clienteId;

    @Column(nullable = false, length = 255)
    private String descripcion;

    @Column(name = "direccion_entrega", nullable = false, length = 250)
    private String direccionEntrega;

    @Column(name = "fecha_pedido", nullable = false)
    private LocalDateTime fechaPedido = LocalDateTime.now();

    @Column(nullable = false, length = 30)
    private String estado = "PENDIENTE";

    public Pedido() {
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
}
