package com.syncura360.service;

import com.syncura360.model.Bed;
import com.syncura360.model.Room;
import com.syncura360.repository.BedRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

        // Verifies that bedRepository.save() was called 2 times to add new beds
        verify(bedRepository, times(2)).save(any(Bed.class));
    }
}
