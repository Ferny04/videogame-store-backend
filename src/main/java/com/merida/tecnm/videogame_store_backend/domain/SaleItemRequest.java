package com.merida.tecnm.videogame_store_backend.domain;

public class SaleItemRequest {

    private Integer videogameId;
    private Integer quantity;

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
}
