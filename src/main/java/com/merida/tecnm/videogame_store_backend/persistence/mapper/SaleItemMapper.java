package com.merida.tecnm.videogame_store_backend.persistence.mapper;

import com.merida.tecnm.videogame_store_backend.domain.SaleItem;
import com.merida.tecnm.videogame_store_backend.domain.SaleItemRequest;
import com.merida.tecnm.videogame_store_backend.persistence.entity.VentaVideojuego;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SaleItemMapper {

    @Mapping(source = "id.idVideojuego", target = "videogameId")
    @Mapping(source = "cantidad", target = "quantity")
    @Mapping(source = "estado", target = "active")
    SaleItem toSaleItem(VentaVideojuego videojuego);

    @InheritInverseConfiguration
    @Mapping(target = "venta", ignore = true)
    @Mapping(target = "videojuego", ignore = true)
    VentaVideojuego toVentaVideojuego(SaleItem item);

    @Mapping(target = "total", ignore = true)
    @Mapping(target = "active", constant = "true")
    SaleItem toSaleItemRequest(SaleItemRequest item);
}
