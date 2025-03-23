package com.syncura360.controller;

import com.syncura360.dto.Drug.DrugDeletionDTO;
import com.syncura360.dto.Drug.DrugFetchListDTO;
import com.syncura360.dto.Drug.DrugFormDTO;
import com.syncura360.dto.GenericMessageResponseDTO;
import com.syncura360.security.JwtUtil;
import com.syncura360.service.DrugService;
import jakarta.persistence.EntityExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
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
            @RequestHeader(name="Authorization") String authorization, @RequestBody DrugFormDTO drugFormDTO, BindingResult bindingResult) {
        // Basic DTO validation
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new GenericMessageResponseDTO("Invalid request."));
        }

        // Get the hospital id the logged in staff works at
        int hospitalId = Integer.parseInt(jwtUtil.getHospitalID(authorization));

        // Attempt to create new drug
        try {
            drugService.createDrug(hospitalId, drugFormDTO);
        } catch (EntityExistsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new GenericMessageResponseDTO(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new GenericMessageResponseDTO("An unexpected error occurred."));
        }

        // Successfully added the new drug
        return ResponseEntity.status(HttpStatus.CREATED).body(new GenericMessageResponseDTO("Successfully added the drug."));
    }

    @PutMapping
    public ResponseEntity<GenericMessageResponseDTO> modifyDrug(
            @RequestHeader(name="Authorization") String authorization, @RequestBody DrugFormDTO drugFormDTO, BindingResult bindingResult) {
        // Basic DTO validation
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new GenericMessageResponseDTO("Invalid request."));
        }

        // Get the hospital id the logged in staff works at
        int hospitalId = Integer.parseInt(jwtUtil.getHospitalID(authorization));

        // Attempt to update the existing drug
        try {
            drugService.updateDrug(hospitalId, drugFormDTO);
        } catch (EntityExistsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new GenericMessageResponseDTO(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new GenericMessageResponseDTO("An unexpected error occurred."));
        }

        // Successfully updated the existing drug
        return ResponseEntity.status(HttpStatus.OK).body(new GenericMessageResponseDTO("Successfully updated the drug."));
    }

    @DeleteMapping
    public ResponseEntity<GenericMessageResponseDTO> removeDrug(
            @RequestHeader(name="Authorization") String authorization, @RequestBody DrugDeletionDTO drugDeletionDTO, BindingResult bindingResult) {
        // Basic DTO validation
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new GenericMessageResponseDTO("Invalid request."));
        }

        // Get the hospital id the logged in staff works at
        int hospitalId = Integer.parseInt(jwtUtil.getHospitalID(authorization));

        // Attempt to delete the existing drug
        try {
            drugService.deleteDrug(hospitalId, drugDeletionDTO);
        } catch (EntityExistsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new GenericMessageResponseDTO(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new GenericMessageResponseDTO("An unexpected error occurred."));
        }

        // Successfully delete the existing drug
        return ResponseEntity.status(HttpStatus.OK).body(new GenericMessageResponseDTO("Successfully removed the drug."));
    }

    @GetMapping
    public ResponseEntity<DrugFetchListDTO> getDrugs(@RequestHeader(name="Authorization") String authorization) {
        // Get the hospital id the logged in staff works at
        int hospitalId = Integer.parseInt(jwtUtil.getHospitalID(authorization));

        // Retrieve the list of all the drugs at this hospital
        DrugFetchListDTO drugListDTO = drugService.fetchDrugs(hospitalId);

        // Check if no drugs exists
        if (drugListDTO.getDrugs().isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        // Success, return the list of drugs
        return ResponseEntity.status(HttpStatus.OK).body(drugListDTO);
    }
}
