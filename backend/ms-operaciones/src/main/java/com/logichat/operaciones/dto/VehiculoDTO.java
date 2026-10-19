package com.logichat.operaciones.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class VehiculoDTO {
    private Long id;
    @NotBlank(message = "Placa requerida")
    @Size(max = 20, message = "Placa máximo 20 caracteres")
    private String placa;
    @NotBlank(message = "Tipo requerido")
    @Size(max = 50, message = "Tipo máximo 50 caracteres")
    private String tipo;
    @Size(max = 50, message = "Marca máximo 50 caracteres")
    private String marca;
    @Size(max = 50, message = "Modelo máximo 50 caracteres")
    private String modelo;
    private String estado;

    public VehiculoDTO() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }
    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
