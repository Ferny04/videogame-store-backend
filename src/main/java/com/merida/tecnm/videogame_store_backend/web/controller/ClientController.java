package com.merida.tecnm.videogame_store_backend.web.controller;

import com.merida.tecnm.videogame_store_backend.domain.Client;
import com.merida.tecnm.videogame_store_backend.domain.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clients")
public class ClientController {

    @Autowired
    private ClientService clientService;

    @GetMapping("")
    public ResponseEntity<List<Client>> getAll() {
        return ResponseEntity.ok(clientService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Client> getClient(@PathVariable("id") String clientId) {
        return clientService.getClient(clientId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("")
    public ResponseEntity<Client> save(@RequestBody Client client) {
        return new ResponseEntity<>(clientService.save(client), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Client> update(@PathVariable("id") String clientId, @RequestBody Client client) {
        return clientService.update(clientId, client)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") String clientId) {
        if (clientService.delete(clientId)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
