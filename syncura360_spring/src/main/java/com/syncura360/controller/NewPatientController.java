package com.syncura360.controller;

import com.syncura360.dto.GenericMessageResponseDTO;
import com.syncura360.dto.Patient.PatientFormDTO;
import com.syncura360.security.JwtUtil;
import com.syncura360.service.PatientService;
import jakarta.persistence.EntityExistsException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/patient")
public class NewPatientController {
    JwtUtil jwtUtil;
    PatientService patientService;

    // Constructor injection
    public NewPatientController(JwtUtil jwtUtil, PatientService patientService) {
        this.jwtUtil = jwtUtil;
        this.patientService = patientService;
    }

    @PostMapping
    public ResponseEntity<GenericMessageResponseDTO> addPatient(
            @Valid @RequestBody PatientFormDTO patientFormDTO, BindingResult bindingResult) {
        // Basic DTO validation
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new GenericMessageResponseDTO("Invalid request."));
        }

        // Attempt to add new patient
        try {
            patientService.createPatient(patientFormDTO);
        } catch (EntityExistsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new GenericMessageResponseDTO(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new GenericMessageResponseDTO("An unexpected error occurred."));
        }

        // Successfully added new patient
        return ResponseEntity.status(HttpStatus.CREATED).body(new GenericMessageResponseDTO("Successfully added the patient."));
    }
}