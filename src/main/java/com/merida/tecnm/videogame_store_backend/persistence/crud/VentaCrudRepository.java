package com.merida.tecnm.videogame_store_backend.persistence.crud;

import com.merida.tecnm.videogame_store_backend.persistence.entity.Venta;
import org.springframework.data.repository.ListCrudRepository;

public interface VentaCrudRepository extends ListCrudRepository<Venta, Integer> {
}
