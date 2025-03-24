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

@Service
public class RoomService {
    RoomRepository roomRepository;
    BedRepository bedRepository;
    EquipmentRepository equipmentRepository;

    public RoomService(RoomRepository roomRepository, BedRepository bedRepository, EquipmentRepository equipmentRepository) {
        this.roomRepository = roomRepository;
        this.bedRepository = bedRepository;
        this.equipmentRepository = equipmentRepository;
    }

    @Transactional
    public void createRoom(int hospitalId, RoomFormDTO roomFormDTO) {
        // Check for room uniqueness
        if (roomRepository.existsById_HospitalIdAndId_RoomName(hospitalId, roomFormDTO.getRoomName())) {
            // Room already exists
            throw new EntityExistsException("Room with given name already exists.");
        }

        // Create new room
        Room room = new Room(new RoomId(hospitalId, roomFormDTO.getRoomName()));

        // Save the new room to database
        roomRepository.save(room);

        // Create each new bed
        for (int i = 0; i < roomFormDTO.getBeds(); i++) {
            // Create a new bed and attach it to the new room
            Bed newBed = new Bed(room);

            // Save thew new bed to the database
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
    public void updateRoom(Room room) {

    }

    // TODO
    public void deleteRoom(Room room) {

    }

    // TODO
    public void fetchRooms() {

    }
}
