package com.merida.tecnm.videogame_store_backend.persistence.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "ventas_videojuegos")
public class VentaVideojuego {

    @EmbeddedId
    private VentaVideojuegoPK id;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(nullable = false, precision = 16, scale = 2)
    private BigDecimal total;

    @Column(nullable = false)
    private Boolean estado;

    @ManyToOne(optional = false)
    @MapsId("idVenta")
    @JoinColumn(name = "id_venta", nullable = false)
    private Venta venta;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_videojuego", insertable = false, updatable = false)
    private Videojuego videojuego;

    public VentaVideojuegoPK getId() {
        return id;
    }

    public void setId(VentaVideojuegoPK id) {
        this.id = id;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public Venta getVenta() {
        return venta;
    }

    public void setVenta(Venta venta) {
        this.venta = venta;
    }

    public Videojuego getVideojuego() {
        return videojuego;
    }

    public void setVideojuego(Videojuego videojuego) {
        this.videojuego = videojuego;
    }
}
