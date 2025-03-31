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

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/room")
public class RoomController {
    JwtUtil jwtUtil;
    RoomService roomService;

    public RoomController(JwtUtil jwtUtil, RoomService roomService) {
        this.jwtUtil = jwtUtil;
        this.roomService = roomService;
    }

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

    @PutMapping
    public ResponseEntity<GenericMessageResponseDTO> modifyRoom(
            @RequestHeader(name="Authorization") String authorization, @Valid @RequestBody RoomFormDTO roomFormDTO, BindingResult bindingResult) {
        // Basic DTO validation
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new GenericMessageResponseDTO("Invalid request: " + ErrorConvertor.convertErrorsToString(bindingResult)));
        }

        // Get the hospital id the logged in staff works at
        int hospitalId = Integer.parseInt(jwtUtil.getHospitalID(authorization));

        // Attempt to update the existing room
        try {
            roomService.updateRoom(hospitalId, roomFormDTO);
        } catch (EntityExistsException | EntityNotFoundException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new GenericMessageResponseDTO(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new GenericMessageResponseDTO("An unexpected error occurred."));
        }

        // Successfully updated the existing room
        return ResponseEntity.status(HttpStatus.OK).body(new GenericMessageResponseDTO("Successfully updated the room."));
    }

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

    @GetMapping
    public ResponseEntity<RoomFetchContainerDTO> getRooms(
            @RequestHeader(name="Authorization") String authorization, @Valid @RequestBody RoomFetchRequestDTO roomFetchRequestDTO, BindingResult bindingResult) {
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
        if (roomFetchRequestDTO.getRoomName() != null && !roomFetchRequestDTO.getRoomName().trim().isEmpty()) {
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
