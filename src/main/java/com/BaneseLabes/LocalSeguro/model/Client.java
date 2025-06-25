package com.BaneseLabes.LocalSeguro.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Clients")
@Data
public class Client {
    private boolean active;
    @Id
    private String cnpj;
    private String name;
    private int activeUsers;
    private int totalSafetyPlaces;
    private int totalWifi;
    private int totalLocations;
}
