package com.BaneseLabes.LocalSeguro.model.location;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "SafePlaces")
public class ResponseLocation {
    @Id
    private String locationId;
    @JsonProperty("results")
    private LocationDetails[] locationDetails;
}
