package com.syncura360.controller;

import com.syncura360.dto.ErrorConvertor;
import com.syncura360.dto.GenericMessageResponseDTO;
import com.syncura360.dto.Visit.AddDrugDTO;
import com.syncura360.dto.Visit.AddServiceDTO;
import com.syncura360.dto.Visit.VisitCreationDTO;
import com.syncura360.security.JwtUtil;
import com.syncura360.service.VisitService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * Handles incoming requests for visit management.
 * @author Benjamin Leiby
 */
@RestController
@CrossOrigin(origins="*")
@RequestMapping("/visit")
public class VisitController {

    @Autowired
    JwtUtil jwtUtil;
    @Autowired
    VisitService visitService;

    /**
     * Starts a new visit.
     * @param authorization JWT header.
     * @param visitCreationDTO DTO to model incoming visit creation request.
     * @param bindingResult Result of parsing the request.
     * @return GenericMessageResponseDTO containing the result of the request.
     */
    @PostMapping
    public ResponseEntity<GenericMessageResponseDTO> createVisit(
        @RequestHeader(name="Authorization") String authorization,
        @RequestBody VisitCreationDTO visitCreationDTO,
        BindingResult bindingResult)
    {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new GenericMessageResponseDTO("Invalid request: " + ErrorConvertor.convertErrorsToString(bindingResult)));
        }

        int hospitalId = Integer.parseInt(jwtUtil.getHospitalID(authorization));

        try {
            visitService.createVisit(hospitalId, visitCreationDTO);
        } catch (EntityExistsException | EntityNotFoundException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new GenericMessageResponseDTO(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new GenericMessageResponseDTO("An unexpected error occurred."));
        }

        return ResponseEntity.status(HttpStatus.OK).body(new GenericMessageResponseDTO("Successfully started a new visit."));
    }

    /**
     * Add a service to a given visit.
     * @param authorization JWT header.
     * @param addServiceDTO DTO to model request to add service.
     * @param bindingResult Result of parsing the request.
     * @return GenericMessageResponseDTO containing the result of the request.
     */
    @PostMapping("/service")
    public ResponseEntity<GenericMessageResponseDTO> addService(
        @RequestHeader(name="Authorization") String authorization,
        @RequestBody AddServiceDTO addServiceDTO,
        BindingResult bindingResult)
    {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new GenericMessageResponseDTO("Invalid request: " + ErrorConvertor.convertErrorsToString(bindingResult)));
        }

        int hospitalId = Integer.parseInt(jwtUtil.getHospitalID(authorization));

        try {
            visitService.addService(hospitalId, addServiceDTO);
        } catch (EntityNotFoundException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new GenericMessageResponseDTO(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new GenericMessageResponseDTO("An unexpected error occurred."));
        }

        return ResponseEntity.status(HttpStatus.OK).body(new GenericMessageResponseDTO("Successfully added service."));
    }

    /**
     *
     * @param authorization
     * @param addDrugDTO
     * @param bindingResult
     * @return
     */
    @PostMapping("/drug")
    public ResponseEntity<GenericMessageResponseDTO> addDrug(
        @RequestHeader(name="Authorization") String authorization,
        @RequestBody AddDrugDTO addDrugDTO,
        BindingResult bindingResult)
    {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new GenericMessageResponseDTO("Invalid request: " + ErrorConvertor.convertErrorsToString(bindingResult)));
        }

        int hospitalId = Integer.parseInt(jwtUtil.getHospitalID(authorization));

        try {
            visitService.addDrug(hospitalId, addDrugDTO);
        } catch (EntityNotFoundException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new GenericMessageResponseDTO(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new GenericMessageResponseDTO("An unexpected error occurred."));
        }

        return ResponseEntity.status(HttpStatus.OK).body(new GenericMessageResponseDTO("Successfully added service."));

    }

}
