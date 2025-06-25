package com.BaneseLabes.LocalSeguro.model.location;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Address {
    private String street;
    private String number;
    private String neighborhood;
    private String city;
    private String state;
    private String postalCode;
    private String country;

    public String toString() {
        return String.format("%s, %s - %s, %s - %s, %s, %s",
                street, number, neighborhood, postalCode, city, state, country);
    }
}



