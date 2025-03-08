package com.syncura360.controller;

import com.syncura360.model.Hospital;
import com.syncura360.model.Staff;
import com.syncura360.repository.HospitalRepository;
import com.syncura360.dto.RegistrationInfo;
import com.syncura360.repository.StaffRepository;
import com.syncura360.security.passwordSecurity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/register")
public class RegController {

    @Autowired
    HospitalRepository hospitalRepository;
    @Autowired
    StaffRepository staffRepository;

    @PostMapping("/hospital")
    public ResponseEntity<String> registerHospital(@Valid @RequestBody RegistrationInfo regInfo) {

        Hospital hospital = regInfo.getHospital();
        Staff headAdmin = regInfo.getStaff();

        // Check for unique values.
        if (!hospitalRepository.addressLine1(hospital.getAddressLine1()).isEmpty()) {
            return ResponseEntity.badRequest().header("message", "hospital address taken").build();
        }
        if (!hospitalRepository.findByTelephone(hospital.getTelephone()).isEmpty()) {
            return ResponseEntity.badRequest().header("message", "hospital phone taken").build();
        }

        if (staffRepository.findByUsername(headAdmin.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().header("message", "staff username taken").build();
        }

        // Hash password and create records.
        System.out.println("valid info");

        headAdmin.setWorksAt(hospital);

        try {
            hospitalRepository.save(hospital);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().header("message", "database error: failed to register hospital.").build();
        }

        String passwordHash = passwordSecurity.getPasswordEncoder().encode(headAdmin.getPasswordHash());
        headAdmin.setPasswordHash(passwordHash);


        try {
            staffRepository.save(headAdmin);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            hospitalRepository.delete(hospital);
            return ResponseEntity.internalServerError().header("message", "database error: failed to register user. rolling back hospital registration").build();
        }

        return ResponseEntity.ok("Registration Successful.");
    }
}