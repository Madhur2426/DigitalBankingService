package com.hcltech.digitalbankingservice.controller;

import com.hcltech.digitalbankingservice.dto.UserRegistrationDto;
import com.hcltech.digitalbankingservice.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register/user")
    public ResponseEntity<String> registerUser(@RequestBody UserRegistrationDto userDto) {
        authService.registerUser(userDto.getUsername(), userDto.getPassword());
        return ResponseEntity.ok("User registered successfully!");
    }

    @PostMapping("/register/admin")
    public ResponseEntity<String> registerAdmin(@RequestBody UserRegistrationDto userDto) {
        authService.registerAdmin(userDto.getUsername(), userDto.getPassword());
        return ResponseEntity.ok("Admin registered successfully!");
    }

    @PostMapping("/login")
    public ResponseEntity<String> createToken(@RequestParam String username, @RequestParam String password) {
        String token = authService.authenticate(username, password);
        return ResponseEntity.ok(token);
    }
}
