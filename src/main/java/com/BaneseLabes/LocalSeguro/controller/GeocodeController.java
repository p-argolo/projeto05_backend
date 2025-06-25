package com.BaneseLabes.LocalSeguro.controller;

import com.BaneseLabes.LocalSeguro.model.location.ResponseLocation;
import com.BaneseLabes.LocalSeguro.service.GeocodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/geocode")
public class GeocodeController {

    @Autowired
    private GeocodeService geocodeService;

    @GetMapping("/getLocation")
    public ResponseLocation getGeoDetails(@RequestParam String address) {
        return geocodeService.getGeoDetails(address);
    }

    @PostMapping("/addLocation")
    public ResponseLocation addSafeplace(@RequestParam String address) {
        return geocodeService.addLocation(address);
    }

    @PatchMapping("/updateLocation")
    public void updateLocation(@RequestParam String locationId, @RequestParam String address) {
        geocodeService.updateLocation(locationId, address);
    }

    @DeleteMapping("/deleteLocation")
    public void deleteLocation(@RequestParam String locationId) {
        geocodeService.deleteLocation(locationId);
    }
}
