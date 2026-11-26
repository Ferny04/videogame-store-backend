package com.merida.tecnm.videogame_store_backend.domain;

import java.math.BigDecimal;

public class Videogame {

    private Integer videogameId;
    private String name;
    private Integer consoleId;
    private BigDecimal price;
    private Integer stock;
    private Boolean active;
    private Console console;

    public Integer getVideogameId() {
        return videogameId;
    }

    public void setVideogameId(Integer videogameId) {
        this.videogameId = videogameId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getConsoleId() {
        return consoleId;
    }

    public void setConsoleId(Integer consoleId) {
        this.consoleId = consoleId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Console getConsole() {
        return console;
    }

    public void setConsole(Console console) {
        this.console = console;
    }
}
