package com.BaneseLabes.LocalSeguro.controller;

import com.BaneseLabes.LocalSeguro.config.JwtUtil;
import com.BaneseLabes.LocalSeguro.model.User;
import com.BaneseLabes.LocalSeguro.service.UserService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/user")

public class UserController {
    private final JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    public UserController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    //Get todos users
    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/get-all")
    public List<User> getAllUsers(@RequestHeader("Authorization") String authHeader) throws Exception {
        String token = authHeader.replace("Bearer ", "");
        Claims claims = jwtUtil.extractClaims(token);
        String cnpj = claims.get("CNPJ").toString();
        return this.userService.findAll(cnpj);
    }

    //Get user
    @GetMapping("/get")
    public Optional<User> getUser(@RequestHeader("Authorization") String authHeader) throws Exception {
        String token = authHeader.replace("Bearer ", "");
        Claims claims = jwtUtil.extractClaims(token);
        String clientId = claims.getSubject();
        String cnpj = claims.get("CNPJ").toString();
        return userService.findByClientId(cnpj, clientId);
    }

    //post
    @PostMapping("/add")
    public ResponseEntity<User> addUser(@RequestHeader("Authorization") String authHeader, @RequestBody User user) throws Exception {
        String token = authHeader.replace("Bearer ", "");

        Claims claims = jwtUtil.extractClaims(token);
        String userSubject = claims.getSubject();
        String cnpj = claims.get("cnpj").toString();

        user.setClientId(userSubject);
        User createdUser = userService.save(user, cnpj);
        return ResponseEntity.ok().body(createdUser);
    }

    @PatchMapping("update")
    public ResponseEntity<User> updateUser(@RequestHeader("Authorization") String authHeader, @RequestBody User user) throws Exception {
        String token = authHeader.replace("Bearer ", "");

        Claims claims = jwtUtil.extractClaims(token);
        String userSubject = claims.getSubject();
        String cnpj = claims.get("CNPJ").toString();
        return userService.updateUser(cnpj, userSubject, user)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("delete")
    public ResponseEntity<User> deleteUser(@RequestHeader("Authorization") String authHeader) throws Exception {
        String token = authHeader.replace("Bearer ", "");

        Claims claims = jwtUtil.extractClaims(token);
        String userSubject = claims.getSubject();
        String cnpj = claims.get("cnpj").toString();
        userService.deleteUser(cnpj, userSubject);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/permissions")
    public ResponseEntity<Map<String, Boolean>> getPermissions(@RequestHeader("Authorization") String authHeader) throws Exception {
        String token = authHeader.replace("Bearer ", "");

        Claims claims = jwtUtil.extractClaims(token);
        String userSubject = claims.getSubject();
        String cnpj = claims.get("cnpj").toString();
        User user = userService.findById(cnpj, userSubject);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        Map<String, Boolean> permissions = new HashMap<>();
        permissions.put("can make PIX", userService.canMakePixOutSafetyPlace(user));
        permissions.put("can make Loan", userService.canMakeLoanOutSafetyPlace(user));
        permissions.put("Can make BankSplit", userService.canMakeBankSplitOutSafetyPlace(user));
        permissions.put("Can make TED", userService.canMakeTedOutSafetyPlace(user));
        permissions.put("Can register virtual card", userService.canRegisterVirtualCard(user));
        permissions.put("Can change password", userService.canChangePassword(user));

        return ResponseEntity.ok(permissions);
    }
}
