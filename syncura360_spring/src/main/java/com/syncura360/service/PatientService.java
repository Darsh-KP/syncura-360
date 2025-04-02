package com.syncura360.service;

import com.syncura360.dto.Patient.PatientFormDTO;
import com.syncura360.dto.Patient.PatientUpdateDTO;
import com.syncura360.model.PatientInfo;
import com.syncura360.model.enums.BloodType;
import com.syncura360.model.enums.Gender;
import com.syncura360.repository.PatientInfoRepository;
import jakarta.persistence.EntityExistsException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

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
                patientFormDTO.getFirstName(),
                patientFormDTO.getLastName(),
                dateOfBirth,
                gender,
                patientFormDTO.getAddressLine1(),
                patientFormDTO.getPostal(),
                patientFormDTO.getCountry())) {
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
                patientFormDTO.getFirstName(),
                patientFormDTO.getLastName(),
                dateOfBirth,
                gender,
                bloodType,
                patientFormDTO.getHeight(),
                patientFormDTO.getWeight(),
                patientFormDTO.getPhone(),
                patientFormDTO.getAddressLine1(),
                addressLine2,
                patientFormDTO.getCity(),
                patientFormDTO.getState(),
                patientFormDTO.getPostal(),
                patientFormDTO.getCountry(),
                emergencyContactName,
                emergencyContactPhone
        );

        // Save new patient to database
        patientInfoRepository.save(newPatientInfo);
    }

    public void updatePatient(PatientUpdateDTO patientUpdateDTO) {
        // Convert fields
        LocalDate dateOfBirth = null;
        if (patientUpdateDTO.getDateOfBirth() != null && !patientUpdateDTO.getDateOfBirth().trim().isEmpty()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

            dateOfBirth = LocalDate.parse(patientUpdateDTO.getDateOfBirth().trim(), formatter);
        }

        Gender gender = null;
        if (patientUpdateDTO.getGender() != null && !patientUpdateDTO.getGender().trim().isEmpty()) {
            gender = Gender.valueOf(patientUpdateDTO.getGender().trim());
        }

        BloodType bloodType = null;
        if (patientUpdateDTO.getBloodType() != null && !patientUpdateDTO.getBloodType().trim().isEmpty()) {
            bloodType = BloodType.fromValue(patientUpdateDTO.getBloodType().trim());
        }

        // Find the patient if they already exists
        Optional<PatientInfo> optionalPatientInfo = patientInfoRepository.findById(patientUpdateDTO.getId());
        if (optionalPatientInfo.isEmpty()) {
            throw new EntityExistsException("Given patient does not exist.");
        }

        // Check if fields are within constraints
        if (patientUpdateDTO.getHeight() != null && patientUpdateDTO.getHeight() < 0) {
            throw new IllegalArgumentException("Height must be a positive integer.");
        }

        if (patientUpdateDTO.getWeight() != null && patientUpdateDTO.getWeight() < 0) {
            throw new IllegalArgumentException("Weight must be a positive integer.");
        }

        // Handle missing fields
        String addressLine2 = patientUpdateDTO.getAddressLine2();
        if (addressLine2 == null || addressLine2.trim().isEmpty()) addressLine2 = null;
        else addressLine2 = addressLine2.trim();

        String emergencyContactName = patientUpdateDTO.getEmergencyContactName();
        if (emergencyContactName == null || emergencyContactName.trim().isEmpty()) emergencyContactName = null;
        else emergencyContactName = emergencyContactName.trim();

        String emergencyContactPhone = patientUpdateDTO.getEmergencyContactPhone();
        if (emergencyContactPhone == null || emergencyContactPhone.trim().isEmpty()) emergencyContactPhone = null;
        else emergencyContactPhone = emergencyContactPhone.trim();

        // Update patient info
        PatientInfo patientInfo = optionalPatientInfo.get();
        patientInfo.setFirstName(patientUpdateDTO.getFirstName().trim());
        patientInfo.setLastName(patientUpdateDTO.getLastName().trim());
        patientInfo.setDateOfBirth(dateOfBirth);
        patientInfo.setGender(gender);
        patientInfo.setBloodType(bloodType);
        patientInfo.setHeight(patientUpdateDTO.getHeight());
        patientInfo.setWeight(patientUpdateDTO.getWeight());
        patientInfo.setPhone(patientUpdateDTO.getPhone().trim());
        patientInfo.setAddressLine1(patientUpdateDTO.getAddressLine1().trim());
        patientInfo.setAddressLine2(addressLine2);
        patientInfo.setCity(patientUpdateDTO.getCity().trim());
        patientInfo.setState(patientUpdateDTO.getState().trim());
        patientInfo.setPostal(patientUpdateDTO.getPostal().trim());
        patientInfo.setCountry(patientUpdateDTO.getCountry().trim());
        patientInfo.setEmergencyContactName(emergencyContactName);
        patientInfo.setEmergencyContactPhone(emergencyContactPhone);

        // Save the updates to the database
        patientInfoRepository.save(patientInfo);
    }
}