package com.merida.tecnm.videogame_store_backend.domain.service;

import com.merida.tecnm.videogame_store_backend.domain.Sale;
import com.merida.tecnm.videogame_store_backend.domain.SaleItem;
import com.merida.tecnm.videogame_store_backend.domain.SaleRequest;
import com.merida.tecnm.videogame_store_backend.domain.repository.ClientRepository;
import com.merida.tecnm.videogame_store_backend.domain.repository.SaleRepository;
import com.merida.tecnm.videogame_store_backend.domain.repository.VideogameRepository;
import com.merida.tecnm.videogame_store_backend.persistence.mapper.SaleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

@Service
public class SaleService {

    private static final BigDecimal ITEM_TOTAL_LIMIT = new BigDecimal("100000000000000");

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private VideogameRepository videogameRepository;

    @Autowired
    private SaleMapper mapper;

    @Transactional(readOnly = true)
    public List<Sale> getAll() {
        return saleRepository.getAll();
    }

    @Transactional(readOnly = true)
    public Optional<Sale> getSale(int saleId) {
        validateSaleId(saleId);
        return saleRepository.getSale(saleId);
    }

    @Transactional
    public Sale save(SaleRequest request) {
        Sale sale = toSale(request);
        sale.setDate(LocalDateTime.now().truncatedTo(ChronoUnit.MICROS));
        sale.setState("A");
        return saleRepository.save(sale);
    }

    @Transactional
    public Optional<Sale> update(int saleId, SaleRequest request) {
        validateSaleId(saleId);
        return saleRepository.update(saleId, toSale(request));
    }

    @Transactional
    public boolean delete(int saleId) {
        validateSaleId(saleId);
        return saleRepository.delete(saleId);
    }

    private Sale toSale(SaleRequest request) {
        CatalogValidation.requiredText(request.getClientId(), 20, "clientId");
        CatalogValidation.optionalText(request.getComment(), 300, "comment");
        String paymentMethod = getPaymentMethod(request.getPaymentMethod());
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "items are required");
        }
        if (clientRepository.getClient(request.getClientId()).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Client not found");
        }

        Sale sale = mapper.toSaleRequest(request);
        sale.setPaymentMethod(paymentMethod);

        Set<Integer> videogameIds = new HashSet<>();
        for (SaleItem item : sale.getItems()) {
            if (item == null || item.getVideogameId() == null || item.getVideogameId() <= 0
                    || item.getQuantity() == null || item.getQuantity() <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid sale item");
            }
            if (!videogameIds.add(item.getVideogameId())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Repeated videogame in the sale");
            }

            BigDecimal price = videogameRepository.getVideogame(item.getVideogameId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Videogame not found"))
                    .getPrice();
            BigDecimal total = price.multiply(BigDecimal.valueOf(item.getQuantity()));
            if (total.compareTo(ITEM_TOTAL_LIMIT) >= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sale item total exceeds numeric(16, 2)");
            }
            item.setTotal(total);
        }
        return sale;
    }

    private String getPaymentMethod(String paymentMethod) {
        if (paymentMethod != null) {
            String value = paymentMethod.strip().toUpperCase(Locale.ROOT);
            if ("TARJETA".equals(value)) {
                return "T";
            }
            if ("EFECTIVO".equals(value)) {
                return "E";
            }
            if (value.matches("[A-Z]")) {
                return value;
            }
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid payment method");
    }

    private void validateSaleId(int saleId) {
        if (saleId <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid sale ID");
        }
    }
}
