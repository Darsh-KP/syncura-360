package com.syncura360.service;

import com.syncura360.dto.Hospital.HospitalSettingFetch;
import com.syncura360.model.Hospital;
import com.syncura360.repository.HospitalRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class HospitalService {
    HospitalRepository hospitalRepository;

    public HospitalService(HospitalRepository hospitalRepository) {
        this.hospitalRepository = hospitalRepository;
    }

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
