package com.merida.tecnm.videogame_store_backend.domain.repository;

import com.merida.tecnm.videogame_store_backend.domain.Videogame;

import java.util.List;
import java.util.Optional;

public interface VideogameRepository {

    List<Videogame> getAll();

    Optional<Videogame> getVideogame(int videogameId);

    Videogame save(Videogame videogame);

    Optional<Videogame> update(int videogameId, Videogame videogame);

    boolean delete(int videogameId);
}
