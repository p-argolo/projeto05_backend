package com.BaneseLabes.LocalSeguro.service;

import com.BaneseLabes.LocalSeguro.dto.LocationDTO;
import com.BaneseLabes.LocalSeguro.model.Authorization;
import com.BaneseLabes.LocalSeguro.model.SafetyPlace;
import com.BaneseLabes.LocalSeguro.model.User;
import com.BaneseLabes.LocalSeguro.model.Wifi.Wifi;
import com.BaneseLabes.LocalSeguro.repository.SafetyPlaceRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SafetyPlaceService {
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private SafetyPlaceRepository safetyPlaceRepository;
    @Autowired
    private UserService userService;

    private SafetyPlaceService(SafetyPlaceRepository safetyPlaceRepository) {
        this.safetyPlaceRepository = safetyPlaceRepository;
    }

    //  Calcula a distância entre duas coordenadas geográficas usando a fórmula de Haversine
    public static double calculateDistanceBetweenCoordinates(
            double transactionLat, double transactionLng,
            double safetyPlaceLat, double safetyPlaceLng) {
        final double EARTH_RADIUS = 6371.0;

        double dLat = Math.toRadians(safetyPlaceLat - transactionLat);
        double dLng = Math.toRadians(safetyPlaceLng - transactionLng);

        double a = Math.pow(Math.sin(dLat / 2), 2) +
                Math.cos(Math.toRadians(transactionLat)) * Math.cos(Math.toRadians(safetyPlaceLat)) *
                        Math.pow(Math.sin(dLng / 2), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c;
    }

    // Cria um novo local seguro para o usuário
    public SafetyPlace save(String userId, SafetyPlace safetyPlace, String cnpj) throws Exception {
        if (safetyPlace.getId() == null || safetyPlace.getId().isEmpty()) {
            safetyPlace.setId(new ObjectId().toString());
        }

        Optional<User> userOptional = userService.findByClientId(cnpj, userId);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        User user = userOptional.get();

        if (safetyPlace.isWifiType()) {
            for (SafetyPlace userSafetyPlace : findAllWifiType(user)) {
                if (safetyPlace.getWifi().equals(userSafetyPlace.getWifi())) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Safety place already exists nearby");
                }
            }
        } else {
            if (safetyPlace.getLocation() == null) {
                throw new RuntimeException("Location is required for location-based safety places");
            }
            List<SafetyPlace> userSafetyPlaces = findAllLocationType(user);
            if (checkDistanceFromSafePlaces(userSafetyPlaces,
                    safetyPlace.getLocation().getLat(),
                    safetyPlace.getLocation().getLng()) != null) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Safety place already exists nearby");
            }
        }

        // Adiciona o novo local seguro
        user.getSafetyPlaces().add(safetyPlace);
        userService.save(user, cnpj);
        return safetyPlaceRepository.save(safetyPlace);
    }

    // Retorna todos os locais seguros cadastrados
    public List<SafetyPlace> findAll() {
        return this.safetyPlaceRepository.findAll();
    }

    //  Retorna todos os locais seguros cadastrados por um usuário
    public List<SafetyPlace> findAllByUserId(String userId, String cnpj) throws Exception {
        User user = userService.findById(cnpj, userId);
        return user.getSafetyPlaces();
    }

    // Retorna locais do tipo Wi-Fi e ativos do usuário
    public List<SafetyPlace> findAllActiveWifiType(User user) {
        return user.getSafetyPlaces().stream()
                .filter(place -> place.isWifiType() && place.isActive())
                .collect(Collectors.toList());
    }

    // Retorna todos os locais do tipo Wi-Fi do usuário (ativos ou não)
    public List<SafetyPlace> findAllWifiType(User user) {
        return user.getSafetyPlaces().stream()
                .filter(place -> place.isWifiType())
                .collect(Collectors.toList());
    }

    // Retorna locais do tipo localização e ativos do usuári
    public List<SafetyPlace> findAllActiveLocationType(User user) {
        return user.getSafetyPlaces().stream()
                .filter(place -> place.isLocationType() && place.isActive())
                .collect(Collectors.toList());
    }

    // Retorna todos os locais do tipo localização do usuário (ativos ou não)
    public List<SafetyPlace> findAllLocationType(User user) {
        return user.getSafetyPlaces().stream()
                .filter(place -> place.isLocationType())
                .collect(Collectors.toList());
    }

    // Retorna todos os locais seguros de todos os usuários de um cliente pelo cnpj
    public List<SafetyPlace> findAllByCnpj(String cnpj) throws Exception {
        String collectionName = userService.resolveCollection(cnpj);
        List<User> users = mongoTemplate.findAll(User.class, collectionName);
        List<SafetyPlace> AllSafetyPlaces = new ArrayList<>();
        for (User user : users) {
            if (user.getSafetyPlaces() != null) {
                AllSafetyPlaces.addAll(user.getSafetyPlaces());
            }
        }
        return AllSafetyPlaces;
    }

    // Retorna o usuário responsável por um local seguro a partir do ID do local
    public User findUserOfSafetyPlace(String safetyPlaceId, String cnpj) throws Exception {
        List<User> users = userService.findAll(cnpj);
        for (User user : users) {
            List<SafetyPlace> safetyPlaces = user.getSafetyPlaces();
            if (safetyPlaces != null) {
                for (SafetyPlace sp : safetyPlaces) {
                    if (sp.getId().equals(safetyPlaceId)) {
                        return user;
                    }
                }
            }
        }
        return null;
    }

    // Atualiza os dados de um local seguro
    public Optional<SafetyPlace> updateSafetyPlace(String cnpj, String id, String clientId, SafetyPlace safetyPlace) throws Exception {
        Optional<SafetyPlace> existingSafetyPlaceOpt = safetyPlaceRepository.findById(id);
        User user = userService.findById(cnpj, clientId);
        if (existingSafetyPlaceOpt.isPresent()) {
            SafetyPlace existingSafetyPlace = existingSafetyPlaceOpt.get();
            if (safetyPlace.getName() != null) {
                existingSafetyPlace.setName(safetyPlace.getName());
            }
            if (safetyPlace.getAddress() != null) {
                existingSafetyPlace.setAddress(safetyPlace.getAddress());
            }
            if (safetyPlace.getWifi() != null) {
                existingSafetyPlace.setWifi(safetyPlace.getWifi());
            }
            if (safetyPlace.getDataInicio() != null) {
                existingSafetyPlace.setDataInicio(safetyPlace.getDataInicio());
            }
            if (safetyPlace.getDataFim() != null) {
                existingSafetyPlace.setDataFim(safetyPlace.getDataFim());
            }
            if (safetyPlace.getAuthorizationInSafetyPlace() != null) {
                existingSafetyPlace.setAuthorizationInSafetyPlace(safetyPlace.getAuthorizationInSafetyPlace());
            }
            if (safetyPlace.isActive() != null) {
                existingSafetyPlace.setActive(safetyPlace.isActive());
            }
            SafetyPlace updatedSafetyPlace = this.safetyPlaceRepository.save(existingSafetyPlace);
            List<SafetyPlace> userSafetyPlaces = user.getSafetyPlaces();

            for (int i = 0; i < userSafetyPlaces.size(); i++) {
                if (id.equals(userSafetyPlaces.get(i).getId())) {
                    userSafetyPlaces.set(i, existingSafetyPlace);
                    userService.save(user, cnpj);
                    break;
                }
            }
            return Optional.of(updatedSafetyPlace);
        }
        return existingSafetyPlaceOpt;
    }

    // Remove um local seguro de um usuário
    public void deleteSafetyPlace(String cnpj, String id, String clientId) throws Exception {
        Optional<SafetyPlace> safetyPlaceOpt = safetyPlaceRepository.findById(id);
        User user = userService.findById(cnpj, clientId);
        if (safetyPlaceOpt.isPresent()) {
            safetyPlaceRepository.deleteById(id);
            List<SafetyPlace> safetyPlaces = user.getSafetyPlaces();
            safetyPlaces.removeIf(sp -> id.equals(sp.getId()));
            user.setSafetyPlaces(safetyPlaces);
            userService.save(user, cnpj);
        }
    }

    // Remove um local seguro de um usuário
    public SafetyPlace checkDistanceFromSafePlaces(List<SafetyPlace> userSafetyPlaces, Double lat, Double lng) {
        if (userSafetyPlaces.isEmpty()) {
            return null;
        }

        SafetyPlace closestPlace = null;
        Double minDistance = Double.MAX_VALUE;

        for (SafetyPlace safetyPlace : userSafetyPlaces) {
            if (safetyPlace.getLocation() == null) {
                continue;
            }
            Double distance = calculateDistanceBetweenCoordinates(
                    lat,
                    lng,
                    safetyPlace.getLocation().getLat(),
                    safetyPlace.getLocation().getLng());

            if (distance < minDistance) {
                minDistance = distance;
                closestPlace = safetyPlace;
            }
        }

        if (minDistance <= 0.05) {
            System.out.println("location distance: " + String.format("%.2f", minDistance) + "KM  safetyPlace: " + closestPlace.getName());
            return closestPlace;
        }
        return null;
    }

    // Verifica se o usuário está dentro de um local seguro e retorna a autorização apropriada com base no tipo de local seguro encontrado
    public Authorization safetyPlaceMatch(LocationDTO locationDTO, User user) throws Exception {
        List<SafetyPlace> userActiveWifiSafetyPlaces = findAllActiveWifiType(user);

        SafetyPlace wifiMatch = checkWifi(locationDTO.wifi(), userActiveWifiSafetyPlaces);
        if (wifiMatch != null) {
            return wifiMatch.getAuthorizationInSafetyPlace();
        } else {
            List<SafetyPlace> userActiveLocationSafetyPlaces = findAllActiveLocationType(user);
            SafetyPlace locationMatch = checkDistanceFromSafePlaces(
                    userActiveLocationSafetyPlaces,
                    locationDTO.location().getLat(),
                    locationDTO.location().getLng()
            );
            if (locationMatch != null) {
                return locationMatch.getAuthorizationInSafetyPlace();
            }
        }
        return user.getAuthorizationOutSafetyPlace();
    }

    // Verifica se o usuário está dentro de um local seguro
    public boolean isInSafetyPlace(LocationDTO locationDTO, User user) throws Exception {
        return safetyPlaceMatch(locationDTO, user) != user.getAuthorizationOutSafetyPlace();
    }

    // Verifica se o Wi-Fi atual do usuário está associado a algum local seguro
    public SafetyPlace checkWifi(Wifi wifi, List<SafetyPlace> userSafetyPlaces) throws Exception {
        for (SafetyPlace safetyPlace : userSafetyPlaces) {
            Wifi safetyWifi = safetyPlace.getWifi();
            if (safetyWifi != null && safetyWifi.equals(wifi)) {
                System.out.println("Dentro do local seguro");
                return safetyPlace;
            }
        }
        return null;
    }

    // Verifica se o usuário tem um local seguro ativo e compatível e retorna 'true' se houver um local seguro ativo, caso contrário, retorna 'false'
    public boolean hasActiveCompatibleSafetyPlace(LocationDTO locationDTO, User user) throws Exception {
        List<SafetyPlace> userActiveWifiSafetyPlaces = findAllActiveWifiType(user);

        SafetyPlace wifiMatch = checkWifi(locationDTO.wifi(), userActiveWifiSafetyPlaces);
        if (wifiMatch != null) {
            return true;
        }

        List<SafetyPlace> userActiveLocationSafetyPlaces = findAllActiveLocationType(user);
        SafetyPlace locationMatch = checkDistanceFromSafePlaces(
                userActiveLocationSafetyPlaces,
                locationDTO.location().getLat(),
                locationDTO.location().getLng()
        );
        return locationMatch != null;
    }

    // Ativa um local seguro, se houver compatibilidade, e atualiza o usuário com a nova lista de locais seguros
    public boolean acceptActive(LocationDTO locationDTO, String locationId, String cnpj, String clientId) throws Exception {
        Optional<SafetyPlace> safetyPlaceOpt = safetyPlaceRepository.findById(locationId);
        User user = userService.findById(cnpj, clientId);

        if (safetyPlaceOpt.isEmpty()) {
            throw new RuntimeException("Local seguro não encontrado com o id " + locationId);
        }

        SafetyPlace safetyPlace = safetyPlaceOpt.get();

        boolean canActivate = hasActiveCompatibleSafetyPlace(locationDTO, user);

        safetyPlace.setActive(canActivate);

        if (canActivate) {
            safetyPlaceRepository.save(safetyPlace);

            List<SafetyPlace> safetyPlaces = user.getSafetyPlaces();
            if (safetyPlaces != null) {
                List<SafetyPlace> updatedList = new ArrayList<>();
                for (SafetyPlace sp : safetyPlaces) {
                    if (sp.getId().equals(locationId)) {
                        updatedList.add(safetyPlace);
                    } else {
                        updatedList.add(sp);
                    }
                }
                user.setSafetyPlaces(updatedList);
                userService.save(user, cnpj);
            }
        }

        return canActivate;
    }
}