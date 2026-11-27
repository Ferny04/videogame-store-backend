package com.merida.tecnm.videogame_store_backend.persistence.mapper;

import com.merida.tecnm.videogame_store_backend.domain.Sale;
import com.merida.tecnm.videogame_store_backend.domain.SaleRequest;
import com.merida.tecnm.videogame_store_backend.persistence.entity.Venta;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = SaleItemMapper.class)
public interface SaleMapper {

    @Mapping(source = "idVenta", target = "saleId")
    @Mapping(source = "idCliente", target = "clientId")
    @Mapping(source = "fecha", target = "date")
    @Mapping(source = "medioPago", target = "paymentMethod")
    @Mapping(source = "comentario", target = "comment")
    @Mapping(source = "estado", target = "state")
    @Mapping(source = "videojuegos", target = "items")
    Sale toSale(Venta venta);

    List<Sale> toSales(List<Venta> ventas);

    @InheritInverseConfiguration(name = "toSale")
    @Mapping(target = "cliente", ignore = true)
    Venta toVenta(Sale sale);

    @Mapping(target = "saleId", ignore = true)
    @Mapping(target = "date", ignore = true)
    @Mapping(target = "state", ignore = true)
    Sale toSaleRequest(SaleRequest request);
}
