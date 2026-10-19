package com.logichat.personas.client;

// DTO espejo de ms-auth/dto/UsuarioResponse.java (solo lo que usa personas).
public class UsuarioRefDTO {
    public Long id;
    public String nombre;
    public String apellido;
    public String email;
    public String rol;
    public String estado;

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

    public String nombreCompleto() {
        String n = nombre != null ? nombre : "";
        String a = apellido != null ? apellido : "";
        return (n + " " + a).trim();
    }
}
