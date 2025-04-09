package com.syncura360.service;

import com.syncura360.dto.Room.EquipmentUpdateDTO;
import com.syncura360.model.Equipment;
import com.syncura360.model.EquipmentId;
import com.syncura360.repository.EquipmentRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Service class responsible for managing equipment in a hospital room.
 *
 * @author Darsh-KP
 */
@Service
public class EquipmentService {
    EquipmentRepository equipmentRepository;

    /**
     * Constructor for initializing {@link EquipmentService} with required dependencies.
     * Uses constructor injection for necessary components.
     *
     * @param equipmentRepository The repository used for equipment operations.
     */
    public EquipmentService(EquipmentRepository equipmentRepository) {
        this.equipmentRepository = equipmentRepository;
    }

    /**
     * Sets the equipment for a specific room in the hospital.
     * If the equipment list is empty or null, it deletes the existing equipment in the room.
     * Otherwise, it updates the room's equipment.
     *
     * @param hospitalId The ID of the hospital.
     * @param roomName The name of the room to set equipment for.
     * @param equipments The list of equipment to be set in the room.
     */
    public void setEquipmentsForRoom(int hospitalId, String roomName, List<EquipmentUpdateDTO> equipments) {
        // Check if there are any equipments
        if (equipments == null || equipments.isEmpty()) {
            deleteEquipmentsForRoom(hospitalId, roomName.trim());
            return;
        }

        // Store all the new equipment in a list
        Set<Equipment> newEquipments = new HashSet<>();

        // For each equipment, update it in the database
        for (EquipmentUpdateDTO equipment : equipments) {
            // Extract the equipment
            Equipment newEquipment = new Equipment(new EquipmentId(hospitalId, roomName.trim(), equipment.getSerialNo().trim()), equipment.getName().trim());

            // Update the in maintenance status
            newEquipment.setUnderMaintenance(equipment.getInMaintenance());

            // Add new equipment to list
            newEquipments.add(newEquipment);

            // Update or create the equipment
            equipmentRepository.save(newEquipment);
        }

        // Get all the equipment in the room
        List<Equipment> allEquipments = equipmentRepository.findById_HospitalIdAndId_RoomName(hospitalId, roomName.trim());

        // Delete any equipment that is not present in the new equipment list
        for (Equipment dbEquipment : allEquipments) {
            // Check if a database equipment is present in the new equipment list
            if (!newEquipments.contains(dbEquipment)) {
                // If not found, delete it from database
                equipmentRepository.delete(dbEquipment);
            }
        }
    }

    /**
     * Deletes all equipment in a specific room for a given hospital.
     *
     * @param hospitalId The ID of the hospital.
     * @param roomName The name of the room to delete the equipment from.
     */
    public void deleteEquipmentsForRoom(Integer hospitalId, String roomName) {
        equipmentRepository.deleteAllById_HospitalIdAndId_RoomName(hospitalId, roomName.trim());
    }
}
