package com.merida.tecnm.videogame_store_backend.domain.service;

import com.merida.tecnm.videogame_store_backend.domain.Client;
import com.merida.tecnm.videogame_store_backend.domain.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    public List<Client> getAll() {
        return clientRepository.getAll();
    }

    public Optional<Client> getClient(String clientId) {
        CatalogValidation.clientId(clientId);
        return clientRepository.getClient(clientId);
    }

    @Transactional
    public Client save(Client client) {
        validate(client);
        CatalogValidation.clientId(client.getClientId());
        if (getClient(client.getClientId()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Client ID already exists");
        }
        return clientRepository.create(client);
    }

    @Transactional
    public Optional<Client> update(String clientId, Client client) {
        CatalogValidation.clientId(clientId);
        validate(client);
        return clientRepository.update(clientId, client);
    }

    @Transactional
    public boolean delete(String clientId) {
        CatalogValidation.clientId(clientId);
        return clientRepository.delete(clientId);
    }

    private void validate(Client client) {
        CatalogValidation.requiredText(client.getName(), 40, "name");
        CatalogValidation.optionalText(client.getLastName(), 100, "lastName");
        CatalogValidation.optionalText(client.getMobile(), 25, "mobile");
        CatalogValidation.optionalText(client.getAddress(), 80, "address");
        CatalogValidation.optionalText(client.getEmail(), 70, "email");
    }
}
