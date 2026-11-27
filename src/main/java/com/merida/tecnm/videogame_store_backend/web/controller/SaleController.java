package com.merida.tecnm.videogame_store_backend.web.controller;

import com.merida.tecnm.videogame_store_backend.domain.Sale;
import com.merida.tecnm.videogame_store_backend.domain.SaleRequest;
import com.merida.tecnm.videogame_store_backend.domain.service.SaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sales")
public class SaleController {

    @Autowired
    private SaleService saleService;

    @GetMapping("")
    public ResponseEntity<List<Sale>> getAll() {
        return ResponseEntity.ok(saleService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Sale> getSale(@PathVariable("id") int saleId) {
        return saleService.getSale(saleId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("")
    public ResponseEntity<Sale> save(@RequestBody SaleRequest request) {
        return new ResponseEntity<>(saleService.save(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Sale> update(@PathVariable("id") int saleId, @RequestBody SaleRequest request) {
        return saleService.update(saleId, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") int saleId) {
        if (saleService.delete(saleId)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
