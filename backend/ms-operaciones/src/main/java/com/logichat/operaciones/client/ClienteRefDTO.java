package com.logichat.operaciones.client;

// DTO espejo de ms-personas/dto/ClienteResponse.java (solo lo que valida operaciones).
public class ClienteRefDTO {
    public Long id;
    public Long usuarioId;
    public String nombre;
    public String email;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
}
