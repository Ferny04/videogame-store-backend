package com.merida.tecnm.videogame_store_backend.domain.service;

import com.merida.tecnm.videogame_store_backend.domain.Videogame;
import com.merida.tecnm.videogame_store_backend.domain.repository.VideogameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class VideogameService {

    private static final BigDecimal PRICE_LIMIT = new BigDecimal("100000000000000");

    @Autowired
    private VideogameRepository videogameRepository;

    public List<Videogame> getAll() {
        return videogameRepository.getAll();
    }

    public Optional<Videogame> getVideogame(int videogameId) {
        return videogameRepository.getVideogame(videogameId);
    }

    @Transactional
    public Videogame save(Videogame videogame) {
        validate(videogame);
        videogame.setVideogameId(null);
        return videogameRepository.save(videogame);
    }

    @Transactional
    public Optional<Videogame> update(int videogameId, Videogame videogame) {
        validate(videogame);
        return videogameRepository.update(videogameId, videogame);
    }

    @Transactional
    public boolean delete(int videogameId) {
        return videogameRepository.delete(videogameId);
    }

    private void validate(Videogame videogame) {
        CatalogValidation.requiredText(videogame.getName(), 100, "name");
        CatalogValidation.required(videogame.getConsoleId(), "consoleId");
        CatalogValidation.required(videogame.getPrice(), "price");
        CatalogValidation.required(videogame.getStock(), "stock");
        CatalogValidation.required(videogame.getActive(), "active");

        BigDecimal price = videogame.getPrice();
        if (price.signum() < 0 || price.compareTo(PRICE_LIMIT) >= 0 || price.stripTrailingZeros().scale() > 2) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "price must be nonnegative with at most 14 integer digits and 2 decimal places");
        }
        if (videogame.getStock() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "stock must be nonnegative");
        }
        videogame.setPrice(price.signum() == 0 ? BigDecimal.ZERO.setScale(2) : price.setScale(2));
    }
}
