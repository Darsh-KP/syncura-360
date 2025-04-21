package com.syncura360.service;

import com.syncura360.model.Bed;
import com.syncura360.model.Room;
import com.syncura360.model.RoomId;
import com.syncura360.model.enums.BedStatus;
import com.syncura360.repository.BedRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BedServiceTest {
    // Create a fake repository
    @Mock
    BedRepository bedRepository;

    // Inject fake bedRepository into bedService
    @InjectMocks
    BedService bedService;

    @Test
    void testUpdateBedsForRoom_AddBeds() {
        // Create a fake room
        Room room = mock(Room.class);

        // Simulate that room has 2 beds
        when(bedRepository.countByRoom(room)).thenReturn(2);

        // Call the method to increase to 4 beds
        bedService.updateBedsForRoom(room, 4);

        // Verify that bedRepository.save() was called 2 times to add new beds
        verify(bedRepository, times(2)).save(any(Bed.class));
    }

    @Test
    void testUpdateBedsForRoom_RemoveBeds() {
        // Create a fake room
        Room room = mock(Room.class);

        // Simulate room with 5 beds
        when(bedRepository.countByRoom(room)).thenReturn(5);

        // Simulate 3 vacant beds
        when(bedRepository.countByRoomAndStatus(eq(room), eq(BedStatus.Vacant))).thenReturn(3);

        // Create a room id for test
        RoomId roomId = new RoomId(1, "room1");
        when(room.getId()).thenReturn(roomId);

        // Call the method to decrease to 3 beds
        bedService.updateBedsForRoom(room, 3);

        // Verify that delete was called with correct arguments
        verify(bedRepository).deleteXVacantBedsInRoom(1, "room1", 2);
    }

    @Test
    void testUpdateBedsForRoom_NoChange() {
        // Create a fake room
        Room room = mock(Room.class);

        // Simulate that the current number of beds matches the desired count
        when(bedRepository.countByRoom(room)).thenReturn(3);

        // Call the method with no change in bed count
        bedService.updateBedsForRoom(room, 3);

        // Verify that no repository methods were called
        verify(bedRepository, never()).save(any());
        verify(bedRepository, never()).deleteXVacantBedsInRoom(anyInt(), anyString(), anyInt());
    }

    @Test
    void testDeleteBedsForRoom_ThrowsException_WhenNotEnoughVacant() {
        // Create a fake room
        Room room = mock(Room.class);

        // Simulate only 1 vacant bed is available
        when(bedRepository.countByRoomAndStatus(room, BedStatus.Vacant)).thenReturn(1);

        // Attempt to delete 2 beds, which should throw an exception
        assertThrows(IllegalArgumentException.class, () -> bedService.deleteBedsForRoom(room, 2));

        // Also verify no deletion was attempted
        verify(bedRepository, never()).deleteXVacantBedsInRoom(anyInt(), anyString(), anyInt());
    }

    @Test
    void testDeleteBedsForRoom_DeletesSuccessfully() {
        // Create a fake room and its ID
        Room room = mock(Room.class);
        RoomId roomId = new RoomId(1, "room1");

        // Simulate room.getId() returning our test ID
        when(room.getId()).thenReturn(roomId);

        // Simulate enough vacant beds
        when(bedRepository.countByRoomAndStatus(room, BedStatus.Vacant)).thenReturn(3);

        // Call the method to delete 2 beds
        bedService.deleteBedsForRoom(room, 2);

        // Verify the correct deletion call was made
        verify(bedRepository).deleteXVacantBedsInRoom(1, "room1", 2);
    }
}
