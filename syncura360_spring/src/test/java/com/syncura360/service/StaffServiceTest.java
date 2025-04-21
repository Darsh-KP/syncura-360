package com.syncura360.service;

import com.syncura360.dto.Staff.StaffSettingFetch;
import com.syncura360.model.Staff;
import com.syncura360.model.enums.Role;
import com.syncura360.repository.StaffRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StaffServiceTest {
    // Create a fake repository
    @Mock
    StaffRepository staffRepository;

    // Inject fake staffRepository into staffService
    @InjectMocks
    StaffService staffService;

    @Test
    void testGetStaffSetting_StaffExists_ReturnsSetting() {
        // Create a mock Staff object
        Staff staff = mock(Staff.class);

        // Simulate the expected values
        when(staff.getUsername()).thenReturn("jdoe");
        when(staff.getRole()).thenReturn(Role.Doctor);
        when(staff.getFirstName()).thenReturn("John");
        when(staff.getLastName()).thenReturn("Doe");
        when(staff.getEmail()).thenReturn("jdoe@example.com");
        when(staff.getDateOfBirth()).thenReturn(LocalDate.of(1985, 2, 15));
        when(staff.getPhone()).thenReturn("555-1234");
        when(staff.getAddressLine1()).thenReturn("100 Clinic Rd");
        when(staff.getAddressLine2()).thenReturn("Suite 200");
        when(staff.getCity()).thenReturn("Healthville");
        when(staff.getState()).thenReturn("CA");
        when(staff.getPostal()).thenReturn("90210");
        when(staff.getCountry()).thenReturn("USA");
        when(staff.getSpecialty()).thenReturn("Cardiology");
        when(staff.getYearsExperience()).thenReturn(12);

        // Simulate the repository returning the staff
        when(staffRepository.findByUsername("jdoe")).thenReturn(Optional.of(staff));

        // Call the service method
        StaffSettingFetch result = staffService.getStaffSetting("jdoe");

        // Validate the returned values
        assertEquals("jdoe", result.getUsername());
        assertEquals("Doctor", result.getRole());
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("jdoe@example.com", result.getEmail());
        assertEquals("02/15/1985", result.getDateOfBirth());
        assertEquals("555-1234", result.getPhone());
        assertEquals("100 Clinic Rd", result.getAddressLine1());
        assertEquals("Suite 200", result.getAddressLine2());
        assertEquals("Healthville", result.getCity());
        assertEquals("CA", result.getState());
        assertEquals("90210", result.getPostal());
        assertEquals("USA", result.getCountry());
        assertEquals("Cardiology", result.getSpecialty());
        assertEquals(12, result.getYearsExperience());
    }

    @Test
    void testGetStaffSetting_StaffDoesNotExist_ThrowsException() {
        // Simulate staff not found
        when(staffRepository.findByUsername("unknownUser")).thenReturn(Optional.empty());

        // Expect EntityNotFoundException when user doesn't exist
        assertThrows(EntityNotFoundException.class, () -> staffService.getStaffSetting("unknownUser"));
    }
}