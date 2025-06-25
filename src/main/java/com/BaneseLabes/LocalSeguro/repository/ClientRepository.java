package com.BaneseLabes.LocalSeguro.repository;

import com.BaneseLabes.LocalSeguro.model.Client;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends MongoRepository<Client, String> {
    public Client findByCnpj(String cnpj);

}
