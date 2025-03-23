package com.syncura360.service;

import com.syncura360.dto.Patient.PatientFormDTO;
import com.syncura360.model.PatientInfo;
import com.syncura360.model.enums.BloodType;
import com.syncura360.model.enums.Gender;
import com.syncura360.repository.PatientInfoRepository;
import jakarta.persistence.EntityExistsException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class PatientService {
    PatientInfoRepository patientInfoRepository;

    // Constructor injection
    public PatientService(PatientInfoRepository patientInfoRepository) {
        this.patientInfoRepository = patientInfoRepository;
    }

    public void createPatient(PatientFormDTO patientFormDTO) {
        // Convert fields
        LocalDate dateOfBirth = null;
        if (patientFormDTO.getDateOfBirth() != null && !patientFormDTO.getDateOfBirth().trim().isEmpty()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

            dateOfBirth = LocalDate.parse(patientFormDTO.getDateOfBirth().trim(), formatter);
        }

        Gender gender = null;
        if (patientFormDTO.getGender() != null && !patientFormDTO.getGender().trim().isEmpty()) {
            gender = Gender.valueOf(patientFormDTO.getGender().trim());
        }

        BloodType bloodType = null;
        if (patientFormDTO.getBloodType() != null && !patientFormDTO.getBloodType().trim().isEmpty()) {
            bloodType = BloodType.fromValue(patientFormDTO.getBloodType().trim());
        }

        // Check for patient uniqueness
        if (patientInfoRepository.existsByFirstNameAndLastNameAndDateOfBirthAndGenderAndAddressLine1AndPostalAndCountry(
                patientFormDTO.getFirstName().trim(),
                patientFormDTO.getLastName().trim(),
                dateOfBirth,
                gender,
                patientFormDTO.getAddressLine1().trim(),
                patientFormDTO.getPostal().trim(),
                patientFormDTO.getCountry().trim())) {
            throw new EntityExistsException("Patient already exists.");
        }

        // Handle missing fields
        String addressLine2 = patientFormDTO.getAddressLine2();
        if (addressLine2 == null || addressLine2.trim().isEmpty()) addressLine2 = null;

        String emergencyContactName = patientFormDTO.getEmergencyContactName();
        if (emergencyContactName == null || emergencyContactName.trim().isEmpty()) emergencyContactName = null;

        String emergencyContactPhone = patientFormDTO.getEmergencyContactPhone();
        if (emergencyContactPhone == null || emergencyContactPhone.trim().isEmpty()) emergencyContactPhone = null;

        // Create new patient
        PatientInfo newPatientInfo = new PatientInfo(
                patientFormDTO.getFirstName().trim(),
                patientFormDTO.getLastName().trim(),
                dateOfBirth,
                gender,
                bloodType,
                patientFormDTO.getHeight(),
                patientFormDTO.getWeight(),
                patientFormDTO.getPhone().trim(),
                patientFormDTO.getAddressLine1().trim(),
                addressLine2,
                patientFormDTO.getCity().trim(),
                patientFormDTO.getState().trim(),
                patientFormDTO.getPostal().trim(),
                patientFormDTO.getCountry().trim(),
                emergencyContactName,
                emergencyContactPhone
        );

        // Save new patient to database
        patientInfoRepository.save(newPatientInfo);
    }
}