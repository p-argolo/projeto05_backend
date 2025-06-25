package com.BaneseLabes.LocalSeguro.service;

import com.BaneseLabes.LocalSeguro.model.Client;
import com.BaneseLabes.LocalSeguro.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    //Retorna todos os clientes cadastrados no banco de dados
    public List<Client> findAll() {
        return clientRepository.findAll();
    }

    // Retorna um cliente pelo CNPJ ou vazio
    public Optional<Client> findByCnpj(String cnpj) {
        return Optional.ofNullable(clientRepository.findByCnpj(cnpj));
    }

    //Salva um novo cliente no banco de dados
    public Client save(Client client) {
        if (clientRepository.findByCnpj(client.getCnpj()) != null) {
            throw new RuntimeException("Client already exists with CNPJ: " + client.getCnpj());
        }
        return clientRepository.save(client);
    }

    //Atualiza um cliente no banco de dados
    public Client update(Client client) {
        Client existing = clientRepository.findByCnpj(client.getCnpj());
        if (existing == null) {
            throw new RuntimeException("Client not found with CNPJ: " + client.getCnpj());
        }

        // Atualize os campos necessários
        if (client.getName() != null) {
            existing.setName(client.getName());
        }

        existing.setActive(client.isActive());
        existing.setActiveUsers(client.getActiveUsers());

        return clientRepository.save(existing);
    }

    //Deleta um cliente no banco de dados
    public boolean deleteByCnpj(String cnpj) {
        Client existing = clientRepository.findByCnpj(cnpj);
        if (existing != null) {
            clientRepository.delete(existing);
            return true;
        }
        return false;
    }
}
