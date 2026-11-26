package com.merida.tecnm.videogame_store_backend.persistence.crud;

import com.merida.tecnm.videogame_store_backend.persistence.entity.Videojuego;
import org.springframework.data.repository.CrudRepository;

public interface VideojuegoCrudRepository extends CrudRepository<Videojuego, Integer> {
}
