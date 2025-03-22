package com.syncura360.service;

import com.syncura360.dto.Drug.DrugCreationDTO;
import com.syncura360.model.Drug;
import com.syncura360.model.DrugId;
import com.syncura360.model.enums.DrugCategory;
import com.syncura360.repository.DrugRepository;
import org.springframework.stereotype.Service;

@Service
public class DrugService {
    DrugRepository drugRepository;

    // Constructor injection
    DrugService(DrugRepository drugRepository) {
        this.drugRepository = drugRepository;
    }

    public void createDrug(int hospitalId, DrugCreationDTO drugCreationDTO) {
        // Check for drug uniqueness
        if(drugRepository.existsById_HospitalIdAndId_Ndc(hospitalId, drugCreationDTO.getNdc())) {
            // Drug already exists
            throw new IllegalStateException("Drug with given ndc already exists.");
        }

        // Create new drug and populate fields
        Drug newDrug = new Drug(
                new DrugId(hospitalId, drugCreationDTO.getNdc()),
                drugCreationDTO.getName(),
                DrugCategory.fromValue(drugCreationDTO.getCategory()),
                drugCreationDTO.getDescription(),
                drugCreationDTO.getStrength(),
                drugCreationDTO.getQuantity(),
                drugCreationDTO.getPrice());

        // Save the new drug to database
        drugRepository.save(newDrug);
    }
}
