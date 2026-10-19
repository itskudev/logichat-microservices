package com.logichat.personas.model;

import jakarta.persistence.*;

@Entity
@Table(name = "conductor")
public class Conductor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_conductor")
    private Long id;

    // Referencia a auth_db sin FK. Se valida por Feign.
    @Column(name = "id_usuario", nullable = false, unique = true)
    private Long usuarioId;

    @Column(nullable = false, unique = true, length = 50)
    private String licencia;

    @Column(length = 20)
    private String telefono;

    @Column(nullable = false, length = 20)
    private String estado = "DISPONIBLE";

    public Conductor() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    public String getLicencia() { return licencia; }
    public void setLicencia(String licencia) { this.licencia = licencia; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
