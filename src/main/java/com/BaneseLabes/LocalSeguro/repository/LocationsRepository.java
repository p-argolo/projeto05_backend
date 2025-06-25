package com.BaneseLabes.LocalSeguro.repository;

import com.BaneseLabes.LocalSeguro.model.location.ResponseLocation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationsRepository extends MongoRepository<ResponseLocation,String> {
}
