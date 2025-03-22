package com.syncura360.controller;

import com.syncura360.dto.Drug.DrugCreationDTO;
import com.syncura360.dto.GenericMessageResponseDTO;
import com.syncura360.security.JwtUtil;
import com.syncura360.service.DrugService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Handles inserting, editing, deleting, and viewing drugs from inventory.
 * @author Darsh-KP
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/drug")
public class DrugController {
    JwtUtil jwtUtil;
    DrugService drugService;

    // Constructor injection
    public DrugController(JwtUtil jwtUtil, DrugService drugService) {
        this.jwtUtil = jwtUtil;
        this.drugService = drugService;
    }

    @PostMapping
    public ResponseEntity<GenericMessageResponseDTO> addDrug(
            @RequestHeader(name="Authorization") String authorization, @RequestBody DrugCreationDTO drugCreationDTO) {
        // TODO Authenticate user

        // Get the hospital id the logged in staff works at
        int hospitalId = Integer.parseInt(jwtUtil.getHospitalID(authorization));

        // Attempt to create new drug
        try {
            drugService.createDrug(hospitalId, drugCreationDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new GenericMessageResponseDTO(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new GenericMessageResponseDTO("An unexpected error occurred."));
        }

        // Success
        return ResponseEntity.status(HttpStatus.CREATED).body(new GenericMessageResponseDTO("Successfully added new drug: "));
    }
}
