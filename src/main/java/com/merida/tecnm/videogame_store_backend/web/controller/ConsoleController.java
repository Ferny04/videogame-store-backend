package com.merida.tecnm.videogame_store_backend.web.controller;

import com.merida.tecnm.videogame_store_backend.domain.Console;
import com.merida.tecnm.videogame_store_backend.domain.service.ConsoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/consoles")
public class ConsoleController {

    @Autowired
    private ConsoleService consoleService;

    @GetMapping("")
    public ResponseEntity<List<Console>> getAll() {
        return ResponseEntity.ok(consoleService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Console> getConsole(@PathVariable("id") int consoleId) {
        return consoleService.getConsole(consoleId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("")
    public ResponseEntity<Console> save(@RequestBody Console console) {
        return new ResponseEntity<>(consoleService.save(console), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Console> update(@PathVariable("id") int consoleId, @RequestBody Console console) {
        return consoleService.update(consoleId, console)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") int consoleId) {
        if (consoleService.delete(consoleId)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
