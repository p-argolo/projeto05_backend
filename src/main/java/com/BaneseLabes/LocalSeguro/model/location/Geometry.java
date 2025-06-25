package com.BaneseLabes.LocalSeguro.model.location;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Geometry {
    @JsonProperty("location")
    private Location location;
}
