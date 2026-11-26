package com.merida.tecnm.videogame_store_backend.persistence.mapper;

import com.merida.tecnm.videogame_store_backend.domain.Console;
import com.merida.tecnm.videogame_store_backend.persistence.entity.Consola;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ConsoleMapper {

    @Mapping(source = "idConsola", target = "consoleId")
    @Mapping(source = "nombre", target = "name")
    @Mapping(source = "estado", target = "active")
    Console toConsole(Consola consola);

    List<Console> toConsoles(List<Consola> consolas);

    @InheritInverseConfiguration
    Consola toConsola(Console console);

    @InheritConfiguration(name = "toConsola")
    @Mapping(target = "idConsola", ignore = true)
    void updateConsola(Console console, @MappingTarget Consola consola);
}
