package com.merida.tecnm.videogame_store_backend.persistence;

import com.merida.tecnm.videogame_store_backend.domain.Sale;
import com.merida.tecnm.videogame_store_backend.domain.repository.SaleRepository;
import com.merida.tecnm.videogame_store_backend.persistence.crud.VentaCrudRepository;
import com.merida.tecnm.videogame_store_backend.persistence.entity.Cliente;
import com.merida.tecnm.videogame_store_backend.persistence.entity.Venta;
import com.merida.tecnm.videogame_store_backend.persistence.entity.VentaVideojuego;
import com.merida.tecnm.videogame_store_backend.persistence.entity.Videojuego;
import com.merida.tecnm.videogame_store_backend.persistence.mapper.SaleMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class VentaRepository implements SaleRepository {

    @Autowired
    private VentaCrudRepository ventaCrudRepository;

    @Autowired
    private SaleMapper mapper;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Sale> getAll() {
        return mapper.toSales(ventaCrudRepository.findAll());
    }

    @Override
    public Optional<Sale> getSale(int saleId) {
        return ventaCrudRepository.findById(saleId).map(mapper::toSale);
    }

    @Override
    public Sale save(Sale sale) {
        Venta venta = mapper.toVenta(sale);
        setCliente(venta, venta.getIdCliente());
        venta.getVideojuegos().forEach(item -> linkItem(venta, item));
        return mapper.toSale(ventaCrudRepository.save(venta));
    }

    @Override
    public Optional<Sale> update(int saleId, Sale sale) {
        return findForWrite(saleId).map(venta -> {
            Venta updated = mapper.toVenta(sale);
            setCliente(venta, updated.getIdCliente());
            venta.setMedioPago(updated.getMedioPago());
            venta.setComentario(updated.getComentario());

            // Keep the managed collection and update existing composite keys in place.
            Map<Integer, VentaVideojuego> currentItems = new HashMap<>();
            for (VentaVideojuego item : venta.getVideojuegos()) {
                currentItems.put(item.getId().getIdVideojuego(), item);
            }
            for (VentaVideojuego item : updated.getVideojuegos()) {
                VentaVideojuego current = currentItems.remove(item.getId().getIdVideojuego());
                if (current == null) {
                    linkItem(venta, item);
                    venta.getVideojuegos().add(item);
                } else {
                    current.setCantidad(item.getCantidad());
                    current.setTotal(item.getTotal());
                }
            }
            venta.getVideojuegos().removeAll(currentItems.values());
            return mapper.toSale(venta);
        });
    }

    @Override
    public boolean delete(int saleId) {
        return findForWrite(saleId).map(venta -> {
            ventaCrudRepository.delete(venta);
            return true;
        }).orElse(false);
    }

    private Optional<Venta> findForWrite(int saleId) {
        return Optional.ofNullable(entityManager.find(Venta.class, saleId, LockModeType.PESSIMISTIC_WRITE));
    }

    private void setCliente(Venta venta, String clientId) {
        venta.setIdCliente(clientId);
        venta.setCliente(entityManager.getReference(Cliente.class, clientId));
    }

    private void linkItem(Venta venta, VentaVideojuego item) {
        item.setVenta(venta);
        item.setVideojuego(entityManager.getReference(Videojuego.class, item.getId().getIdVideojuego()));
    }
}
