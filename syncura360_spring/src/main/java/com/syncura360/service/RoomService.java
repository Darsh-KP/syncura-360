package com.syncura360.service;

import com.syncura360.dto.Room.*;
import com.syncura360.model.*;
import com.syncura360.model.enums.BedStatus;
import com.syncura360.repository.BedRepository;
import com.syncura360.repository.EquipmentRepository;
import com.syncura360.repository.RoomRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class RoomService {
    RoomRepository roomRepository;
    BedRepository bedRepository;
    EquipmentRepository equipmentRepository;
    BedService bedService;
    EquipmentService equipmentService;

    public RoomService(RoomRepository roomRepository, BedRepository bedRepository, EquipmentRepository equipmentRepository, BedService bedService, EquipmentService equipmentService) {
        this.roomRepository = roomRepository;
        this.bedRepository = bedRepository;
        this.equipmentRepository = equipmentRepository;
        this.bedService = bedService;
        this.equipmentService = equipmentService;
    }

    @Transactional
    public void createRoom(int hospitalId, RoomFormDTO roomFormDTO) {
        // Check for room uniqueness
        if (roomRepository.existsById_HospitalIdAndId_RoomName(hospitalId, roomFormDTO.getRoomName())) {
            // Room already exists
            throw new EntityExistsException("Room with given name already exists.");
        }

        // Create new room
        Room room = new Room(new RoomId(hospitalId, roomFormDTO.getRoomName().trim()), roomFormDTO.getDepartment().trim());

        // Save the new room to database
        roomRepository.save(room);

        // Create each new bed
        for (int i = 0; i < roomFormDTO.getBeds(); i++) {
            // Create a new bed and attach it to the new room
            Bed newBed = new Bed(room);

            // Save the new bed to the database
            bedRepository.save(newBed);
        }

        // Check if room has new equipment
        if (roomFormDTO.getEquipments() != null && !roomFormDTO.getEquipments().isEmpty()) {
            // Create each new equipment
            for (EquipmentFormDTO equipmentFormDTO : roomFormDTO.getEquipments()) {
                // Create a new equipment and attach it to the new room
                Equipment newEquipment = new Equipment(new EquipmentId(hospitalId, room.getId().getRoomName(), equipmentFormDTO.getSerialNo().trim()), equipmentFormDTO.getName().trim());

                // Save the new equipment to the database
                equipmentRepository.save(newEquipment);
            }
        }
    }

    @Transactional
    public void updateRoom(int hospitalId, RoomUpdateDTO roomUpdateDTO) {
        // Find the room if it already exists
        Optional<Room> optionalRoom = roomRepository.findById_HospitalIdAndId_RoomName(hospitalId, roomUpdateDTO.getRoomName().trim());
        if (optionalRoom.isEmpty()) {
            // Room not found
            throw new EntityNotFoundException("Room with given name does not exist.");
        }

        // Extract the room
        Room room = optionalRoom.get();

        // Update the department
        room.setDepartment(roomUpdateDTO.getDepartment().trim());

        // Update the beds
        bedService.updateBedsForRoom(room, roomUpdateDTO.getBeds());

        // Update the equipments
        equipmentService.setEquipmentsForRoom(hospitalId, room.getId().getRoomName(), roomUpdateDTO.getEquipments());

        // Save the room
        roomRepository.save(room);
    }

    @Transactional
    public void deleteRoom(int hospitalId, RoomDeletionDTO roomDeletionDTO) {
        // Find the room if it already exists
        Optional<Room> optionalRoom = roomRepository.findById_HospitalIdAndId_RoomName(hospitalId, roomDeletionDTO.getRoomName().trim());
        if (optionalRoom.isEmpty()) {
            // Room not found
            throw new EntityNotFoundException("Room with given name does not exist.");
        }

        // Extract the room
        Room room = optionalRoom.get();

        // Check if any beds are occupied
        int occupiedBeds = bedRepository.countByRoomAndStatus(room, BedStatus.Occupied);

        if (occupiedBeds > 0) {
            throw new IllegalArgumentException("There are occupied beds in the room.");
        }

        // Delete all the beds in the room
        bedRepository.deleteAllByRoom(room);

        // Delete all the equipments in the room
        equipmentService.deleteEquipmentsForRoom(hospitalId, roomDeletionDTO.getRoomName().trim());

        // Delete the room
        roomRepository.delete(room);
    }

    public List<RoomFetchDTO> fetchRooms(int hospitalId) {
        // Get a list of rooms
        return roomRepository.findById_HospitalId(hospitalId).stream().map(
                room -> fetchRoom(hospitalId, new RoomFetchRequestDTO(room.getId().getRoomName().trim()))).toList();
    }

    public RoomFetchDTO fetchRoom(int hospitalId, RoomFetchRequestDTO roomFetchRequestDTO) {
        // Find the room if it already exists
        Optional<Room> optionalRoom = roomRepository.findById_HospitalIdAndId_RoomName(hospitalId, roomFetchRequestDTO.getRoomName().trim());
        if (optionalRoom.isEmpty()) {
            // Room not found
            throw new EntityNotFoundException("Room with given name does not exist.");
        }

        // Extract the room
        Room room = optionalRoom.get();

        // Get the numbers of beds in the room
        int totalBeds = bedRepository.countByRoom(room);
        int bedsVacant = bedRepository.countByRoomAndStatus(room, BedStatus.Vacant);
        int bedsOccupied = bedRepository.countByRoomAndStatus(room, BedStatus.Occupied);
        int bedsMaintenance = bedRepository.countByRoomAndStatus(room, BedStatus.Under_Maintenance);

        // Get all the equipments
        List<EquipmentFetchDTO> fetchedEquipments = equipmentRepository.findById_HospitalIdAndId_RoomName(hospitalId, room.getId().getRoomName().trim()).stream().map(equipment ->
                new EquipmentFetchDTO(equipment.getId().getSerialNo().trim(), equipment.getName().trim(), equipment.getUnderMaintenance())).toList();

        return new RoomFetchDTO(
                room.getId().getRoomName().trim(),
                room.getDepartment().trim(),
                totalBeds,
                bedsVacant,
                bedsOccupied,
                bedsMaintenance,
                fetchedEquipments
        );
    }
}
