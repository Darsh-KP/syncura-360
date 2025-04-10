package com.syncura360.repository;

import com.syncura360.model.PatientInfo;
import com.syncura360.model.enums.Gender;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;

/**
 * Repository interface for performing CRUD operations on the PatientInfo entity.
 *
 * @author Darsh-KP
 */
public interface PatientInfoRepository extends CrudRepository<PatientInfo, Integer> {
    Boolean existsByFirstNameAndLastNameAndDateOfBirthAndGenderAndAddressLine1AndPostalAndCountry(
            String firstName, String lastName, LocalDate dateOfBirth, Gender gender, String addressLine1, String postal, String country);
}