package com.merida.tecnm.videogame_store_backend.persistence.crud;

import com.merida.tecnm.videogame_store_backend.persistence.entity.Consola;
import org.springframework.data.repository.CrudRepository;

public interface ConsolaCrudRepository extends CrudRepository<Consola, Integer> {
}
