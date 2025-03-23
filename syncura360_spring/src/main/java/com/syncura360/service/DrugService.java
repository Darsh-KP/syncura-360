package com.syncura360.service;

import com.syncura360.dto.Drug.DrugDeletionDTO;
import com.syncura360.dto.Drug.DrugFetchDTO;
import com.syncura360.dto.Drug.DrugFetchListDTO;
import com.syncura360.dto.Drug.DrugFormDTO;
import com.syncura360.model.Drug;
import com.syncura360.model.DrugId;
import com.syncura360.model.enums.DrugCategory;
import com.syncura360.repository.DrugRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class DrugService {
    DrugRepository drugRepository;

    // Constructor injection
    DrugService(DrugRepository drugRepository) {
        this.drugRepository = drugRepository;
    }

    public void createDrug(int hospitalId, DrugFormDTO drugFormDTO) {
        // Check for drug uniqueness
        if (drugRepository.existsById_HospitalIdAndId_Ndc(hospitalId, drugFormDTO.getNdc())) {
            // Drug already exists
            throw new EntityExistsException("Drug with given ndc already exists.");
        }

        // Handle missing fields
        String category = drugFormDTO.getCategory();
        DrugCategory drugCategory = null;
        if (category != null && !category.trim().isEmpty()) drugCategory = DrugCategory.fromValue(category);

        String description = drugFormDTO.getDescription();
        if (description == null || description.trim().isEmpty()) description = null;

        // Create new drug and populate fields
        Drug newDrug = new Drug(
                new DrugId(hospitalId, drugFormDTO.getNdc()),
                drugFormDTO.getName(),
                drugCategory,
                description,
                drugFormDTO.getStrength(),
                drugFormDTO.getQuantity(),
                drugFormDTO.getPrice());

        // Save the new drug to database
        drugRepository.save(newDrug);
    }

    public void updateDrug(int hospitalId, DrugFormDTO drugFormDTO) {
        // Find the drug if it already exists
        Optional<Drug> drugResult = drugRepository.findById_HospitalIdAndId_Ndc(hospitalId, drugFormDTO.getNdc());
        if (drugResult.isEmpty()) {
            // Drug not found
            throw new EntityNotFoundException("Drug with given ndc does not exist.");
        }

        // Check if fields are within constraints
        int quantity = drugFormDTO.getQuantity();
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative.");
        }

        BigDecimal price = drugFormDTO.getPrice();
        if (price.precision() > 10 || price.scale() > 2) {
            throw new IllegalArgumentException("Price must have a precision of 10 and a scale of 2.");
        }

        // Update the existing drug's info
        Drug drug = drugResult.get();
        drug.setName(drugFormDTO.getName());
        if (drugFormDTO.getCategory() != null) drug.setCategory(DrugCategory.fromValue(drugFormDTO.getCategory()));
        if (drugFormDTO.getDescription() != null) drug.setDescription(drugFormDTO.getDescription());
        drug.setStrength(drugFormDTO.getStrength());
        if (drugFormDTO.getQuantity() != null) drug.setQuantity(quantity);
        drug.setPrice(price);

        // Save the drug to database
        drugRepository.save(drug);
    }

    public void deleteDrug(int hospitalId, DrugDeletionDTO drugFormDTO) {
        // Find the drug if it already exists
        Optional<Drug> drugResult = drugRepository.findById_HospitalIdAndId_Ndc(hospitalId, drugFormDTO.getNdc());
        if (drugResult.isEmpty()) {
            // Drug not found
            throw new EntityNotFoundException("Drug with given ndc does not exist.");
        }

        // Delete the drug
        Drug drug = drugResult.get();
        drugRepository.deleteById(new DrugId(hospitalId, drug.getId().getNdc()));
    }

    public DrugFetchListDTO fetchDrugs(int hospitalId) {
        // Return a list of all the drugs for the hospital
        return new DrugFetchListDTO(drugRepository.findAllById_HospitalId(hospitalId).stream().map(drug ->
                new DrugFetchDTO(drug.getId().getNdc(), drug.getName(), drug.getCategory().getValue(), drug.getDescription(),
                        drug.getStrength(), drug.getQuantity(), drug.getPrice())).toList());
    }
}
