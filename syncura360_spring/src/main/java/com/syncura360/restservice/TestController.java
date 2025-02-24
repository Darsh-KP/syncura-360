package com.syncura360.restservice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin(origins = "*")
@RestController
public class TestController {
    @GetMapping("/test_auth")
    public ResponseEntity<String> registerHospital(@Valid @RequestBody RegistrationInfo regInfo) {
        return null;
    }
}
