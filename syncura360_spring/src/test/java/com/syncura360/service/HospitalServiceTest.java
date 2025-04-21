package com.syncura360.service;

import com.syncura360.dto.Hospital.HospitalSettingFetch;
import com.syncura360.model.Hospital;
import com.syncura360.model.enums.TraumaLevel;
import com.syncura360.repository.HospitalRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HospitalServiceTest {
    // Create a fake repository
    @Mock
    HospitalRepository hospitalRepository;

    // Inject fake hospitalRepository into hospitalService
    @InjectMocks
    HospitalService hospitalService;

    @Test
    void testGetHospitalSetting_HospitalExists_ReturnsSetting() {
        // Create a fake hospital entity
        Hospital hospital = mock(Hospital.class);
        when(hospital.getName()).thenReturn("Test Hospital");
        when(hospital.getAddressLine1()).thenReturn("123 Main St");
        when(hospital.getAddressLine2()).thenReturn("Suite 100");
        when(hospital.getCity()).thenReturn("Testville");
        when(hospital.getState()).thenReturn("TS");
        when(hospital.getPostal()).thenReturn("12345");
        when(hospital.getTelephone()).thenReturn("123-456-7890");
        when(hospital.getType()).thenReturn("General");

        TraumaLevel traumaLevel = mock(TraumaLevel.class);
        when(traumaLevel.getValue()).thenReturn("Level I");
        when(hospital.getTraumaLevel()).thenReturn(traumaLevel);
        when(hospital.getHasHelipad()).thenReturn(true);

        // Simulate hospital found in the repository
        when(hospitalRepository.findById(1)).thenReturn(Optional.of(hospital));

        // Call the method
        HospitalSettingFetch setting = hospitalService.getHospitalSetting(1);

        // Validate returned setting values
        assertEquals("Test Hospital", setting.getHospitalName());
        assertEquals("123 Main St", setting.getAddressLine1());
        assertEquals("Suite 100", setting.getAddressLine2());
        assertEquals("Testville", setting.getCity());
        assertEquals("TS", setting.getState());
        assertEquals("12345", setting.getPostal());
        assertEquals("123-456-7890", setting.getTelephone());
        assertEquals("General", setting.getType());
        assertEquals("Level I", setting.getTraumaLevel());
        assertTrue(setting.getHasHelipad());
    }

    @Test
    void testGetHospitalSetting_HospitalDoesNotExist_ThrowsException() {
        // Simulate empty result from repository
        when(hospitalRepository.findById(1)).thenReturn(Optional.empty());

        // Validate exception is thrown when hospital not found
        assertThrows(EntityNotFoundException.class, () -> hospitalService.getHospitalSetting(1));
    }
}