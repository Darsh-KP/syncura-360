package com.syncura360.controller;

import com.syncura360.model.Hospital;
import com.syncura360.model.Staff;
import com.syncura360.repository.HospitalRepository;
import com.syncura360.dto.RegistrationInfo;
import com.syncura360.repository.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(16);

        Hospital hospital = regInfo.getHospital();
        Staff headAdmin = regInfo.getStaff();

        // Check for unique values.

        if (!hospitalRepository.findByHospitalName(hospital.getName()).isEmpty()) {
            return ResponseEntity.badRequest().header("message", "hospital name taken").build();
        }
        else if (!hospitalRepository.addressLine1(hospital.getAddressLine1()).isEmpty()) {
            return ResponseEntity.badRequest().header("message", "hospital address taken").build();
        }
        else if (!hospitalRepository.findByTelephone(hospital.getTelephone()).isEmpty()) {
            return ResponseEntity.badRequest().header("message", "hospital phone taken").build();
        }
        else if (!staffRepository.findByEmail(headAdmin.getEmail()).isEmpty()) {
            return ResponseEntity.badRequest().header("message", "staff email taken").build();
        }
        else if (staffRepository.findByUsername(headAdmin.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().header("message", "staff username taken").build();
        }
        else if (!staffRepository.findByPhone(headAdmin.getPhone()).isEmpty()) {
            return ResponseEntity.badRequest().header("message", "staff phone taken").build();
        }
        else {  // Hash password and create records.

            System.out.println("valid info");

            headAdmin.setWorksAt(hospital);

            try {
                hospitalRepository.save(hospital);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return ResponseEntity.internalServerError().header("message", "database error: failed to register hospital.").build();
            }

            String passwordHash = encoder.encode(headAdmin.getPasswordHash());
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

    @PostMapping("/test")
    public ResponseEntity<String> test(@RequestBody TestRequest testRequest) {
        System.out.println("Received data: \n" + testRequest.text);

        return ResponseEntity.ok("Received Data");
    }
    public record TestRequest(String text) {}

}