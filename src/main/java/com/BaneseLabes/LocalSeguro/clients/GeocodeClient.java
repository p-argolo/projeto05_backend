package com.BaneseLabes.LocalSeguro.clients;

import com.BaneseLabes.LocalSeguro.model.location.ResponseLocation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "geocodeClient", url = "https://maps.googleapis.com/maps/api/geocode")
public interface GeocodeClient {

    @GetMapping("/json")
    ResponseLocation getGeoDetails(@RequestParam("key") String apiKey, @RequestParam("address") String address);
}
