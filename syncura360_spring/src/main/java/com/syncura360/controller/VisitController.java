package com.syncura360.controller;

import com.syncura360.dto.Drug.DrugFetchListDTO;
import com.syncura360.dto.ErrorConvertor;
import com.syncura360.dto.GenericMessageResponseDTO;
import com.syncura360.dto.Visit.*;
import com.syncura360.security.JwtUtil;
import com.syncura360.service.VisitService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.NoSuchElementException;

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

        return ResponseEntity.status(HttpStatus.CREATED).body(new GenericMessageResponseDTO("Successfully started a new visit."));
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
        } catch (EntityNotFoundException | IllegalArgumentException | DateTimeParseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new GenericMessageResponseDTO(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new GenericMessageResponseDTO("An unexpected error occurred."));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(new GenericMessageResponseDTO("Successfully added service."));
    }

    /**
     * Add a drug to a given visit.
     * @param authorization JWT header.
     * @param addDrugDTO DTO to model request to add drug.
     * @param bindingResult Result of parsing the request.
     * @return GenericMessageResponseDTO containing the result of the request.
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
        } catch (EntityNotFoundException | IllegalArgumentException | DateTimeParseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new GenericMessageResponseDTO(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new GenericMessageResponseDTO("An unexpected error occurred."));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(new GenericMessageResponseDTO("Successfully added drug."));
    }

    @PostMapping("/room")
    public ResponseEntity<GenericMessageResponseDTO> addRoom(
        @RequestHeader(name="Authorization") String authorization,
        @RequestBody AddRoomDTO addRoomDTO,
        BindingResult bindingResult)
    {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new GenericMessageResponseDTO("Invalid request: " + ErrorConvertor.convertErrorsToString(bindingResult)));
        }

        int hospitalId = Integer.parseInt(jwtUtil.getHospitalID(authorization));

        try {
            visitService.addRoom(hospitalId, addRoomDTO);
        } catch (EntityExistsException | EntityNotFoundException | IllegalArgumentException | DateTimeParseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new GenericMessageResponseDTO(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new GenericMessageResponseDTO("An unexpected error occurred."));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(new GenericMessageResponseDTO("Successfully added room to visit."));
    }

    @DeleteMapping("/room")
    public ResponseEntity<GenericMessageResponseDTO> removeRoom(
            @RequestHeader(name="Authorization") String authorization,
            @RequestBody DeleteRoomDTO deleteRoomDTO,
            BindingResult bindingResult)
    {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new GenericMessageResponseDTO("Invalid request: " + ErrorConvertor.convertErrorsToString(bindingResult)));
        }

        int hospitalId = Integer.parseInt(jwtUtil.getHospitalID(authorization));

        try {
            visitService.removeRoom(hospitalId, deleteRoomDTO);
        } catch (EntityExistsException | EntityNotFoundException | IllegalArgumentException | DateTimeParseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new GenericMessageResponseDTO(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new GenericMessageResponseDTO("An unexpected error occurred."));
        }

        return ResponseEntity.status(HttpStatus.OK).body(new GenericMessageResponseDTO("Successfully removed patient from room."));
    }

    @GetMapping
    public ResponseEntity<VisitListDTO> getVisits(@RequestHeader(name="Authorization") String authorization) {

        int hospitalId = Integer.parseInt(jwtUtil.getHospitalID(authorization));

        VisitListDTO response;
        try {
            response = new VisitListDTO(visitService.getVisits(hospitalId));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new VisitListDTO(new ArrayList<>()));
        }

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/doctors")
    public ResponseEntity<DoctorListDTO> getDoctors(@RequestHeader(name="Authorization") String authorization) {

        int hospitalId = Integer.parseInt(jwtUtil.getHospitalID(authorization));

        DoctorListDTO response;
        try {
            response = new DoctorListDTO(visitService.getDoctors(hospitalId));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new DoctorListDTO(new ArrayList<>()));
        }

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/services")
    public ResponseEntity<ServiceListDTO> getServices(@RequestHeader(name="Authorization") String authorization) {

        int hospitalId = Integer.parseInt(jwtUtil.getHospitalID(authorization));

        ServiceListDTO response;
        try {
            response = new ServiceListDTO(visitService.getServices(hospitalId));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new ServiceListDTO(new ArrayList<>()));
        }

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/drugs")
    public ResponseEntity<DrugFetchListDTO> getDrugs(@RequestHeader(name="Authorization") String authorization) {

        int hospitalId = Integer.parseInt(jwtUtil.getHospitalID(authorization));

        DrugFetchListDTO response;
        try {
            response = new DrugFetchListDTO(visitService.getDrugs(hospitalId));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new DrugFetchListDTO(new ArrayList<>()));
        }

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/note")
    public ResponseEntity<GenericMessageResponseDTO> editNote(
        @RequestHeader(name="Authorization") String authorization,
        @RequestBody NoteDTO noteDTO,
        BindingResult bindingResult)
    {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new GenericMessageResponseDTO("Invalid request: " + ErrorConvertor.convertErrorsToString(bindingResult)));
        }

        int hospitalId = Integer.parseInt(jwtUtil.getHospitalID(authorization));

        try {
            visitService.editNote(hospitalId, noteDTO);
        } catch (EntityNotFoundException | IllegalArgumentException | DateTimeParseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new GenericMessageResponseDTO(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new GenericMessageResponseDTO("An unexpected error occurred."));
        }

        return ResponseEntity.status(HttpStatus.OK).body(new GenericMessageResponseDTO("Successfully edited note."));
    }

}
