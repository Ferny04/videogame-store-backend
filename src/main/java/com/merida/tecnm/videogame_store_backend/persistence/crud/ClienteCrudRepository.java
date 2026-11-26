package com.merida.tecnm.videogame_store_backend.persistence.crud;

import com.merida.tecnm.videogame_store_backend.persistence.entity.Cliente;
import org.springframework.data.repository.CrudRepository;

public interface ClienteCrudRepository extends CrudRepository<Cliente, String> {
}
