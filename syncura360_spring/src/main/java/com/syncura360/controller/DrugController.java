package com.syncura360.controller;

import com.syncura360.dto.Drug.DrugDeletionDTO;
import com.syncura360.dto.Drug.DrugFetchListDTO;
import com.syncura360.dto.Drug.DrugFormDTO;
import com.syncura360.dto.Drug.DrugUpdateDTO;
import com.syncura360.dto.ErrorConvertor;
import com.syncura360.dto.GenericMessageResponseDTO;
import com.syncura360.security.JwtUtil;
import com.syncura360.service.DrugService;
import jakarta.persistence.EntityExistsException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for managing drugs in the inventory.
 * Handles operations for inserting, editing, deleting, and viewing drug information.
 * @author Darsh-KP
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/drug")
public class DrugController {
    JwtUtil jwtUtil;
    DrugService drugService;

    /**
     * Constructor for initializing DrugController with required dependencies.
     * Uses constructor injection for necessary components.
     *
     * @param jwtUtil Used to access jwt claims.
     * @param drugService The service layer used for performing operations related to drugs.
     */
    public DrugController(JwtUtil jwtUtil, DrugService drugService) {
        this.jwtUtil = jwtUtil;
        this.drugService = drugService;
    }

    /**
     * Processes the HTTP request to add a new drug in the system.
     * Validates drug data, and stores it in the database.
     *
     * @param authorization The authorization token used to identify user making the request.
     * @param drugFormDTO DTO containing the details of the drug to be added.
     * @param bindingResult Holds the results of the validation of the {@link DrugFormDTO}. This allows for checking if the data is valid before proceeding.
     * @return A {@link ResponseEntity} containing a {@link GenericMessageResponseDTO} that indicated whether the drug was successfully added or if an error occurred.
     */
    @PostMapping
    public ResponseEntity<GenericMessageResponseDTO> addDrug(
            @RequestHeader(name="Authorization") String authorization, @Valid @RequestBody DrugFormDTO drugFormDTO, BindingResult bindingResult) {
        // Basic DTO validation
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new GenericMessageResponseDTO("Invalid request: " + ErrorConvertor.convertErrorsToString(bindingResult)));
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

    /**
     * Processes the HTTP request to update an existing drug in the system.
     * Validates drug data, and updates it in the database.
     *
     * @param authorization The authorization token used to identify user making the request.
     * @param drugFormDTO DTO containing the details of the drug to be updated.
     * @param bindingResult Holds the results of the validation of the {@link DrugFormDTO}. This allows for checking if the data is valid before proceeding.
     * @return A {@link ResponseEntity} containing a {@link GenericMessageResponseDTO} that indicated whether the drug was successfully updated or if an error occurred.
     */
    @PutMapping
    public ResponseEntity<GenericMessageResponseDTO> modifyDrug(
            @RequestHeader(name="Authorization") String authorization, @Valid @RequestBody DrugUpdateDTO drugUpdateDTO, BindingResult bindingResult) {
        // Basic DTO validation
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new GenericMessageResponseDTO("Invalid request: " + ErrorConvertor.convertErrorsToString(bindingResult)));
        }

        // Get the hospital id the logged in staff works at
        int hospitalId = Integer.parseInt(jwtUtil.getHospitalID(authorization));

        // Attempt to update the existing drug
        try {
            drugService.updateDrug(hospitalId, drugUpdateDTO);
        } catch (EntityExistsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new GenericMessageResponseDTO(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new GenericMessageResponseDTO("An unexpected error occurred."));
        }

        // Successfully updated the existing drug
        return ResponseEntity.status(HttpStatus.OK).body(new GenericMessageResponseDTO("Successfully updated the drug."));
    }

    /**
     * Processes the HTTP request to delete an existing drug in the system.
     * Validates drug data, and removes it from the database.
     *
     * @param authorization The authorization token used to identify user making the request.
     * @param drugDeletionDTO DTO containing the details of the drug to be removed.
     * @param bindingResult Holds the results of the validation of the {@link DrugDeletionDTO}. This allows for checking if the data is valid before proceeding.
     * @return A {@link ResponseEntity} containing a {@link GenericMessageResponseDTO} that indicated whether the drug was successfully deleted or if an error occurred.
     */
    @DeleteMapping
    public ResponseEntity<GenericMessageResponseDTO> removeDrug(
            @RequestHeader(name="Authorization") String authorization, @Valid @RequestBody DrugDeletionDTO drugDeletionDTO, BindingResult bindingResult) {
        // Basic DTO validation
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new GenericMessageResponseDTO("Invalid request: " + ErrorConvertor.convertErrorsToString(bindingResult)));
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

    /**
     * Fetches a list of drugs from the inventory.
     *
     * @param authorization The authorization token used to identify user making the request.
     * @return A {@link ResponseEntity} containing a {@link DrugFetchListDTO} with a list of drugs. Only sends necessary information contained in {@link DrugFormDTO}.
     */
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
