package com.merida.tecnm.videogame_store_backend.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class VentaVideojuegoPK implements Serializable {

    @Column(name = "id_venta")
    private Integer idVenta;

    @Column(name = "id_videojuego")
    private Integer idVideojuego;

    public Integer getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(Integer idVenta) {
        this.idVenta = idVenta;
    }

    public Integer getIdVideojuego() {
        return idVideojuego;
    }

    public void setIdVideojuego(Integer idVideojuego) {
        this.idVideojuego = idVideojuego;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof VentaVideojuegoPK)) {
            return false;
        }
        VentaVideojuegoPK id = (VentaVideojuegoPK) object;
        return Objects.equals(idVenta, id.idVenta) && Objects.equals(idVideojuego, id.idVideojuego);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idVenta, idVideojuego);
    }
}
