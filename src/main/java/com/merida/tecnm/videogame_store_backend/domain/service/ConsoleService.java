package com.merida.tecnm.videogame_store_backend.domain.service;

import com.merida.tecnm.videogame_store_backend.domain.Console;
import com.merida.tecnm.videogame_store_backend.domain.repository.ConsoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ConsoleService {

    @Autowired
    private ConsoleRepository consoleRepository;

    public List<Console> getAll() {
        return consoleRepository.getAll();
    }

    public Optional<Console> getConsole(int consoleId) {
        return consoleRepository.getConsole(consoleId);
    }

    @Transactional
    public Console save(Console console) {
        validate(console);
        console.setConsoleId(null);
        return consoleRepository.save(console);
    }

    @Transactional
    public Optional<Console> update(int consoleId, Console console) {
        validate(console);
        return consoleRepository.update(consoleId, console);
    }

    @Transactional
    public boolean delete(int consoleId) {
        return consoleRepository.delete(consoleId);
    }

    private void validate(Console console) {
        CatalogValidation.requiredText(console.getName(), 45, "name");
        CatalogValidation.required(console.getActive(), "active");
    }
}
