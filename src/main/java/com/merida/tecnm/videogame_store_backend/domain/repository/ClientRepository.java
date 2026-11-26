package com.merida.tecnm.videogame_store_backend.domain.repository;

import com.merida.tecnm.videogame_store_backend.domain.Client;

import java.util.List;
import java.util.Optional;

public interface ClientRepository {

    List<Client> getAll();

    Optional<Client> getClient(String clientId);

    Client create(Client client);

    Optional<Client> update(String clientId, Client client);

    boolean delete(String clientId);
}
