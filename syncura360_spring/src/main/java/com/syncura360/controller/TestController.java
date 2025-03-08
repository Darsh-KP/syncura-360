package com.syncura360.controller;

import com.syncura360.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
public class TestController {

    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    JwtUtil jwtUtil;

    @GetMapping("/test_auth")
    public ResponseEntity<AuthResponse> registerHospital(@RequestBody String message, @RequestHeader(name="Authorization") String authorization) {
        String role = jwtUtil.getRole(authorization);
        return ResponseEntity.ok().body(new AuthResponse("Authentication Successful!", role));
    }
    public record AuthResponse(String message, String role) {}
}
