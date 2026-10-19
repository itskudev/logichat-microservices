package com.logichat.auth.dto;

import com.logichat.auth.model.Usuario;

public class UsuarioResponse {
    public Long id;
    public String nombre;
    public String apellido;
    public String email;
    public String rol;
    public String estado;

    public UsuarioResponse() {
    }

    public UsuarioResponse(Usuario u) {
        this.id = u.getId();
        this.nombre = u.getNombre();
        this.apellido = u.getApellido();
        this.email = u.getEmail();
        this.rol = u.getRol() != null ? u.getRol().getNombre() : null;
        this.estado = u.getEstado();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
