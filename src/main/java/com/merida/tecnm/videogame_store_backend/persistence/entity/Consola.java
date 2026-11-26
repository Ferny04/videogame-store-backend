package com.merida.tecnm.videogame_store_backend.persistence.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "consolas")
public class Consola {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_consola")
    private Integer idConsola;

    @Column(nullable = false, unique = true, length = 45)
    private String nombre;

    @Column(nullable = false)
    private Boolean estado;

    public Integer getIdConsola() {
        return idConsola;
    }

    public void setIdConsola(Integer idConsola) {
        this.idConsola = idConsola;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }
}
