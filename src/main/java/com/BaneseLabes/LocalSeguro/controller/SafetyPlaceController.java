package com.BaneseLabes.LocalSeguro.controller;

import com.BaneseLabes.LocalSeguro.config.JwtUtil;
import com.BaneseLabes.LocalSeguro.dto.LocationDTO;
import com.BaneseLabes.LocalSeguro.model.SafetyPlace;
import com.BaneseLabes.LocalSeguro.model.User;
import com.BaneseLabes.LocalSeguro.service.SafetyPlaceService;
import com.BaneseLabes.LocalSeguro.service.UserService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/safetyPlace")
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*", allowCredentials = "true")
public class SafetyPlaceController {
    @Autowired
    private SafetyPlaceService safetyPlaceService;
    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/get")
    public List<SafetyPlace> getSafetyPlaces() {
        return this.safetyPlaceService.findAll();
    }

    //todos de um usuario
    @GetMapping("/get-all")
    public List<SafetyPlace> getUserSafetyPlaces(@RequestHeader("Authorization") String authHeader) throws Exception {
        String token = authHeader.replace("Bearer ", "");
        Claims claims = jwtUtil.extractClaims(token);
        String userSubject = claims.getSubject();
        String cnpj = claims.get("CNPJ").toString();
        return this.safetyPlaceService.findAllByUserId(userSubject, cnpj);
    }

    @GetMapping("/get-cnpj")
    public List<SafetyPlace> getAllSafetyPlaces(@RequestHeader("Authorization") String authHeader) throws Exception {
        String token = authHeader.replace("Bearer ", "");
        Claims claims = jwtUtil.extractClaims(token);
        String cnpj = claims.get("CNPJ").toString();
        return this.safetyPlaceService.findAllByCnpj(cnpj);
    }

    @PostMapping("/add")
    public ResponseEntity<SafetyPlace> saveSafetyPlace(@RequestHeader("Authorization") String authHeader, @RequestBody SafetyPlace safetyPlace) throws Exception {
        String token = authHeader.replace("Bearer ", "");

        Claims claims = jwtUtil.extractClaims(token);
        String userSubject = claims.getSubject();
        String cnpj = claims.get("CNPJ").toString();

        SafetyPlace createdSafetyPlace = safetyPlaceService.save(userSubject, safetyPlace, cnpj);
        return ResponseEntity.ok().body(createdSafetyPlace);
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<SafetyPlace> updateSafetyPlace(@PathVariable String id, @RequestHeader("Authorization") String authHeader, @RequestBody SafetyPlace safetyPlace) throws Exception {
        String token = authHeader.replace("Bearer ", "");

        Claims claims = jwtUtil.extractClaims(token);
        String userSubject = claims.getSubject();
        String cnpj = claims.get("CNPJ").toString();
        return safetyPlaceService.updateSafetyPlace(cnpj, id, userSubject, safetyPlace)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<SafetyPlace> deleteSafetyPlace(@PathVariable String id, @RequestHeader("Authorization") String authHeader) throws Exception {
        String token = authHeader.replace("Bearer ", "");

        Claims claims = jwtUtil.extractClaims(token);
        String userSubject = claims.getSubject();
        String cnpj = claims.get("CNPJ").toString();
        safetyPlaceService.deleteSafetyPlace(cnpj, id, userSubject);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/isInSafetyPlace")
    public ResponseEntity<Boolean> isInSafetyPlace(@RequestHeader("Authorization") String authHeader, @RequestBody LocationDTO locationDTO) throws Exception {
        String token = authHeader.replace("Bearer ", "");
        Claims claims = jwtUtil.extractClaims(token);
        String userSubject = claims.getSubject();
        String cnpj = claims.get("CNPJ").toString();
        Optional<User> userOptional = userService.findByClientId(cnpj, userSubject);
        User user = userOptional.get();
        boolean isInSafetyPlace = safetyPlaceService.isInSafetyPlace(locationDTO, user);
        return ResponseEntity.ok(isInSafetyPlace);
    }

    @PatchMapping("/acceptActive/{id}")
    public ResponseEntity<Boolean> validateSafetyPlace(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable String id,
            @RequestBody LocationDTO locationDTO
    ) throws Exception {
        String token = authHeader.replace("Bearer ", "");
        Claims claims = jwtUtil.extractClaims(token);
        String userSubject = claims.getSubject();
        String cnpj = claims.get("CNPJ").toString();
        boolean result = safetyPlaceService.acceptActive(locationDTO, id, cnpj, userSubject);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/getUserFromId/{id}")
    public ResponseEntity<User> getUserFromId(@RequestHeader("Authorization") String authHeader, @PathVariable String id) throws Exception {
        String token = authHeader.replace("Bearer ", "");
        Claims claims = jwtUtil.extractClaims(token);
        String cnpj = claims.get("CNPJ").toString();
        User user = safetyPlaceService.findUserOfSafetyPlace(id, cnpj);
        return ResponseEntity.ok(user);
    }
}
