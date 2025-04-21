package com.syncura360.controller;

import com.syncura360.model.Hospital;
import com.syncura360.model.Staff;
import com.syncura360.model.enums.Role;
import com.syncura360.model.enums.TraumaLevel;
import com.syncura360.repository.HospitalRepository;
import com.syncura360.dto.Authentication.RegistrationInfo;
import com.syncura360.repository.StaffRepository;
import com.syncura360.security.passwordSecurity;
import com.syncura360.dto.Hospital.HospitalCreationDto;
import com.syncura360.dto.Staff.StaffCreationDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

/**
 * Handles hospital registration and initialization of head admin credentials.
 * This controller is responsible for registering a new hospital along with its head administrator.
 *
 * @author Benjamin Leiby
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/register")
public class RegController {
    HospitalRepository hospitalRepository;
    StaffRepository staffRepository;

    /**
     * Constructor to inject dependencies for hospital and staff repositories.
     *
     * @param hospitalRepository the repository for accessing hospital data.
     * @param staffRepository the repository for accessing staff data.
     */
    public RegController(HospitalRepository hospitalRepository, StaffRepository staffRepository) {
        this.hospitalRepository = hospitalRepository;
        this.staffRepository = staffRepository;
    }

    /**
     * Registers a new hospital and its head admin.
     * This method checks for unique values (such as hospital address and phone number,
     * and staff username) before saving the hospital and the head admin to the database.
     * If any error occurs during the process, it handles rollback and returns appropriate error messages.
     *
     * @param regInfo DTO containing hospital and head admin information ({@link RegistrationInfo}).
     * @return a {@link ResponseEntity} with a status and message indicating the result of the registration.
     */
    @PostMapping("/hospital")
    public ResponseEntity<String> registerHospital(@Valid @RequestBody RegistrationInfo regInfo) {

        HttpStatus responseType = HttpStatus.NOT_ACCEPTABLE;
        String responseMessage;

        HospitalCreationDto hospitalCreationDto = regInfo.getHospital();
        StaffCreationDto headAdminCreationDto = regInfo.getStaff();

        System.out.println(hospitalCreationDto.toString());


        // Check for unique values. Not sure exactly which values we should be checking for, but we can decide soon.
        if (!hospitalRepository.addressLine1(hospitalCreationDto.getAddressLine1()).isEmpty()) { responseMessage = "Failed: Hospital address is taken.";}
        else if (!hospitalRepository.findByTelephone(hospitalCreationDto.getTelephone()).isEmpty()) { responseMessage = "Failed: Hospital phone is taken."; }
        else if (staffRepository.findByUsername(headAdminCreationDto.getUsername()).isPresent()) { responseMessage = "Failed: Staff username is taken."; }
        else {

            Hospital hospital;
            Staff staff;

            try {
                hospital = getHospital(hospitalCreationDto);
                hospitalRepository.save(hospital);
            } catch (Exception e) {

                System.out.println(e.getMessage());

                responseType = HttpStatus.INTERNAL_SERVER_ERROR;
                responseMessage = "Failed. Error saving hospital to database.";
                return ResponseEntity.status(responseType).body(responseMessage);
            }

            try {
                staff = getStaff(headAdminCreationDto, hospital);
                staffRepository.save(staff);
            } catch (Exception e) {
                hospitalRepository.delete(hospital); // VERY IMPORTANT. Roll back hospital creation if user creation fails.
                responseType = HttpStatus.INTERNAL_SERVER_ERROR;
                responseMessage = "Failed. Error saving user credentials to database.";
                return ResponseEntity.status(responseType).body(responseMessage);
            }

            responseType = HttpStatus.OK;
            responseMessage = "Registration successful.";

        }

        return ResponseEntity.status(responseType).body(responseMessage);

      }

    /**
     * Converts {@link StaffCreationDto} into a {@link Staff} JPA entity for insertion into the database.
     * The staff is set with various personal details and hospital association.
     *
     * @param headAdminCreationDto DTO containing head admin registration information.
     * @param hospital the hospital entity to associate the staff with.
     * @return a {@link Staff} entity populated with the provided details.
     */
    private static Staff getStaff(StaffCreationDto headAdminCreationDto, Hospital hospital) {
        PasswordEncoder encoder = passwordSecurity.getPasswordEncoder();
        Staff staff = new Staff();
        staff.setUsername(headAdminCreationDto.getUsername());
        staff.setPasswordHash(encoder.encode(headAdminCreationDto.getPasswordHash()));
        staff.setRole(Role.fromValue(headAdminCreationDto.getRole()));
        staff.setFirstName(headAdminCreationDto.getFirstName());
        staff.setLastName(headAdminCreationDto.getLastName());
        staff.setEmail(headAdminCreationDto.getEmail());
        staff.setPhone(headAdminCreationDto.getPhone());
        staff.setAddressLine1(headAdminCreationDto.getAddressLine1());
        staff.setAddressLine2(headAdminCreationDto.getAddressLine2());
        staff.setCity(headAdminCreationDto.getCity());
        staff.setState(headAdminCreationDto.getState());
        staff.setPostal(headAdminCreationDto.getPostal());
        staff.setCountry(headAdminCreationDto.getCountry());
        staff.setDateOfBirth(headAdminCreationDto.getDateOfBirth());
        staff.setWorksAt(hospital);
        return staff;
    }

    /**
     * Converts {@link HospitalCreationDto} into a {@link Hospital} JPA entity for insertion into the database.
     *
     * @param hospitalCreationDto DTO containing hospital registration information.
     * @return a {@link Hospital} entity populated with the provided details.
     */
    private static Hospital getHospital(HospitalCreationDto hospitalCreationDto) {
        Hospital hospital = new Hospital();
        hospital.setName(hospitalCreationDto.getName());
        hospital.setAddressLine1(hospitalCreationDto.getAddressLine1());
        hospital.setAddressLine2(hospitalCreationDto.getAddressLine2());
        hospital.setCity(hospitalCreationDto.getCity());
        hospital.setState(hospitalCreationDto.getState());
        hospital.setPostal(hospitalCreationDto.getPostal());
        hospital.setTelephone(hospitalCreationDto.getTelephone());
        hospital.setType(hospitalCreationDto.getType());
        hospital.setTraumaLevel(TraumaLevel.fromValue(hospitalCreationDto.getTraumaLevel()));
        hospital.setHasHelipad(hospital.getHasHelipad());
        return hospital;
    }
}