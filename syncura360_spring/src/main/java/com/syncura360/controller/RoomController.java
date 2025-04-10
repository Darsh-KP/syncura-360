package com.syncura360.controller;

import com.syncura360.dto.ErrorConvertor;
import com.syncura360.dto.GenericMessageResponseDTO;
import com.syncura360.dto.Room.*;
import com.syncura360.security.JwtUtil;
import com.syncura360.service.RoomService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Handles room-related operations for hospitals, including adding, modifying,
 * removing, and fetching room details. The controller also ensures that actions
 * are performed by authenticated staff belonging to the relevant hospital.
 *
 * @author Darsh-KP
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/room")
public class RoomController {
    JwtUtil jwtUtil;
    RoomService roomService;

    /**
     * Constructor to inject dependencies for JWT utility and room service.
     *
     * @param jwtUtil the utility class for handling JWT token validation and extraction.
     * @param roomService the service class for managing room-related operations.
     */
    public RoomController(JwtUtil jwtUtil, RoomService roomService) {
        this.jwtUtil = jwtUtil;
        this.roomService = roomService;
    }

    /**
     * Adds a new room to the hospital.
     * This method validates the room data and creates the room for the relevant hospital,
     * which is retrieved using the JWT token.
     *
     * @param authorization the JWT authorization token to authenticate the logged-in staff.
     * @param roomFormDTO the data transfer object containing the room details to be added.
     * @param bindingResult the result of the validation of the room form data.
     * @return a {@link ResponseEntity} containing a response message with a status code indicating the result.
     */
    @PostMapping
    public ResponseEntity<GenericMessageResponseDTO> addRoom(
            @RequestHeader(name="Authorization") String authorization, @Valid @RequestBody RoomFormDTO roomFormDTO, BindingResult bindingResult) {
        // Basic DTO validation
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new GenericMessageResponseDTO("Invalid request: " + ErrorConvertor.convertErrorsToString(bindingResult)));
        }

        // Get the hospital id the logged in staff works at
        int hospitalId = Integer.parseInt(jwtUtil.getHospitalID(authorization));

        // Attempt to create new room
        try {
            roomService.createRoom(hospitalId, roomFormDTO);
        } catch (EntityExistsException | EntityNotFoundException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new GenericMessageResponseDTO(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new GenericMessageResponseDTO("An unexpected error occurred."));
        }

        // Successfully added the new room
        return ResponseEntity.status(HttpStatus.CREATED).body(new GenericMessageResponseDTO("Successfully added the room."));
    }

    /**
     * Modifies an existing room in the hospital.
     * This method validates the room data and updates the room information for the relevant hospital,
     * using the hospital ID from the JWT token.
     *
     * @param authorization the JWT authorization token to authenticate the logged-in staff.
     * @param roomUpdateDTO the data transfer object containing the updated room details.
     * @param bindingResult the result of the validation of the room update data.
     * @return a {@link ResponseEntity} containing a response message with a status code indicating the result.
     */
    @PutMapping
    public ResponseEntity<GenericMessageResponseDTO> modifyRoom(
            @RequestHeader(name="Authorization") String authorization, @Valid @RequestBody RoomUpdateDTO roomUpdateDTO, BindingResult bindingResult) {
        // Basic DTO validation
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new GenericMessageResponseDTO("Invalid request: " + ErrorConvertor.convertErrorsToString(bindingResult)));
        }

        // Get the hospital id the logged in staff works at
        int hospitalId = Integer.parseInt(jwtUtil.getHospitalID(authorization));

        // Attempt to update the existing room
        try {
            roomService.updateRoom(hospitalId, roomUpdateDTO);
        } catch (EntityExistsException | EntityNotFoundException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new GenericMessageResponseDTO(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new GenericMessageResponseDTO("An unexpected error occurred."));
        }

        // Successfully updated the existing room
        return ResponseEntity.status(HttpStatus.OK).body(new GenericMessageResponseDTO("Successfully updated the room."));
    }

    /**
     * Removes an existing room from the hospital.
     * This method validates the room deletion request and deletes the room from the relevant hospital,
     * identified using the JWT token.
     *
     * @param authorization the JWT authorization token to authenticate the logged-in staff.
     * @param roomDeletionDTO the data transfer object containing the details of the room to be deleted.
     * @param bindingResult the result of the validation of the room deletion data.
     * @return a {@link ResponseEntity} containing a response message with a status code indicating the result.
     */
    @DeleteMapping
    public ResponseEntity<GenericMessageResponseDTO> removeRoom(
            @RequestHeader(name="Authorization") String authorization, @Valid @RequestBody RoomDeletionDTO roomDeletionDTO, BindingResult bindingResult) {
        // Basic DTO validation
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new GenericMessageResponseDTO("Invalid request: " + ErrorConvertor.convertErrorsToString(bindingResult)));
        }

        // Get the hospital id the logged in staff works at
        int hospitalId = Integer.parseInt(jwtUtil.getHospitalID(authorization));

        // Attempt to delete the existing room
        try {
            roomService.deleteRoom(hospitalId, roomDeletionDTO);
        } catch (EntityExistsException | EntityNotFoundException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new GenericMessageResponseDTO(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new GenericMessageResponseDTO("An unexpected error occurred."));
        }

        // Successfully deleted the existing room
        return ResponseEntity.status(HttpStatus.OK).body(new GenericMessageResponseDTO("Successfully deleted the room."));
    }

    /**
     * Retrieves a list of rooms in the hospital.
     * This method checks if a specific room is requested and fetches the details of that room,
     * or fetches all the rooms in the hospital if no specific room is requested.
     * The hospital ID is retrieved from the JWT token.
     *
     * @param authorization the JWT authorization token to authenticate the logged-in staff.
     * @param roomFetchRequestDTO the optional data transfer object containing room search criteria.
     * @param bindingResult the result of the validation of the room fetch data.
     * @return a {@link ResponseEntity} containing a response message and a list of rooms or specific room details.
     */
    @GetMapping
    public ResponseEntity<RoomFetchContainerDTO> getRooms(
            @RequestHeader(name="Authorization") String authorization, @Valid @RequestBody(required = false) RoomFetchRequestDTO roomFetchRequestDTO, BindingResult bindingResult) {
        // Response container
        RoomFetchContainerDTO roomFetchContainerDTO = new RoomFetchContainerDTO();

        // Basic DTO validation
        if (bindingResult.hasErrors()) {
            roomFetchContainerDTO.setMessage("Invalid request: " + ErrorConvertor.convertErrorsToString(bindingResult));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(roomFetchContainerDTO);
        }

        // Get the hospital id the logged in staff works at
        int hospitalId = Integer.parseInt(jwtUtil.getHospitalID(authorization));

        // Check if a specific room is requested
        if (roomFetchRequestDTO != null && roomFetchRequestDTO.getRoomName() != null && !roomFetchRequestDTO.getRoomName().trim().isEmpty()) {
            // Fetch the room
            try {
                RoomFetchDTO roomFetchDTO = roomService.fetchRoom(hospitalId, roomFetchRequestDTO);

                // Fill the container properly
                roomFetchContainerDTO.setRoomName(roomFetchDTO.getRoomName());
                roomFetchContainerDTO.setDepartment(roomFetchDTO.getDepartment());
                roomFetchContainerDTO.setBeds(roomFetchDTO.getBeds());
                roomFetchContainerDTO.setEquipments(roomFetchDTO.getEquipments());
                roomFetchContainerDTO.setRooms(null);

                // Fetch a specific room
                return ResponseEntity.status(HttpStatus.OK).body(roomFetchContainerDTO);
            } catch (EntityExistsException | EntityNotFoundException | IllegalArgumentException e) {
                roomFetchContainerDTO.setMessage(e.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(roomFetchContainerDTO);
            } catch (Exception e) {
                roomFetchContainerDTO.setMessage("An unexpected error occurred.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(roomFetchContainerDTO);
            }
        }

        // Get all the rooms
        List<RoomFetchDTO> roomFetchDTOList = roomService.fetchRooms(hospitalId);

        // Check if no rooms found
        if (roomFetchDTOList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        // Fill the container with the fetched rooms
        roomFetchContainerDTO.setRooms(roomFetchDTOList);

        // Success, return the list of rooms
        return ResponseEntity.status(HttpStatus.OK).body(roomFetchContainerDTO);
    }
}
