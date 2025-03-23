package com.syncura360.controller;

import com.syncura360.model.Hospital;
import com.syncura360.model.Staff;
import com.syncura360.model.enums.Role;
import com.syncura360.model.enums.TraumaLevel;
import com.syncura360.repository.HospitalRepository;
import com.syncura360.dto.RegistrationInfo;
import com.syncura360.repository.StaffRepository;
import com.syncura360.security.passwordSecurity;
import com.syncura360.dto.HospitalCreationDto;
import com.syncura360.dto.StaffCreationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

/**
 * Handles hospital registration and initialization of head admin credentials.
 * @author Benjamin Leiby
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/register")
public class RegController {

    @Autowired
    HospitalRepository hospitalRepository;
    @Autowired
    StaffRepository staffRepository;

    /**
     * @param regInfo DTO containing hospital and head admin info.
     * @return String containing result message.
     */
    @PostMapping("/hospital")
    public ResponseEntity<String> registerHospital(@Valid @RequestBody RegistrationInfo regInfo) {

        HttpStatus responseType = HttpStatus.NOT_ACCEPTABLE;
        String responseMessage = "Failed.";

        HospitalCreationDto hospitalCreationDto = regInfo.getHospital();
        StaffCreationDto headAdminCreationDto = regInfo.getStaff();

        System.out.println(hospitalCreationDto.toString());


        // Check for unique values. Not sure exactly which values we should be checking for, but we can decide soon.
        if (!hospitalRepository.addressLine1(hospitalCreationDto.getAddressLine1()).isEmpty()) { responseMessage = "Failed: Hospital address is taken.";}
        else if (!hospitalRepository.findByTelephone(hospitalCreationDto.getTelephone()).isEmpty()) { responseMessage = "Failed: Hospital phone is taken."; }
        else if (staffRepository.findByUsername(headAdminCreationDto.getUsername()).isPresent()) { responseMessage = "Failed: Staff username is taken."; }
        else {

            Hospital hospital = null;
            Staff staff = null;

            try {
                hospital = getHospital(hospitalCreationDto);
                hospitalRepository.save(hospital);
            } catch (Exception e) {

                System.out.println(e.toString());

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
     * @param headAdminCreationDto DTO to model head admin registration info.
     * @param hospital JPA Entity to model the hospital.
     * @return Staff JPA Entity for insertion.
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
     * @param hospitalCreationDto DTO to model hospital registration info.
     * @return Hospital JPA entity for insertion.
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