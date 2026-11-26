package com.merida.tecnm.videogame_store_backend.persistence.mapper;

import com.merida.tecnm.videogame_store_backend.domain.Client;
import com.merida.tecnm.videogame_store_backend.persistence.entity.Cliente;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ClientMapper {

    @Mapping(source = "id", target = "clientId")
    @Mapping(source = "nombre", target = "name")
    @Mapping(source = "apellidos", target = "lastName")
    @Mapping(source = "celular", target = "mobile")
    @Mapping(source = "direccion", target = "address")
    @Mapping(source = "correoElectronico", target = "email")
    Client toClient(Cliente cliente);

    List<Client> toClients(List<Cliente> clientes);

    @InheritInverseConfiguration
    Cliente toCliente(Client client);

    @InheritConfiguration(name = "toCliente")
    @Mapping(target = "id", ignore = true)
    void updateCliente(Client client, @MappingTarget Cliente cliente);
}
