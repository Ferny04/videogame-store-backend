package com.merida.tecnm.videogame_store_backend.web.controller;

import com.merida.tecnm.videogame_store_backend.domain.Videogame;
import com.merida.tecnm.videogame_store_backend.domain.service.VideogameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/videogames")
public class VideogameController {

    @Autowired
    private VideogameService videogameService;

    @GetMapping("")
    public ResponseEntity<List<Videogame>> getAll() {
        return ResponseEntity.ok(videogameService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Videogame> getVideogame(@PathVariable("id") int videogameId) {
        return videogameService.getVideogame(videogameId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("")
    public ResponseEntity<Videogame> save(@RequestBody Videogame videogame) {
        return new ResponseEntity<>(videogameService.save(videogame), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Videogame> update(@PathVariable("id") int videogameId, @RequestBody Videogame videogame) {
        return videogameService.update(videogameId, videogame)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") int videogameId) {
        if (videogameService.delete(videogameId)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
