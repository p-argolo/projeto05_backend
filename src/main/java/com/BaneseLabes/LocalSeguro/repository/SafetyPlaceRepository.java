package com.BaneseLabes.LocalSeguro.repository;

import com.BaneseLabes.LocalSeguro.model.SafetyPlace;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

;

@Repository
public interface SafetyPlaceRepository extends MongoRepository<SafetyPlace, String> {
}