package com.syncura360.controller;

import com.syncura360.dto.Drug.DrugFetchListDTO;
import com.syncura360.dto.ErrorConvertor;
import com.syncura360.dto.GenericMessageResponseDTO;
import com.syncura360.dto.Visit.*;
import com.syncura360.security.JwtUtil;
import com.syncura360.service.VisitService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
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
     * Attempt to discharge a patient.
     * @param authorization JWT header.
     * @param dischargeDTO DTO to model incoming discharge request.
     * @param bindingResult Result of parsing request.
     * @return GenericMessageResponseDTO indicating result of action.
     */
    @PostMapping("/discharge")
    public ResponseEntity<GenericMessageResponseDTO> discharge(
        @RequestHeader(name="Authorization") String authorization,
        @RequestBody DischargeDTO dischargeDTO,
        BindingResult bindingResult)
    {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new GenericMessageResponseDTO("Invalid request: " + ErrorConvertor.convertErrorsToString(bindingResult)));
        }

        int hospitalId = Integer.parseInt(jwtUtil.getHospitalID(authorization));

        try {
            visitService.discharge(hospitalId, dischargeDTO);
        } catch (EntityExistsException | EntityNotFoundException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new GenericMessageResponseDTO(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new GenericMessageResponseDTO("An unexpected error occurred."));
        }

        return ResponseEntity.status(HttpStatus.OK).body(new GenericMessageResponseDTO("Successfully discharged patient."));
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

    /**
     * Attempt to add a room to a visit.
     * @param authorization JWT header.
     * @param addRoomDTO DTO to model incoming room addition request.
     * @param bindingResult Result of parsing request.
     * @return GenericMessageResponseDTO indicating result of action.
     */
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

    /**
     * Attempts to remove a patient from a room.
     * @param authorization JWT header.
     * @param deleteRoomDTO DTO to model removal request.
     * @param bindingResult Result of parsing request.
     * @return GenericMessageResponseDTO indicating result of request.
     */
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

    /**
     * Attempt to retrieve details of a given visit.
     * @param authorization JWT header.
     * @param patientId ID of patient for visit lookup.
     * @param dateTime Admission date time for visit lookup.
     * @return VisitDetailsDTO DTO to containerize visit timeline and visit note.
     */
    @GetMapping("/{patientId}/{dateTime}")
    public ResponseEntity<VisitDetailsDTO> getVisitDetails(
            @RequestHeader(name="Authorization") String authorization,
            @NotNull @PathVariable("patientId") int patientId,
            @NotNull @PathVariable("dateTime") String dateTime)
    {
        int hospitalId = Integer.parseInt(jwtUtil.getHospitalID(authorization));

        VisitDetailsDTO response;
        try {
            response = new VisitDetailsDTO();
            response.setTimeline(visitService.getTimeline(hospitalId, patientId));
            response.setVisitNote(visitService.getNote(hospitalId, patientId));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new VisitDetailsDTO());
        }

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Get a list of currently ongoing visits.
      * @param authorization JWT header.
     * @return VisitListDTO to model response.
     */
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

    /**
     * Get a list of doctors at users hospital.
     * @param authorization JWT header.
     * @return DoctorListDTO to model response.
     */
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

    /**
     * Get a list of doctors at users hospital.
     * @param authorization JWT header.
     * @return DoctorListDTO to model response.
     */
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

    /**
     * Get a list of drugs at users hospital.
     * @param authorization JWT header.
     * @return DrugFetchListDTO to model response.
     */
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

    /**
     * Get list of available rooms.
     * @param authorization JWT header.
     * @return RoomListDTO to model room list.
     */
    @GetMapping("/rooms")
    public ResponseEntity<RoomListDTO> getRooms(@RequestHeader(name="Authorization") String authorization) {

        int hospitalId = Integer.parseInt(jwtUtil.getHospitalID(authorization));

        RoomListDTO response;
        try {
            response = new RoomListDTO(visitService.getRooms(hospitalId));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new RoomListDTO(new ArrayList<>()));
        }

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Attempt to modify visit note.
     * @param authorization JWT header.
     * @param noteDTO DTO to model incoming note request.
     * @param bindingResult Result of parsing request.
     * @return GenericMessageResponseDTO to indicate result of request.
     */
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