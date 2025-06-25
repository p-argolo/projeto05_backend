package com.BaneseLabes.LocalSeguro.model.location;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LocationDetails {
    @JsonProperty("geometry")
    private Geometry geometry;

    @JsonProperty("formatted_address")
    private String address;
    private Address splittedAddress;


    public void format() {
        //addressStr = "R. Wilton Melo, 26 - Suíça, Aracaju - SE, 49050-790, Brazil";
        String addressStr = this.address;
        String[] parts = addressStr.split(", ");
        String street = parts[0];
        String number = parts[1].split("-")[0];
        String neighborhood = parts[1].split("-")[1];
        String city = parts[2].split("-")[0];
        String state = parts[2].split("-")[1];
        String postalCode = parts[3];
        String country = parts[4];

        Address address = Address.builder()
                .street(street)
                .number(number)
                .neighborhood(neighborhood)
                .city(city)
                .state(state)
                .postalCode(postalCode)
                .country(country)
                .build();
        this.setSplittedAddress(address);
    }
}









