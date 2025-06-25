package com.BaneseLabes.LocalSeguro.model;

import com.BaneseLabes.LocalSeguro.model.Wifi.Wifi;
import com.BaneseLabes.LocalSeguro.model.location.Address;
import com.BaneseLabes.LocalSeguro.model.location.Location;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Setter
@Getter
@Document(collection = "safetyPlace")
public class SafetyPlace {
    @Id
    private String id;

    private String name;
    private LocalDate dataInicio;
    private LocalDate dataFim;

    private Location location;
    private Address address;
    private Wifi wifi;
    private Authorization authorizationInSafetyPlace;
    private Boolean active;

    public SafetyPlace() {
    }

    public boolean isWifiType() {
        return this.wifi != null;
    }

    public boolean isLocationType() {
        return this.location != null && this.address != null;
    }

    public Boolean isActive() {
        return this.active;
    }
}