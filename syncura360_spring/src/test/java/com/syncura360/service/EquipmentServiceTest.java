package com.syncura360.service;

import com.syncura360.dto.Room.EquipmentUpdateDTO;
import com.syncura360.model.Equipment;
import com.syncura360.model.EquipmentId;
import com.syncura360.repository.EquipmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EquipmentServiceTest {
    // Create fake repository
    @Mock
    EquipmentRepository equipmentRepository;

    // Inject fake equipmentRepository into equipmentService
    @InjectMocks
    EquipmentService equipmentService;

    @Test
    void testSetEquipmentsForRoom_NullEquipments_ShouldDeleteAll() {
        // Create room values for test
        int hospitalId = 1;
        String roomName = "Room1";

        // Call the method to remove all equipments, due to passing null
        equipmentService.setEquipmentsForRoom(hospitalId, roomName, null);

        // Verify that the correct deletion call was made
        verify(equipmentRepository, times(1)).deleteAllById_HospitalIdAndId_RoomName(eq(hospitalId), eq("Room1"));
    }

    @Test
    void testSetEquipmentsForRoom_EmptyEquipments_ShouldDeleteAll() {
        // Create room values for test
        int hospitalId = 1;
        String roomName = "Room1";

        // Call the method to remove all equipments, due to passing empty list
        equipmentService.setEquipmentsForRoom(hospitalId, roomName, new ArrayList<>());

        // Verify that the correct deletion call was made
        verify(equipmentRepository, times(1)).deleteAllById_HospitalIdAndId_RoomName(eq(hospitalId), eq("Room1"));
    }

    @Test
    void testSetEquipmentsForRoom_WithEquipments_ShouldSaveAndDeleteExtra() {
        // Create room values for test
        int hospitalId = 1;
        String roomName = "Room1";

        // Simulate a EquipmentUpdateDTO
        EquipmentUpdateDTO dto1 = mock(EquipmentUpdateDTO.class);
        when(dto1.getSerialNo()).thenReturn("Serial1");
        when(dto1.getName()).thenReturn("Equipment1");
        when(dto1.getInMaintenance()).thenReturn(true);

        // Create a input list
        List<EquipmentUpdateDTO> inputEquipments = List.of(dto1);

        // Simulate equipments in the database
        Equipment existingEquipment = new Equipment(new EquipmentId(hospitalId, "Room1", "Serial2"), "Equipment2");
        List<Equipment> dbEquipments = List.of(existingEquipment);

        when(equipmentRepository.findById_HospitalIdAndId_RoomName(eq(hospitalId), eq("Room1")))
                .thenReturn(dbEquipments);

        // Call the method to set new equipments
        equipmentService.setEquipmentsForRoom(hospitalId, roomName, inputEquipments);

        // Check if the save was called, and capture the equipment for testing
        ArgumentCaptor<Equipment> equipmentCaptor = ArgumentCaptor.forClass(Equipment.class);
        verify(equipmentRepository).save(equipmentCaptor.capture());

        // Verify what equipment was saved
        Equipment saved = equipmentCaptor.getValue();
        assertEquals("Serial1", saved.getId().getSerialNo());
        assertEquals("Equipment1", saved.getName());
        assertTrue(saved.getUnderMaintenance());

        // Verify extra equipment deletion
        verify(equipmentRepository).delete(existingEquipment);
    }

    @Test
    void testDeleteEquipmentsForRoom_DeleteSuccessfully() {
        // Create room values for test
        int hospitalId = 1;
        String roomName = "Room1";

        // Call the method
        equipmentService.deleteEquipmentsForRoom(hospitalId, roomName);

        // Verify that the method was called correctly
        verify(equipmentRepository).deleteAllById_HospitalIdAndId_RoomName(eq(hospitalId), eq("Room1"));
    }
}