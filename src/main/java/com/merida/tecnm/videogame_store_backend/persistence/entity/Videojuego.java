package com.merida.tecnm.videogame_store_backend.persistence.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "videojuegos")
public class Videojuego {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_videojuego")
    private Integer idVideojuego;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(name = "id_consola", nullable = false)
    private Integer idConsola;

    @Column(name = "precio_venta", nullable = false, precision = 16, scale = 2)
    private BigDecimal precioVenta;

    @Column(name = "cantidad_stock", nullable = false)
    private Integer cantidadStock;

    @Column(nullable = false)
    private Boolean estado;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_consola", insertable = false, updatable = false)
    private Consola consola;

    public Integer getIdVideojuego() {
        return idVideojuego;
    }

    public void setIdVideojuego(Integer idVideojuego) {
        this.idVideojuego = idVideojuego;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getIdConsola() {
        return idConsola;
    }

    public void setIdConsola(Integer idConsola) {
        this.idConsola = idConsola;
    }

    public BigDecimal getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(BigDecimal precioVenta) {
        this.precioVenta = precioVenta;
    }

    public Integer getCantidadStock() {
        return cantidadStock;
    }

    public void setCantidadStock(Integer cantidadStock) {
        this.cantidadStock = cantidadStock;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public Consola getConsola() {
        return consola;
    }

    public void setConsola(Consola consola) {
        this.consola = consola;
    }
}
