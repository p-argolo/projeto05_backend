package com.BaneseLabes.LocalSeguro.controller;

import com.BaneseLabes.LocalSeguro.model.Client;
import com.BaneseLabes.LocalSeguro.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clients")
public class ClientController {

    @Autowired
    private ClientService clientService;

    @GetMapping
    public ResponseEntity<List<Client>> getAllClients() {
        return ResponseEntity.ok(clientService.findAll());
    }

    @GetMapping("/{cnpj}")
    public ResponseEntity<Client> getClientByCnpj(@PathVariable String cnpj) {
        return clientService.findByCnpj(cnpj)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/create")
    public ResponseEntity<Client> createClient(@RequestBody Client client) {
        try {
            Client created = clientService.save(client);
            return ResponseEntity.ok(created);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping
    public ResponseEntity<Client> updateClient(@RequestBody Client client) {
        try {
            Client updated = clientService.update(client);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{cnpj}")
    public ResponseEntity<Void> deleteClient(@PathVariable String cnpj) {
        boolean deleted = clientService.deleteByCnpj(cnpj);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
