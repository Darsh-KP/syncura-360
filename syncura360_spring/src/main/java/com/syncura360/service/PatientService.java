package com.syncura360.service;

import com.syncura360.dto.Patient.*;
import com.syncura360.model.PatientInfo;
import com.syncura360.model.enums.BloodType;
import com.syncura360.model.enums.Gender;
import com.syncura360.repository.PatientInfoRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service class responsible for managing patient information.
 *
 * @author Darsh-KP
 */
@Service
public class PatientService {
    PatientInfoRepository patientInfoRepository;

    /**
     * Constructor for initializing {@link PatientService} with required dependencies.
     * Uses constructor injection for necessary components.
     *
     * @param patientInfoRepository The repository used for patient information operations.
     */
    public PatientService(PatientInfoRepository patientInfoRepository) {
        this.patientInfoRepository = patientInfoRepository;
    }

    /**
     * Creates a new patient in the system.
     *
     * @param patientFormDTO The data transfer object containing the patient details.
     * @throws EntityExistsException If a patient with the same details already exists.
     */
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

    /**
     * Updates an existing patient's information.
     *
     * @param patientUpdateDTO The data transfer object containing the updated patient details.
     * @throws EntityExistsException If the patient does not exist.
     * @throws IllegalArgumentException If input constraints are violated (e.g., negative height or weight).
     */
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

        String medicalNotes = patientUpdateDTO.getMedicalNotes();
        if (medicalNotes == null || medicalNotes.trim().isEmpty()) medicalNotes = null;
        else medicalNotes = medicalNotes.trim();

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
        patientInfo.setMedicalNotes(medicalNotes);

        // Save the updates to the database
        patientInfoRepository.save(patientInfo);
    }

    /**
     * Fetches a list of all patients in the system.
     *
     * @return A {@link PatientViewFetchContainer} containing a list of all patients.
     */
    public PatientViewFetchContainer fetchPatients() {
        // Return a list of all the patients
        List<PatientViewFetchDTO> patientsList = new ArrayList<>();
        patientInfoRepository.findAll().forEach(patientInfo -> patientsList.add(
                new PatientViewFetchDTO(
                        patientInfo.getId(),
                        patientInfo.getFirstName(),
                        patientInfo.getLastName(),
                        patientInfo.getDateOfBirth().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")),
                        patientInfo.getGender().toString(),
                        patientInfo.getPhone(),
                        patientInfo.getAddressLine1(),
                        patientInfo.getAddressLine2(),
                        patientInfo.getCity(),
                        patientInfo.getState(),
                        patientInfo.getPostal(),
                        patientInfo.getCountry()
                )));
        return new PatientViewFetchContainer(patientsList);
    }

    /**
     * Fetches details of a specific patient by their ID.
     *
     * @param patientId The ID of the patient.
     * @return A {@link SpecificPatientFetchDTO} containing the details of the patient.
     * @throws EntityNotFoundException If the patient with the given ID does not exist.
     */
    public SpecificPatientFetchDTO fetchPatient(Integer patientId) {
        // Find the patient if they already exists
        Optional<PatientInfo> optionalPatientInfo = patientInfoRepository.findById(patientId);
        if (optionalPatientInfo.isEmpty()) {
            // Patient not found
            throw new EntityNotFoundException("Given patient id does not exist.");
        }

        // Extract the patient
        PatientInfo patientInfo = optionalPatientInfo.get();

        return new SpecificPatientFetchDTO(
                patientInfo.getId(),
                patientInfo.getFirstName(),
                patientInfo.getLastName(),
                patientInfo.getDateOfBirth().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")),
                patientInfo.getGender().toString(),
                patientInfo.getBloodType().getValue(),
                patientInfo.getHeight(),
                patientInfo.getWeight(),
                patientInfo.getPhone(),
                patientInfo.getAddressLine1(),
                patientInfo.getAddressLine2(),
                patientInfo.getCity(),
                patientInfo.getState(),
                patientInfo.getPostal(),
                patientInfo.getCountry(),
                patientInfo.getEmergencyContactName(),
                patientInfo.getEmergencyContactPhone(),
                patientInfo.getMedicalNotes()
        );
    }
}