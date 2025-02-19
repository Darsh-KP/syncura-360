package com.example.syncura360_spring.restservice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

@RestController
@CrossOrigin(origins = "http://localhost:4200") // ALLOW REQUESTS FROM LOCAL ANGULAR ENVIRONMENT DURING DEVELOPMENT
@RequestMapping("/register")
public class RegController {

    @PostMapping("/hospital")
    public ResponseEntity<String> registerHospital(@Valid @RequestBody Hospital regData) {
        System.out.println("Received hospital registration data: \n" + regData.toString());

        // CALL HOSPITAL SERVICE FOR INTERACTING WITH HOSPITAL ENTITIES IN DATABASE

        return ResponseEntity.ok("Received Data");
    }

    @PostMapping("/staff")
    public ResponseEntity<String> registerStaff(@Valid @RequestBody Staff regData) {
        System.out.println("Received staff registration data: \n" + regData.toString());

        // CALL USER SERVICE FOR INTERACTING WITH USER ENTITIES IN DATABASE

        return ResponseEntity.ok("Received Data");
    }
}