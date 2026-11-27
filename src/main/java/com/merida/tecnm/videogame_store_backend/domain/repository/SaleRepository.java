package com.merida.tecnm.videogame_store_backend.domain.repository;

import com.merida.tecnm.videogame_store_backend.domain.Sale;

import java.util.List;
import java.util.Optional;

public interface SaleRepository {

    List<Sale> getAll();

    Optional<Sale> getSale(int saleId);

    Sale save(Sale sale);

    Optional<Sale> update(int saleId, Sale sale);

    boolean delete(int saleId);
}
