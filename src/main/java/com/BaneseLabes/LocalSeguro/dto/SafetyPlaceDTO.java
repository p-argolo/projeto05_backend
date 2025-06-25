package com.BaneseLabes.LocalSeguro.dto;

import com.BaneseLabes.LocalSeguro.model.Authorization;
import com.BaneseLabes.LocalSeguro.model.Wifi.Wifi;
import com.BaneseLabes.LocalSeguro.model.location.Location;

public record SafetyPlaceDTO(Wifi wifi, Location location, Authorization authorizationInSafetyPlace) {
}
