package com.syncura360.service;

import com.syncura360.dto.Room.*;
import com.syncura360.model.*;
import com.syncura360.model.enums.BedStatus;
import com.syncura360.repository.BedRepository;
import com.syncura360.repository.EquipmentRepository;
import com.syncura360.repository.RoomRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomServiceTest {
    // Create fake repositories and services
    @Mock
    RoomRepository roomRepository;

    @Mock
    BedRepository bedRepository;

    @Mock
    EquipmentRepository equipmentRepository;

    @Mock
    BedService bedService;

    @Mock
    EquipmentService equipmentService;

    // Inject RoomService with fake repositories and services
    @InjectMocks
    RoomService roomService;

    @Test
    void testCreateRoom_RoomAlreadyExists() {
        // Create fake RoomFormDTO with necessary values
        int hospitalId = 1;
        RoomFormDTO roomFormDTO = mock(RoomFormDTO.class);
        when(roomFormDTO.getRoomName()).thenReturn("Room1");

        // Simulate that the room already exists
        when(roomRepository.existsById_HospitalIdAndId_RoomName(hospitalId, "Room1")).thenReturn(true);

        // Try to create a room that already exists, should throw an exception
        assertThrows(EntityExistsException.class, () -> roomService.createRoom(hospitalId, roomFormDTO));
    }

    @Test
    void testCreateRoom_Success() {
        // Create fake RoomFormDTO with necessary values
        int hospitalId = 1;
        RoomFormDTO roomFormDTO = mock(RoomFormDTO.class);
        when(roomFormDTO.getRoomName()).thenReturn("Room1");
        when(roomFormDTO.getDepartment()).thenReturn("Department1");
        when(roomFormDTO.getBeds()).thenReturn(5);
        when(roomFormDTO.getEquipments()).thenReturn(List.of());

        // Simulate the room doesn't exist yet
        when(roomRepository.existsById_HospitalIdAndId_RoomName(hospitalId, "Room1")).thenReturn(false);

        // Create a room
        roomService.createRoom(hospitalId, roomFormDTO);

        // Verify that save was called for the room, beds, and equipments
        verify(roomRepository).save(any(Room.class));
        verify(bedRepository, times(5)).save(any(Bed.class));
        verify(equipmentRepository, never()).save(any(Equipment.class));
    }

    @Test
    void testUpdateRoom_RoomNotFound() {
        // Create fake RoomUpdateDTO with necessary values
        int hospitalId = 1;
        RoomUpdateDTO roomUpdateDTO = mock(RoomUpdateDTO.class);
        when(roomUpdateDTO.getRoomName()).thenReturn("Room1");

        // Simulate that the room does not exist
        when(roomRepository.findById_HospitalIdAndId_RoomName(hospitalId, "Room1")).thenReturn(Optional.empty());

        // Try to update a non-existent room, should throw an exception
        assertThrows(EntityNotFoundException.class, () -> roomService.updateRoom(hospitalId, roomUpdateDTO));
    }

    @Test
    void testUpdateRoom_Success() {
        // Create fake RoomUpdateDTO with necessary values
        int hospitalId = 1;
        RoomUpdateDTO roomUpdateDTO = mock(RoomUpdateDTO.class);
        when(roomUpdateDTO.getRoomName()).thenReturn("Room1");
        when(roomUpdateDTO.getDepartment()).thenReturn("Neurology");
        when(roomUpdateDTO.getBeds()).thenReturn(5);

        // Create fake Room object to simulate an existing room
        Room room = mock(Room.class);
        when(roomRepository.findById_HospitalIdAndId_RoomName(hospitalId, "Room1")).thenReturn(Optional.of(room));
        when(room.getId()).thenReturn(new RoomId(1, "Room1"));

        // Call the method to update room
        roomService.updateRoom(hospitalId, roomUpdateDTO);

        // Verify that save was called for the room, and methods for updating beds and equipments
        verify(roomRepository).save(room);
        verify(bedService).updateBedsForRoom(eq(room), eq(5));
        verify(equipmentService).setEquipmentsForRoom(eq(hospitalId), eq("Room1"), any());
    }

    @Test
    void testDeleteRoom_RoomNotFound() {
        // Create fake RoomDeletionDTO with necessary values
        int hospitalId = 1;
        RoomDeletionDTO roomDeletionDTO = mock(RoomDeletionDTO.class);
        when(roomDeletionDTO.getRoomName()).thenReturn("Room1");

        // Simulate that the room does not exist
        when(roomRepository.findById_HospitalIdAndId_RoomName(hospitalId, "Room1")).thenReturn(Optional.empty());

        // Try to delete a non-existent room, should throw an exception
        assertThrows(EntityNotFoundException.class, () -> roomService.deleteRoom(hospitalId, roomDeletionDTO));
    }

    @Test
    void testDeleteRoom_RoomHasOccupiedBeds() {
        // Create fake RoomDeletionDTO with necessary values
        int hospitalId = 1;
        RoomDeletionDTO roomDeletionDTO = mock(RoomDeletionDTO.class);
        when(roomDeletionDTO.getRoomName()).thenReturn("Room1");

        // Create fake Room object to simulate the existing room
        Room room = mock(Room.class);
        when(roomRepository.findById_HospitalIdAndId_RoomName(hospitalId, "Room1")).thenReturn(Optional.of(room));

        // Simulate that the room has occupied beds
        when(bedRepository.countByRoomAndStatus(room, BedStatus.Occupied)).thenReturn(1);

        // Try to delete a room with occupied beds, should throw an exception
        assertThrows(IllegalArgumentException.class, () -> roomService.deleteRoom(hospitalId, roomDeletionDTO));
    }

    @Test
    void testDeleteRoom_Success() {
        // Create fake RoomDeletionDTO with necessary values
        int hospitalId = 1;
        RoomDeletionDTO roomDeletionDTO = mock(RoomDeletionDTO.class);
        when(roomDeletionDTO.getRoomName()).thenReturn("Room1");

        // Create fake Room object to simulate the existing room
        Room room = mock(Room.class);
        when(roomRepository.findById_HospitalIdAndId_RoomName(hospitalId, "Room1")).thenReturn(Optional.of(room));

        // Simulate that the room has no occupied beds
        when(bedRepository.countByRoomAndStatus(room, BedStatus.Occupied)).thenReturn(0);

        // Call the method to delete room
        roomService.deleteRoom(hospitalId, roomDeletionDTO);

        // Verify that room, beds, and equipments were deleted
        verify(bedRepository).deleteAllByRoom(room);
        verify(equipmentService).deleteEquipmentsForRoom(hospitalId, "Room1");
        verify(roomRepository).delete(room);
    }

    @Test
    void testFetchRooms() {
        // Hospital ID for the test
        int hospitalId = 1;

        // Create and mock a Room object
        Room room = mock(Room.class);
        RoomId roomId = mock(RoomId.class);

        // Mock the behavior of Room and RoomId to return a valid room name and department
        when(room.getId()).thenReturn(roomId);
        when(roomId.getRoomName()).thenReturn("Room1");
        when(room.getDepartment()).thenReturn("Department1");

        // Mock the repository to return a list with the mocked room
        when(roomRepository.findById_HospitalId(hospitalId)).thenReturn(List.of(room));

        // Mock the repository to simulate finding a room by name in fetchRoom
        when(roomRepository.findById_HospitalIdAndId_RoomName(hospitalId, "Room1")).thenReturn(Optional.of(room));

        // Call the method to fetch rooms
        List<RoomFetchDTO> rooms = roomService.fetchRooms(hospitalId);

        // Verify that the room fetching methods were called
        verify(roomRepository).findById_HospitalId(hospitalId);
        verify(roomRepository).findById_HospitalIdAndId_RoomName(hospitalId, "Room1");

        // Assert that the result list is correct
        assertEquals(1, rooms.size());
        assertEquals("Room1", rooms.getFirst().getRoomName());
        assertEquals("Department1", rooms.getFirst().getDepartment());
    }

    @Test
    void testFetchRoom_RoomNotFound() {
        // Create fake RoomFetchRequestDTO with necessary values
        int hospitalId = 1;
        RoomFetchRequestDTO roomFetchRequestDTO = mock(RoomFetchRequestDTO.class);
        when(roomFetchRequestDTO.getRoomName()).thenReturn("Room1");

        // Simulate that the room does not exist
        when(roomRepository.findById_HospitalIdAndId_RoomName(hospitalId, "Room1")).thenReturn(Optional.empty());

        // Try to fetch a non-existent room, should throw an exception
        assertThrows(EntityNotFoundException.class, () -> roomService.fetchRoom(hospitalId, roomFetchRequestDTO));
    }

    @Test
    void testFetchRoom_Success() {
        // Mock the input DTO
        int hospitalId = 1;
        RoomFetchRequestDTO roomFetchRequestDTO = mock(RoomFetchRequestDTO.class);
        when(roomFetchRequestDTO.getRoomName()).thenReturn("Room1");

        // Mock the RoomId
        RoomId roomId = mock(RoomId.class);
        when(roomId.getRoomName()).thenReturn("Room1");

        // Create and mock the Room object
        Room room = mock(Room.class);
        when(room.getId()).thenReturn(roomId);
        when(room.getDepartment()).thenReturn("Cardiology");

        // Mock the RoomRepository to return the Room object
        when(roomRepository.findById_HospitalIdAndId_RoomName(hospitalId, "Room1")).thenReturn(Optional.of(room));

        // Mock the BedRepository methods
        when(bedRepository.countByRoom(room)).thenReturn(10);  // Assume 10 total beds
        when(bedRepository.countByRoomAndStatus(room, BedStatus.Vacant)).thenReturn(5);  // 5 vacant beds
        when(bedRepository.countByRoomAndStatus(room, BedStatus.Occupied)).thenReturn(3);  // 3 occupied beds
        when(bedRepository.countByRoomAndStatus(room, BedStatus.Under_Maintenance)).thenReturn(2);  // 2 under maintenance

        // Mock the EquipmentRepository to return some equipment
        Equipment equipment = mock(Equipment.class);
        EquipmentId equipmentId = mock(EquipmentId.class);
        when(equipment.getId()).thenReturn(equipmentId);
        when(equipmentId.getSerialNo()).thenReturn("E12345");
        when(equipment.getName()).thenReturn("ECG Machine");
        when(equipment.getUnderMaintenance()).thenReturn(false);

        List<Equipment> equipments = List.of(equipment);
        when(equipmentRepository.findById_HospitalIdAndId_RoomName(hospitalId, "Room1")).thenReturn(equipments);

        // Call the fetchRoom method
        RoomFetchDTO roomFetchDTO = roomService.fetchRoom(hospitalId, roomFetchRequestDTO);

        // Verify that the room was fetched from the repository
        verify(roomRepository).findById_HospitalIdAndId_RoomName(hospitalId, "Room1");

        // Verify the expected counts for beds
        assertEquals(10, roomFetchDTO.getBeds());
        assertEquals(5, roomFetchDTO.getBedsVacant());
        assertEquals(3, roomFetchDTO.getBedsOccupied());
        assertEquals(2, roomFetchDTO.getBedsMaintenance());

        // Verify the equipment mapping
        assertEquals(1, roomFetchDTO.getEquipments().size());
        EquipmentFetchDTO equipmentFetchDTO = roomFetchDTO.getEquipments().getFirst();
        assertEquals("E12345", equipmentFetchDTO.getSerialNo());
        assertEquals("ECG Machine", equipmentFetchDTO.getName());
        assertFalse(equipmentFetchDTO.isInMaintenance());

        // Verify the other details
        assertEquals("Room1", roomFetchDTO.getRoomName());
        assertEquals("Cardiology", roomFetchDTO.getDepartment());
    }
}