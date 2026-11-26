package com.merida.tecnm.videogame_store_backend.persistence;

import com.merida.tecnm.videogame_store_backend.domain.Videogame;
import com.merida.tecnm.videogame_store_backend.domain.repository.VideogameRepository;
import com.merida.tecnm.videogame_store_backend.persistence.crud.ConsolaCrudRepository;
import com.merida.tecnm.videogame_store_backend.persistence.crud.VideojuegoCrudRepository;
import com.merida.tecnm.videogame_store_backend.persistence.entity.Videojuego;
import com.merida.tecnm.videogame_store_backend.persistence.mapper.VideogameMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Repository
public class VideojuegoRepository implements VideogameRepository {

    @Autowired
    private VideojuegoCrudRepository videojuegoCrudRepository;

    @Autowired
    private ConsolaCrudRepository consolaCrudRepository;

    @Autowired
    private VideogameMapper mapper;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Videogame> getAll() {
        return mapper.toVideogames((List<Videojuego>) videojuegoCrudRepository.findAll());
    }

    @Override
    public Optional<Videogame> getVideogame(int videogameId) {
        return videojuegoCrudRepository.findById(videogameId).map(mapper::toVideogame);
    }

    @Override
    public Videogame save(Videogame videogame) {
        return saveVideojuego(mapper.toVideojuego(videogame));
    }

    @Override
    public Optional<Videogame> update(int videogameId, Videogame videogame) {
        return findForWrite(videogameId).map(videojuego -> {
            mapper.updateVideojuego(videogame, videojuego);
            return saveVideojuego(videojuego);
        });
    }

    private Videogame saveVideojuego(Videojuego videojuego) {
        videojuego.setConsola(consolaCrudRepository.findById(videojuego.getIdConsola())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "consoleId does not exist")));
        return mapper.toVideogame(videojuegoCrudRepository.save(videojuego));
    }

    @Override
    public boolean delete(int videogameId) {
        return findForWrite(videogameId).map(videojuego -> {
            videojuegoCrudRepository.delete(videojuego);
            return true;
        }).orElse(false);
    }

    private Optional<Videojuego> findForWrite(int videogameId) {
        return Optional.ofNullable(entityManager.find(Videojuego.class, videogameId, LockModeType.PESSIMISTIC_WRITE));
    }
}
