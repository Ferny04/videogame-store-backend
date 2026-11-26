package com.merida.tecnm.videogame_store_backend.domain.repository;

import com.merida.tecnm.videogame_store_backend.domain.Console;

import java.util.List;
import java.util.Optional;

public interface ConsoleRepository {

    List<Console> getAll();

    Optional<Console> getConsole(int consoleId);

    Console save(Console console);

    Optional<Console> update(int consoleId, Console console);

    boolean delete(int consoleId);
}
