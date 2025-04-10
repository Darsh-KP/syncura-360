package com.syncura360.controller;

import com.syncura360.dto.ErrorConvertor;
import com.syncura360.dto.GenericMessageResponseDTO;
import com.syncura360.dto.Patient.PatientFormDTO;
import com.syncura360.dto.Patient.PatientUpdateDTO;
import com.syncura360.dto.Patient.PatientViewFetchContainer;
import com.syncura360.dto.Patient.SpecificPatientFetchDTO;
import com.syncura360.service.PatientService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for managing patient-related operations.
 * Provides endpoints to add, modify, and fetch patient information.
 *
 * @author Darsh-KP
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/patient")
public class PatientController {
    PatientService patientService;

    /**
     * Constructor to inject the PatientService dependency.
     *
     * @param patientService the {@link PatientService} instance for handling patient-related business logic.
     */
    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    /**
     * Endpoint to add a new patient.
     * Validates the request body and attempts to create a new patient.
     *
     * @param patientFormDTO the DTO containing the details of the patient to be added ({@link PatientFormDTO}).
     * @param bindingResult the result of validating the patientFormDTO.
     * @return a {@link ResponseEntity} containing a {@link GenericMessageResponseDTO} with the outcome message.
     */
    @PostMapping
    public ResponseEntity<GenericMessageResponseDTO> addPatient(
            @Valid @RequestBody PatientFormDTO patientFormDTO, BindingResult bindingResult) {
        // Basic DTO validation
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new GenericMessageResponseDTO("Invalid request: " + ErrorConvertor.convertErrorsToString(bindingResult)));
        }

        // Attempt to add new patient
        try {
            patientService.createPatient(patientFormDTO);
        } catch (EntityExistsException | EntityNotFoundException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new GenericMessageResponseDTO(e.getMessage()));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new GenericMessageResponseDTO("An unexpected error occurred."));
        }

        // Successfully added new patient
        return ResponseEntity.status(HttpStatus.CREATED).body(new GenericMessageResponseDTO("Successfully added the patient."));
    }

    /**
     * Endpoint to modify an existing patient's information.
     * Validates the request body and attempts to update the patient details.
     *
     * @param patientUpdateDTO the DTO containing the updated patient information ({@link PatientUpdateDTO}).
     * @param bindingResult the result of validating the patientUpdateDTO.
     * @return a {@link ResponseEntity} containing a {@link GenericMessageResponseDTO} with the outcome message.
     */
    @PutMapping
    public ResponseEntity<GenericMessageResponseDTO> modifyPatient(
            @Valid @RequestBody PatientUpdateDTO patientUpdateDTO, BindingResult bindingResult) {
        // Basic DTO validation
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new GenericMessageResponseDTO("Invalid request: " + ErrorConvertor.convertErrorsToString(bindingResult)));
        }

        // Attempt to update a patient
        try {
            patientService.updatePatient(patientUpdateDTO);
        } catch (EntityExistsException | EntityNotFoundException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new GenericMessageResponseDTO(e.getMessage()));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new GenericMessageResponseDTO("An unexpected error occurred."));
        }

        // Successfully updated the existing patient
        return ResponseEntity.status(HttpStatus.CREATED).body(new GenericMessageResponseDTO("Successfully updated the patient."));
    }

    /**
     * Endpoint to retrieve the list of all patients.
     *
     * @return a {@link ResponseEntity} containing a {@link PatientViewFetchContainer} with the list of patients,
     *         or a 204 NO_CONTENT if no patients exist.
     */
    @GetMapping
    public ResponseEntity<PatientViewFetchContainer> getPatients() {
        // Retrieve the list of patients
        PatientViewFetchContainer patientViewFetchContainer = patientService.fetchPatients();

        // Check if no patients exists
        if (patientViewFetchContainer.getPatients().isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        // Success, return the list of patients
        return ResponseEntity.status(HttpStatus.OK).body(patientViewFetchContainer);
    }

    /**
     * Endpoint to retrieve a specific patient by their ID.
     *
     * @param patientId the ID of the patient to be fetched.
     * @return a {@link ResponseEntity} containing a {@link SpecificPatientFetchDTO} with the patient's details,
     *         or a 404 NOT_FOUND if the patient does not exist.
     */
    @GetMapping("/{patient-id}")
    public ResponseEntity<SpecificPatientFetchDTO> getPatientById(@PathVariable("patient-id") Integer patientId) {
        // Basic path variable check
        if (patientId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        // Attempt to fetch the specific patient
        SpecificPatientFetchDTO specificPatientFetchDTO;
        try {
            specificPatientFetchDTO = patientService.fetchPatient(patientId);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        // Success, return the patient
        return ResponseEntity.status(HttpStatus.OK).body(specificPatientFetchDTO);
    }
}