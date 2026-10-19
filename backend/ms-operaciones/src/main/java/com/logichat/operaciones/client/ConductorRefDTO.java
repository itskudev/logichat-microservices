package com.logichat.operaciones.client;

// DTO espejo de ms-personas/dto/ConductorResponse.java.
public class ConductorRefDTO {
    public Long id;
    public Long usuarioId;
    public String nombre;
    public String licencia;
    public String estado;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
