package com.syncura360.service;

import com.syncura360.dto.Patient.PatientFormDTO;
import com.syncura360.dto.Patient.PatientUpdateDTO;
import com.syncura360.dto.Patient.PatientViewFetchContainer;
import com.syncura360.dto.Patient.SpecificPatientFetchDTO;
import com.syncura360.model.PatientInfo;
import com.syncura360.model.enums.BloodType;
import com.syncura360.model.enums.Gender;
import com.syncura360.repository.PatientInfoRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PatientServiceTest {
    // Create a fake repository
    @Mock
    PatientInfoRepository patientInfoRepository;

    // Inject fake patientInfoRepository into patientService
    @InjectMocks
    PatientService patientService;

    @Test
    void testCreatePatient_SuccessfullyCreates() {
        // Create a fake DTO
        PatientFormDTO dto = mock(PatientFormDTO.class);
        when(dto.getFirstName()).thenReturn("John");
        when(dto.getLastName()).thenReturn("Doe");
        when(dto.getDateOfBirth()).thenReturn("01/01/1990");
        when(dto.getGender()).thenReturn("Male");
        when(dto.getBloodType()).thenReturn("A+");
        when(dto.getHeight()).thenReturn(180);
        when(dto.getWeight()).thenReturn(75);
        when(dto.getPhone()).thenReturn("1234567890");
        when(dto.getAddressLine1()).thenReturn("123 Main St");
        when(dto.getAddressLine2()).thenReturn("Apt 4");
        when(dto.getCity()).thenReturn("City");
        when(dto.getState()).thenReturn("State");
        when(dto.getPostal()).thenReturn("12345");
        when(dto.getCountry()).thenReturn("Country");
        when(dto.getEmergencyContactName()).thenReturn("Jane Doe");
        when(dto.getEmergencyContactPhone()).thenReturn("0987654321");

        // Simulate that patient does not already exist
        when(patientInfoRepository.existsByFirstNameAndLastNameAndDateOfBirthAndGenderAndAddressLine1AndPostalAndCountry(
                any(), any(), any(), any(), any(), any(), any())).thenReturn(false);

        // Call the method
        patientService.createPatient(dto);

        // Verify save is called
        verify(patientInfoRepository, times(1)).save(any(PatientInfo.class));
    }

    @Test
    void testCreatePatient_AlreadyExists_ThrowsException() {
        // Create a fake DTO
        PatientFormDTO dto = mock(PatientFormDTO.class);
        when(dto.getFirstName()).thenReturn("John");
        when(dto.getLastName()).thenReturn("Doe");
        when(dto.getDateOfBirth()).thenReturn("01/01/1990");
        when(dto.getGender()).thenReturn("Male");
        when(dto.getBloodType()).thenReturn("A+");
        when(dto.getAddressLine1()).thenReturn("123 Main St");
        when(dto.getPostal()).thenReturn("12345");
        when(dto.getCountry()).thenReturn("Country");

        // Simulate that patient already exists
        when(patientInfoRepository.existsByFirstNameAndLastNameAndDateOfBirthAndGenderAndAddressLine1AndPostalAndCountry(
                any(), any(), any(), any(), any(), any(), any())).thenReturn(true);

        // Expect EntityExistsException
        assertThrows(EntityExistsException.class, () -> patientService.createPatient(dto));
    }

    @Test
    void testUpdatePatient_SuccessfullyUpdates() {
        // Create a fake DTO
        PatientUpdateDTO dto = mock(PatientUpdateDTO.class);
        when(dto.getPatientId()).thenReturn(1);
        when(dto.getFirstName()).thenReturn("John");
        when(dto.getLastName()).thenReturn("Doe");
        when(dto.getDateOfBirth()).thenReturn("01/01/1990");
        when(dto.getGender()).thenReturn("Male");
        when(dto.getBloodType()).thenReturn("O+");
        when(dto.getHeight()).thenReturn(175);
        when(dto.getWeight()).thenReturn(70);
        when(dto.getPhone()).thenReturn("1234567890");
        when(dto.getAddressLine1()).thenReturn("456 Main St");
        when(dto.getAddressLine2()).thenReturn("Unit 1");
        when(dto.getCity()).thenReturn("New City");
        when(dto.getState()).thenReturn("New State");
        when(dto.getPostal()).thenReturn("54321");
        when(dto.getCountry()).thenReturn("New Country");
        when(dto.getEmergencyContactName()).thenReturn("Jane Smith");
        when(dto.getEmergencyContactPhone()).thenReturn("9876543210");
        when(dto.getMedicalNotes()).thenReturn("None");

        // Simulate patient found in DB
        PatientInfo existingPatient = new PatientInfo();
        when(patientInfoRepository.findById(1)).thenReturn(Optional.of(existingPatient));

        // Call method
        patientService.updatePatient(dto);

        // Verify save is called
        verify(patientInfoRepository).save(existingPatient);
    }

    @Test
    void testUpdatePatient_NotFound_ThrowsException() {
        // Create a fake DTO
        PatientUpdateDTO dto = mock(PatientUpdateDTO.class);
        when(dto.getPatientId()).thenReturn(1);

        // Simulate patient not found
        when(patientInfoRepository.findById(1)).thenReturn(Optional.empty());

        // Expect EntityExistsException
        assertThrows(EntityExistsException.class, () -> patientService.updatePatient(dto));
    }

    @Test
    void testUpdatePatient_InvalidHeight_ThrowsException() {
        // Create a fake DTO
        PatientUpdateDTO dto = mock(PatientUpdateDTO.class);
        when(dto.getPatientId()).thenReturn(1);
        when(dto.getHeight()).thenReturn(-10);

        // Simulate patient found
        when(patientInfoRepository.findById(1)).thenReturn(Optional.of(new PatientInfo()));

        // Expect IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> patientService.updatePatient(dto));
    }

    @Test
    void testUpdatePatient_InvalidWeight_ThrowsException() {
        // Create a fake DTO
        PatientUpdateDTO dto = mock(PatientUpdateDTO.class);
        when(dto.getPatientId()).thenReturn(1);
        when(dto.getWeight()).thenReturn(-5);

        // Simulate patient found
        when(patientInfoRepository.findById(1)).thenReturn(Optional.of(new PatientInfo()));

        // Expect IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> patientService.updatePatient(dto));
    }

    @Test
    void testFetchPatients_ReturnsAll() {
        // Simulate patients
        PatientInfo patient1 = mock(PatientInfo.class);
        when(patient1.getId()).thenReturn(1);
        when(patient1.getFirstName()).thenReturn("John");
        when(patient1.getLastName()).thenReturn("Doe");
        when(patient1.getDateOfBirth()).thenReturn(LocalDate.of(1990, 1, 1));
        when(patient1.getGender()).thenReturn(Gender.Male);
        when(patient1.getPhone()).thenReturn("1234567890");
        when(patient1.getAddressLine1()).thenReturn("123 Main St");
        when(patient1.getAddressLine2()).thenReturn("Apt 4");
        when(patient1.getCity()).thenReturn("City");
        when(patient1.getState()).thenReturn("State");
        when(patient1.getPostal()).thenReturn("12345");
        when(patient1.getCountry()).thenReturn("Country");

        when(patientInfoRepository.findAll()).thenReturn(List.of(patient1));

        // Call method
        PatientViewFetchContainer result = patientService.fetchPatients();

        // Validate result
        assertEquals(1, result.getPatients().size());
        assertEquals("John", result.getPatients().getFirst().getFirstName());
    }

    @Test
    void testFetchPatient_Exists_ReturnsDetails() {
        // Create a fake patient
        PatientInfo patient = mock(PatientInfo.class);
        when(patient.getId()).thenReturn(1);
        when(patient.getFirstName()).thenReturn("John");
        when(patient.getLastName()).thenReturn("Doe");
        when(patient.getDateOfBirth()).thenReturn(LocalDate.of(1990, 1, 1));
        when(patient.getGender()).thenReturn(Gender.Male);
        when(patient.getBloodType()).thenReturn(BloodType.O_Positive);
        when(patient.getHeight()).thenReturn(180);
        when(patient.getWeight()).thenReturn(75);
        when(patient.getPhone()).thenReturn("1234567890");
        when(patient.getAddressLine1()).thenReturn("123 Main St");
        when(patient.getAddressLine2()).thenReturn("Apt 1");
        when(patient.getCity()).thenReturn("City");
        when(patient.getState()).thenReturn("State");
        when(patient.getPostal()).thenReturn("12345");
        when(patient.getCountry()).thenReturn("Country");
        when(patient.getEmergencyContactName()).thenReturn("Jane Doe");
        when(patient.getEmergencyContactPhone()).thenReturn("9876543210");
        when(patient.getMedicalNotes()).thenReturn("N/A");

        // Simulate found patient
        when(patientInfoRepository.findById(1)).thenReturn(Optional.of(patient));

        // Call method
        SpecificPatientFetchDTO result = patientService.fetchPatient(1);

        // Validate values
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("01/01/1990", result.getDateOfBirth());
    }

    @Test
    void testFetchPatient_NotFound_ThrowsException() {
        // Simulate no patient found
        when(patientInfoRepository.findById(1)).thenReturn(Optional.empty());

        // Expect EntityNotFoundException
        assertThrows(EntityNotFoundException.class, () -> patientService.fetchPatient(1));
    }
}