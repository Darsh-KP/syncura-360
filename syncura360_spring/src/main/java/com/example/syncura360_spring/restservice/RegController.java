package com.example.syncura360_spring.restservice;

import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/register")
public class RegController {

    @PostMapping("/hospital")
    public ResponseEntity<String> registerHospital(@Valid @RequestBody RegistrationInfo regInfo) {

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(16);

        // Prepare Data for insertion:


        return ResponseEntity.ok("Received Data");
    }

    @PostMapping("/test")
    public ResponseEntity<String> test(@RequestBody TestRequest testRequest) {
        System.out.println("Received request \n" + testRequest.text);

        return ResponseEntity.ok("Received Data");
    }
    public record TestRequest(String text) {}

}