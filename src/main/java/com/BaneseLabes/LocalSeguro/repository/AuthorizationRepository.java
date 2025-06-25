package com.BaneseLabes.LocalSeguro.repository;

import com.BaneseLabes.LocalSeguro.model.Authorization;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorizationRepository extends MongoRepository<Authorization, String>{
}