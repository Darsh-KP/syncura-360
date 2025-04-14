package com.syncura360.service;

import com.syncura360.dto.Hospital.HospitalSettingFetch;
import com.syncura360.model.Hospital;
import com.syncura360.repository.HospitalRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service for handling operations related to hospital settings and data retrieval.
 *
 * @author Darsh-KP
 */
@Service
public class HospitalService {
    HospitalRepository hospitalRepository;

    /**
     * Constructs a HospitalService with the given repository dependency.
     *
     * @param hospitalRepository Repository for accessing hospital data.
     */
    public HospitalService(HospitalRepository hospitalRepository) {
        this.hospitalRepository = hospitalRepository;
    }

    /**
     * Retrieves hospital settings for a given hospital ID.
     *
     * @param hospitalId The unique identifier of the hospital.
     * @return A {@link HospitalSettingFetch} DTO containing hospital setting details.
     * @throws EntityNotFoundException If the hospital with the given ID does not exist.
     */
    public HospitalSettingFetch getHospitalSetting(int hospitalId) {
        // Find the hospital if it already exists
        Optional<Hospital> optionalHospital = hospitalRepository.findById(hospitalId);
        if (optionalHospital.isEmpty()) {
            // Hospital not found
            throw new EntityNotFoundException("Hospital with given id does not exist.");
        }

        // Get the hospital
        Hospital hospital = optionalHospital.get();

        // Return hospital setting details
        return new HospitalSettingFetch(
                hospital.getName(),
                hospital.getAddressLine1(),
                hospital.getAddressLine2(),
                hospital.getCity(),
                hospital.getState(),
                hospital.getPostal(),
                hospital.getTelephone(),
                hospital.getType(),
                hospital.getTraumaLevel().getValue(),
                hospital.getHasHelipad()
        );
    }
}
