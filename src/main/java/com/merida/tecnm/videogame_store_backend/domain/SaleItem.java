package com.merida.tecnm.videogame_store_backend.domain;

import java.math.BigDecimal;

public class SaleItem {

    private Integer videogameId;
    private Integer quantity;
    private BigDecimal total;
    private Boolean active;

    public Integer getVideogameId() {
        return videogameId;
    }

    public void setVideogameId(Integer videogameId) {
        this.videogameId = videogameId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
