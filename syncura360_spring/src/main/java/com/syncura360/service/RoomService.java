package com.syncura360.service;

import com.syncura360.dto.Room.EquipmentFormDTO;
import com.syncura360.dto.Room.RoomFormDTO;
import com.syncura360.model.*;
import com.syncura360.repository.BedRepository;
import com.syncura360.repository.EquipmentRepository;
import com.syncura360.repository.RoomRepository;
import jakarta.persistence.EntityExistsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class RoomService {
    RoomRepository roomRepository;
    BedRepository bedRepository;
    EquipmentRepository equipmentRepository;
    BedService bedService;

    public RoomService(RoomRepository roomRepository, BedRepository bedRepository, EquipmentRepository equipmentRepository, BedService bedService) {
        this.roomRepository = roomRepository;
        this.bedRepository = bedRepository;
        this.equipmentRepository = equipmentRepository;
        this.bedService = bedService;
    }

    @Transactional
    public void createRoom(int hospitalId, RoomFormDTO roomFormDTO) {
        // Check for room uniqueness
        if (roomRepository.existsById_HospitalIdAndId_RoomName(hospitalId, roomFormDTO.getRoomName())) {
            // Room already exists
            throw new EntityExistsException("Room with given name already exists.");
        }

        // Create new room
        Room room = new Room(new RoomId(hospitalId, roomFormDTO.getRoomName()), roomFormDTO.getRoomName());

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
                Equipment newEquipment = new Equipment(new EquipmentId(hospitalId, room.getId().getRoomName(), equipmentFormDTO.getSerialNo()), equipmentFormDTO.getName());

                // Save the new equipment to the database
                equipmentRepository.save(newEquipment);
            }
        }
    }

    // TODO
    @Transactional
    public void updateRoom(int hospitalId, RoomFormDTO roomFormDTO) {
        // Find the room if it already exists
        Optional<Room> optionalRoom = roomRepository.findById_HospitalIdAndId_RoomName(hospitalId, roomFormDTO.getRoomName());
        if (optionalRoom.isEmpty()) {
            // Room not found
            throw new EntityExistsException("Room with given name does not exist.");
        }

        // Extract the room
        Room room = optionalRoom.get();

        // Update the department
        room.setDepartment(roomFormDTO.getDepartment().trim());

        // Update the beds
        bedService.updateBedsForRoom(room, roomFormDTO.getBeds());

        // Update the equipments


        // Save the room
    }

    // TODO
    public void deleteRoom(int hospitalId, RoomFormDTO roomFormDTO) {

    }

    // TODO
    public void fetchRooms() {

    }

    // TODO
    public void fetchRoom() {

    }
}
