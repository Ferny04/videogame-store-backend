package com.merida.tecnm.videogame_store_backend.persistence;

import com.merida.tecnm.videogame_store_backend.domain.Console;
import com.merida.tecnm.videogame_store_backend.domain.repository.ConsoleRepository;
import com.merida.tecnm.videogame_store_backend.persistence.crud.ConsolaCrudRepository;
import com.merida.tecnm.videogame_store_backend.persistence.entity.Consola;
import com.merida.tecnm.videogame_store_backend.persistence.mapper.ConsoleMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ConsolaRepository implements ConsoleRepository {

    @Autowired
    private ConsolaCrudRepository consolaCrudRepository;

    @Autowired
    private ConsoleMapper mapper;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Console> getAll() {
        return mapper.toConsoles((List<Consola>) consolaCrudRepository.findAll());
    }

    @Override
    public Optional<Console> getConsole(int consoleId) {
        return consolaCrudRepository.findById(consoleId).map(mapper::toConsole);
    }

    @Override
    public Console save(Console console) {
        return mapper.toConsole(consolaCrudRepository.save(mapper.toConsola(console)));
    }

    @Override
    public Optional<Console> update(int consoleId, Console console) {
        return findForWrite(consoleId).map(consola -> {
            mapper.updateConsola(console, consola);
            return mapper.toConsole(consola);
        });
    }

    @Override
    public boolean delete(int consoleId) {
        return findForWrite(consoleId).map(consola -> {
            consolaCrudRepository.delete(consola);
            return true;
        }).orElse(false);
    }

    private Optional<Consola> findForWrite(int consoleId) {
        return Optional.ofNullable(entityManager.find(Consola.class, consoleId, LockModeType.PESSIMISTIC_WRITE));
    }
}
