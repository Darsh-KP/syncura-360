package com.syncura360.controller;

import com.syncura360.model.Hospital;
import com.syncura360.model.Staff;
import com.syncura360.model.enums.Role;
import com.syncura360.security.passwordSecurity;
import com.syncura360.dto.Staff.StaffCreationRequest;
import com.syncura360.dto.Staff.StaffCreationResponse;
import com.syncura360.repository.StaffRepository;
import com.syncura360.dto.Staff.StaffUpdateRequest;
import com.syncura360.security.JwtUtil;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Controller responsible for handling CRUD operations on staff credentials.
 * Provides endpoints for updating, creating, retrieving, and managing staff members.
 * <p>
 * This controller ensures that operations are performed securely, and that
 * only authenticated users with proper authorization can modify staff details.
 *
 * @author Benjamin Leiby
 */
@RestController
@CrossOrigin(origins="*")
@RequestMapping("/staff")
public class StaffController {

    @Autowired
    StaffRepository staffRepository;
    @Autowired
    JwtUtil jwtUtil;

    /**
     * Attempts to apply a list of staff information updates.
     * <p>
     * This method allows an authorized user to batch update multiple staff records.
     * If an update fails (either due to a staff member not existing or not being authorized),
     * it will be tracked and communicated in the response.
     *
     * @param authorization the JWT token of the authorized user.
     * @param staffUpdateRequest the DTO containing a collection of attempted staff updates.
     * @return a {@link ResponseEntity} containing a {@link StaffUpdateResponse} with the result message and list of updated staff.
     */
    @PutMapping("/batch")
    public ResponseEntity<StaffUpdateResponse> updateStaff(
            @RequestHeader(name="Authorization") String authorization,
            @RequestBody StaffUpdateRequest staffUpdateRequest)
    {

        StaffUpdateResponse response = new StaffUpdateResponse();
        List<String> failed = new ArrayList<>(); // Track to determine partial success.

        String authenticatedUsername = jwtUtil.getUsername(authorization);
        Optional<Staff> authenticatedStaff = staffRepository.findByUsername(authenticatedUsername);
        if (authenticatedStaff.isEmpty()) {
            response.setMessage("Failed: Accessing user not found.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

        Hospital hospital = authenticatedStaff.get().getWorksAt();

        System.out.println(staffUpdateRequest.getUpdates().toString());


        for (StaffUpdateRequest.StaffUpdateDto updateDto : staffUpdateRequest.getUpdates()) {

            Optional<Staff> optionalStaff = staffRepository.findByUsername(updateDto.getUsername());

            // Failed if staff member doesn't exist or staff doesn't work at same hospital as accessing user.
            if (optionalStaff.isEmpty() || optionalStaff.get().getWorksAt() != hospital) {
                failed.add(updateDto.getUsername());
            }
            else {
                Staff staff = optionalStaff.get();
                try {
                    applyUpdates(staff, updateDto.getFields());
                    staffRepository.save(staff);
                    response.getStaffUsernames().add(updateDto.getUsername());
                    // Failed if saving changes causes any error.
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    failed.add(updateDto.getUsername()); }
            }

        }

        if (failed.isEmpty()) {
            response.setMessage("Success: All updates applied.");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
        else if (response.getStaffUsernames().isEmpty()) {
            response.setMessage("Failed: All updates failed.");
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body(response);
        }
        else { // Else then only partial records were updated.
            response.setMessage("Partial success: Some updates applied.");
            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).body(response);
        }

    }

    /**
     * Applies updates to a staff member based on a field-value mapping.
     * <p>
     * This method updates a staff entity with the provided field names and values.
     * It supports common fields such as `username`, `role`, `firstName`, etc.
     *
     * @param staff the staff entity to update.
     * @param fields a map of field names and values to be updated.
     */
    private void applyUpdates(Staff staff, Map<String, Object> fields) {

        for (Map.Entry<String, Object> entry : fields.entrySet()) {

            String fieldName = entry.getKey();
            Object fieldValue = entry.getValue();

            switch (fieldName) {

                case "username": staff.setUsername((String) fieldValue);
                    break;
                case "role": staff.setRole(Role.fromValue((String) fieldValue));
                    break;
                case "firstName": staff.setFirstName((String) fieldValue);
                    break;
                case "lastName": staff.setLastName((String) fieldValue);
                    break;
                case "email": staff.setEmail((String) fieldValue);
                    break;
                case "phone": staff.setPhone((String) fieldValue);
                    break;
                case "addressLine1": staff.setAddressLine1((String) fieldValue);
                    break;
                case "addressLine2": staff.setAddressLine2((String) fieldValue);
                    break;
                case "city": staff.setCity((String) fieldValue);
                    break;
                case "state": staff.setState((String) fieldValue);
                    break;
                case "postal": staff.setPostal((String) fieldValue);
                    break;
                case "country": staff.setCountry((String) fieldValue);
                    break;
                case "specialty": staff.setSpecialty((String) fieldValue);
                    break;
                case "dateOfBirth": staff.setDateOfBirth(LocalDate.parse((String) fieldValue));
                    break;
                case "yearsExperience":
                    if (fieldValue instanceof Integer) { staff.setYearsExperience((Integer) fieldValue); }
                    else if (fieldValue instanceof Number) { // Handles cases where the number is a double or float.
                        staff.setYearsExperience(((Number) fieldValue).intValue());
                    }
                    else { throw new IllegalArgumentException("Illegal data type"); }
                    break;
                default:
                    throw new IllegalArgumentException("Unknown field name");

            } // End switch
        } // End for
    }

    /**
     * DTO to model the response containing successfully updated staff records.
     * The response contains a message and a list of updated staff usernames.
     *
     * @author Benjamin Leiby
     */
    @Data
    public static class StaffUpdateResponse {
        private String message;
        private List<String> staffUsernames; // Usernames of updated staff records
        public StaffUpdateResponse(String responseMessage, List<String> staffUsernames) {
            this.message = responseMessage;
            this.staffUsernames = staffUsernames;
        }
        public StaffUpdateResponse() {
            staffUsernames = new ArrayList<>();
        }
    }

    /**
     * Attempts to insert new staff members from a list of new staff information.
     * <p>
     * This method allows an authorized user to add new staff members to the system.
     * It securely hashes the password and associates the staff with the hospital
     * the authenticated user works at.
     *
     * @param authorization the JWT token of the authorized user.
     * @param staffCreationRequest the DTO containing a collection of new staff member information.
     * @return a {@link ResponseEntity} containing a {@link StaffCreationResponse} with the result message and list of added staff.
     */
    @PostMapping
    public ResponseEntity<StaffCreationResponse> createStaff(
            @RequestHeader(name="Authorization") String authorization,
            @RequestBody StaffCreationRequest staffCreationRequest)
    {

        StaffCreationResponse response = new StaffCreationResponse();

        String authenticatedUsername = jwtUtil.getUsername(authorization);
        Optional<Staff> authenticatedStaff = staffRepository.findByUsername(authenticatedUsername);
        if (authenticatedStaff.isEmpty()) {
            response.setMessage("Failed: Accessing user not found.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

        Hospital hospital = authenticatedStaff.get().getWorksAt();
        PasswordEncoder encoder = passwordSecurity.getPasswordEncoder();

        List<Staff> staffList = staffCreationRequest.getStaff().stream()
                .map(dto -> {
                    Staff staff = new Staff();
                    staff.setUsername(dto.getUsername());
                    staff.setPasswordHash(encoder.encode(dto.getPasswordHash()));
                    staff.setRole(Role.fromValue(dto.getRole()));
                    staff.setFirstName(dto.getFirstName());
                    staff.setLastName(dto.getLastName());
                    staff.setEmail(dto.getEmail());
                    staff.setPhone(dto.getPhone());
                    staff.setAddressLine1(dto.getAddressLine1());
                    staff.setAddressLine2(dto.getAddressLine2());
                    staff.setCity(dto.getCity());
                    staff.setState(dto.getState());
                    staff.setPostal(dto.getPostal());
                    staff.setCountry(dto.getCountry());
                    staff.setDateOfBirth(dto.getDateOfBirth());
                    staff.setWorksAt(hospital);
                    return staff;
                })
                .toList();

        try { staffRepository.saveAll(staffList); }
        catch (Exception e) {
            response.setMessage("Failed. Error saving records to database.");
            response.setStaffUsernames(new ArrayList<>());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

        // Extract the usernames from the staffList
        List<String> staffUsernames = staffList.stream()
                .map(Staff::getUsername)
                .collect(Collectors.toList());

        response.setMessage("Success. Records created.");
        response.setStaffUsernames(staffUsernames);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    /**
     * Retrieves the list of staff members' information at the accessing user's hospital.
     * <p>
     * This method returns a list of all staff members working at the same hospital as the
     * authenticated user.
     *
     * @param authorization the JWT token of the authorized user.
     * @return a {@link ResponseEntity} containing an {@link AllStaffDTO} with the list of staff information.
     */
    @GetMapping("/all")
    public ResponseEntity<AllStaffDTO> getAllStaff(
            @RequestHeader(name="Authorization") String authorization)
    {

        String authenticatedUsername = jwtUtil.getUsername(authorization);
        Optional<Staff> authenticatedStaff = staffRepository.findByUsername(authenticatedUsername);
        if (authenticatedStaff.isEmpty()) {
            AllStaffDTO response = new AllStaffDTO("Failed: Accessing user not found.", new ArrayList<>());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

        List<StaffRepository.StaffProjection> staffList = staffRepository.findByWorksAt(
                authenticatedStaff.get().getWorksAt());

        if (staffList.isEmpty()) {
            AllStaffDTO response = new AllStaffDTO("Failed: No staff members found.", new ArrayList<>());
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
        }

        AllStaffDTO response = new AllStaffDTO("Success.", staffList);
        return ResponseEntity.status(HttpStatus.OK).body(response);

    }

    /**
     * DTO for a list of staff information.
     * This record models the response containing a message and a list of staff members' info.
     *
     * @param message a message describing the result.
     * @param staffList a list of projections containing staff details.
     */
    public record AllStaffDTO (String message, List<StaffRepository.StaffProjection> staffList) { }
}