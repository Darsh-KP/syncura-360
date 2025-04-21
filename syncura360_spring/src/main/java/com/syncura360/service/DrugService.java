package com.syncura360.service;

import com.syncura360.dto.Drug.*;
import com.syncura360.model.Drug;
import com.syncura360.model.DrugId;
import com.syncura360.model.enums.DrugCategory;
import com.syncura360.repository.DrugRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Service class responsible for managing drugs in a hospital.
 *
 * @author Darsh-KP
 */
@Service
public class DrugService {
    DrugRepository drugRepository;

    /**
     * Constructor for initializing {@link DrugService} with required dependencies.
     * Uses constructor injection for necessary components.
     *
     * @param drugRepository The repository used for drug operations.
     */
    public DrugService(DrugRepository drugRepository) {
        this.drugRepository = drugRepository;
    }

    /**
     * Creates a new drug in the hospital's inventory if it does not already exist.
     *
     * @param hospitalId The ID of the hospital.
     * @param drugFormDTO The data transfer object containing drug details.
     * @throws EntityExistsException If the drug with the given NDC already exists.
     */
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
                drugFormDTO.getPpq(),
                drugFormDTO.getQuantity(),
                drugFormDTO.getPrice());

        // Save the new drug to database
        drugRepository.save(newDrug);
    }

    /**
     * Updates an existing drug in the hospital's inventory.
     *
     * @param hospitalId The ID of the hospital.
     * @param drugUpdateDTO The data transfer object containing updated drug details.
     * @throws EntityNotFoundException If the drug with the given NDC does not exist.
     * @throws IllegalArgumentException If any input constraints are violated (e.g., negative quantity or invalid price).
     */
    public void updateDrug(int hospitalId, DrugUpdateDTO drugUpdateDTO) {
        // Find the drug if it already exists
        Optional<Drug> drugResult = drugRepository.findById_HospitalIdAndId_Ndc(hospitalId, drugUpdateDTO.getNdc());
        if (drugResult.isEmpty()) {
            // Drug not found
            throw new EntityNotFoundException("Drug with given ndc does not exist.");
        }

        // Check if fields are within constraints
        int quantity = drugUpdateDTO.getQuantity();
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative.");
        }

        BigDecimal price = drugUpdateDTO.getPrice();
        if (price.precision() > 10 || price.scale() > 2) {
            throw new IllegalArgumentException("Price must have a precision of 10 and a scale of 2.");
        }

        // Update the existing drug's info
        Drug drug = drugResult.get();
        drug.setQuantity(quantity);
        drug.setPrice(price);

        // Save the drug to database
        drugRepository.save(drug);
    }

    /**
     * Deletes a drug from the hospital's inventory.
     *
     * @param hospitalId The ID of the hospital.
     * @param drugFormDTO The data transfer object containing the drug details to be deleted.
     * @throws EntityNotFoundException If the drug with the given NDC does not exist.
     */
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

    /**
     * Fetches a list of all drugs for a given hospital.
     *
     * @param hospitalId The ID of the hospital.
     * @return A {@link DrugFetchListDTO} containing a list of all drugs for the hospital.
     */
    public DrugFetchListDTO fetchDrugs(int hospitalId) {
        // Return a list of all the drugs for the hospital
        return new DrugFetchListDTO(drugRepository.findAllById_HospitalId(hospitalId).stream().map(drug ->
                new DrugFetchDTO(drug.getId().getNdc(), drug.getName(), drug.getCategory().getValue(), drug.getDescription(),
                        drug.getStrength(), drug.getPpq(), drug.getQuantity(), drug.getPrice())).toList());
    }
}
