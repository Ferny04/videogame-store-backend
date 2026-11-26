package com.merida.tecnm.videogame_store_backend.persistence.mapper;

import com.merida.tecnm.videogame_store_backend.domain.Videogame;
import com.merida.tecnm.videogame_store_backend.persistence.entity.Videojuego;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring", uses = ConsoleMapper.class)
public interface VideogameMapper {

    @Mapping(source = "idVideojuego", target = "videogameId")
    @Mapping(source = "nombre", target = "name")
    @Mapping(source = "idConsola", target = "consoleId")
    @Mapping(source = "precioVenta", target = "price")
    @Mapping(source = "cantidadStock", target = "stock")
    @Mapping(source = "estado", target = "active")
    @Mapping(source = "consola", target = "console")
    Videogame toVideogame(Videojuego videojuego);

    List<Videogame> toVideogames(List<Videojuego> videojuegos);

    @InheritInverseConfiguration
    @Mapping(target = "consola", ignore = true)
    Videojuego toVideojuego(Videogame videogame);

    @InheritConfiguration(name = "toVideojuego")
    @Mapping(target = "idVideojuego", ignore = true)
    void updateVideojuego(Videogame videogame, @MappingTarget Videojuego videojuego);
}
