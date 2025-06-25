package com.BaneseLabes.LocalSeguro.service;

import com.BaneseLabes.LocalSeguro.clients.GeocodeClient;
import com.BaneseLabes.LocalSeguro.model.location.LocationDetails;
import com.BaneseLabes.LocalSeguro.model.location.ResponseLocation;
import com.BaneseLabes.LocalSeguro.repository.LocationsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GeocodeService {

    @Autowired
    private GeocodeClient geocodeClient;

    @Autowired
    private LocationsRepository locationRepository;

    @Value("${MAPS_API_KEY}")
    private String API_KEY;

    //Consulta a API de geolocalização e formata os dados recebidos
    public ResponseLocation getGeoDetails(String address) {
        ResponseLocation responseLocation = geocodeClient.getGeoDetails(API_KEY, address);
        LocationDetails[] locationDetails = responseLocation.getLocationDetails();
        for (LocationDetails locationDetail : locationDetails) {
            locationDetail.format();
        }
        return responseLocation;
    }

    //Consulta e salva uma nova localização no banco de dados
    public ResponseLocation addLocation(String address) {
        ResponseLocation responseLocation = getGeoDetails(address);
        LocationDetails[] locationDetails = responseLocation.getLocationDetails();
        for (LocationDetails locationDetail : locationDetails) {
            locationDetail.format();
        }
        return locationRepository.save(responseLocation);
    }

    //Atualiza os dados de uma localização existente com base em um novo endereço
    public ResponseLocation updateLocation(String locationId, String newAddress) {
        ResponseLocation oldLocation = locationRepository.findById(locationId).
                orElseThrow(() -> new RuntimeException("Document not found"));
        ResponseLocation newResponseLocation = getGeoDetails(newAddress);
        LocationDetails[] locationDetails = newResponseLocation.getLocationDetails();
        for (LocationDetails locationDetail : locationDetails) {
            locationDetail.format();
        }
        oldLocation.setLocationDetails(newResponseLocation.getLocationDetails());
        locationRepository.save(oldLocation);
        return newResponseLocation;
    }

    //Remove uma localização do banco de dados pelo ID
    public void deleteLocation(String locationId) {
        locationRepository.deleteById(locationId);
    }

}

