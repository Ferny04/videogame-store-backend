package com.merida.tecnm.videogame_store_backend.persistence;

import com.merida.tecnm.videogame_store_backend.domain.Client;
import com.merida.tecnm.videogame_store_backend.domain.repository.ClientRepository;
import com.merida.tecnm.videogame_store_backend.persistence.crud.ClienteCrudRepository;
import com.merida.tecnm.videogame_store_backend.persistence.entity.Cliente;
import com.merida.tecnm.videogame_store_backend.persistence.mapper.ClientMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ClienteRepository implements ClientRepository {

    @Autowired
    private ClienteCrudRepository clienteCrudRepository;

    @Autowired
    private ClientMapper mapper;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Client> getAll() {
        return mapper.toClients((List<Cliente>) clienteCrudRepository.findAll());
    }

    @Override
    public Optional<Client> getClient(String clientId) {
        return clienteCrudRepository.findById(clientId).map(mapper::toClient);
    }

    @Override
    public Client create(Client client) {
        Cliente cliente = mapper.toCliente(client);
        entityManager.persist(cliente);
        return mapper.toClient(cliente);
    }

    @Override
    public Optional<Client> update(String clientId, Client client) {
        return findForWrite(clientId).map(cliente -> {
            mapper.updateCliente(client, cliente);
            return mapper.toClient(cliente);
        });
    }

    @Override
    public boolean delete(String clientId) {
        return findForWrite(clientId).map(cliente -> {
            clienteCrudRepository.delete(cliente);
            return true;
        }).orElse(false);
    }

    private Optional<Cliente> findForWrite(String clientId) {
        return Optional.ofNullable(entityManager.find(Cliente.class, clientId, LockModeType.PESSIMISTIC_WRITE));
    }
}
