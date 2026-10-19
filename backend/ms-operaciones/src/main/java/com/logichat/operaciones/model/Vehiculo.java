package com.logichat.operaciones.model;

import jakarta.persistence.*;

@Entity
@Table(name = "vehiculo")
public class Vehiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_vehiculo")
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String placa;

    @Column(nullable = false, length = 50)
    private String tipo;

    @Column(length = 50)
    private String marca;

    @Column(length = 50)
    private String modelo;

    @Column(nullable = false, length = 20)
    private String estado = "DISPONIBLE";

    public Vehiculo() {
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
