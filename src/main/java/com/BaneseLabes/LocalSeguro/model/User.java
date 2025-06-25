package com.BaneseLabes.LocalSeguro.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection = "user")
public class User {
    @Id
    private String clientId;
    private List<SafetyPlace> safetyPlaces;
    private Authorization authorizationOutSafetyPlace;

    public User() {
        this.safetyPlaces = new ArrayList<>();
    }
}
