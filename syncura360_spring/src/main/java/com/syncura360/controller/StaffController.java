package com.syncura360.controller;

import com.syncura360.model.Hospital;
import com.syncura360.model.Staff;
import com.syncura360.model.enums.Role;
import com.syncura360.security.passwordSecurity;
import com.syncura360.unorganized.StaffCreationRequest;
import com.syncura360.unorganized.StaffCreationResponse;
import com.syncura360.repository.StaffRepository;
import com.syncura360.unorganized.StaffUpdateRequest;
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
 * CRUD operations on staff credentials.
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
     * Attempts to apply a list of staff info updates.
     * @param authorization JWT.
     * @param staffUpdateRequest DTO to model a collection of attempted staff updates.
     * @return StaffUpdateResponse DTO to communicate which updates were successful.
     */
    @PutMapping("/batch")
    public ResponseEntity<StaffUpdateResponse> updateStaff(
            @RequestHeader(name="Authorization") String authorization,
            @RequestBody StaffUpdateRequest staffUpdateRequest)
    {

        StaffUpdateResponse response = new StaffUpdateResponse();
        List<String> failed = new ArrayList<String>(); // Track to determine partial success.

        String authenticatedUsername = jwtUtil.getUsername(authorization);
        Optional<Staff> authenticatedStaff = staffRepository.findByUsername(authenticatedUsername);
        if (authenticatedStaff.isEmpty()) {
            response.setMessage("Failed: Accessing user not found.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

        Hospital hospital = authenticatedStaff.get().getWorksAt();

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
                } catch (Exception e) { failed.add(updateDto.getUsername()); }
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

    private void applyUpdates(Staff staff, Map<String, Object> fields) {

        for (Map.Entry<String, Object> entry : fields.entrySet()) {

            String fieldName = entry.getKey();
            Object fieldValue = entry.getValue();

            switch (fieldName) {
                
                case "passwordHash":
                    staff.setPasswordHash((String) fieldValue);
                    break;
                case "firstName":
                    staff.setFirstName((String) fieldValue);
                    break;
                case "lastName":
                    staff.setLastName((String) fieldValue);
                    break;
                case "email":
                    staff.setEmail((String) fieldValue);
                    break;
                case "dateOfBirth":
                    if (fieldValue instanceof LocalDate) {
                        staff.setDateOfBirth((LocalDate) fieldValue);
                    }
                    else {
                        System.err.println("Bad date format");
                    }
                    break;
                case "phone":
                    staff.setPhone((String) fieldValue);
                    break;
                case "addressLine1":
                    staff.setAddressLine1((String) fieldValue);
                    break;
                case "addressLine2":
                    staff.setAddressLine2((String) fieldValue);
                    break;
                case "city":
                    staff.setCity((String) fieldValue);
                    break;
                case "state":
                    staff.setState((String) fieldValue);
                    break;
                case "postal":
                    staff.setPostal((String) fieldValue);
                    break;
                case "country":
                    staff.setCountry((String) fieldValue);
                    break;
                case "specialty":
                    staff.setSpecialty((String) fieldValue);
                    break;
                case "yearsExperience":
                    if (fieldValue instanceof Integer) {
                        staff.setYearsExperience((Integer) fieldValue);
                    } else if (fieldValue instanceof Number) { // Handles cases where the number is a double or float.
                        staff.setYearsExperience(((Number) fieldValue).intValue());
                    }
                    break;
                default:
                    System.err.println("Unknown field: " + fieldName);
                    break;
            }
        }
    }

    /**
     * DTO to model response containing successfully updated records.
     * @author Benjamin Leiby
     */
    @Data
    public static class StaffUpdateResponse {
        private String message;
        private List<String> staffUsernames; // Of updated records
        public StaffUpdateResponse(String responseMessage, List<String> staffUsernames) {
            this.message = responseMessage;
            this.staffUsernames = staffUsernames;
        }
        public StaffUpdateResponse() {
            staffUsernames = new ArrayList<String>();
        }
    }

    @PostMapping
    public ResponseEntity<StaffCreationResponse> createStaff(
            @RequestHeader(name="Authorization") String authorization,
            @RequestBody StaffCreationRequest staffCreationRequest)
    {

        Optional<Staff> authenticatedStaff = staffRepository.findByUsername(jwtUtil.getUsername(authorization));

        if (authenticatedStaff.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
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

        staffRepository.saveAll(staffList);

        // Extract the IDs from the updated staffList
        List<String> staffIds = staffList.stream()
                .map(Staff::getUsername)
                .collect(Collectors.toList());

        StaffCreationResponse response = new StaffCreationResponse("Staff created successfully.", staffIds);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    @GetMapping("/all")
    public ResponseEntity<List<StaffRepository.StaffProjection>> getAllStaff(
            @RequestHeader(name="Authorization") String authorization)
    {

        Optional<Staff> authenticatedStaff = staffRepository.findByUsername(jwtUtil.getUsername(authorization));

        if (authenticatedStaff.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        List<StaffRepository.StaffProjection> staffList = staffRepository.findByWorksAt(
                authenticatedStaff.get().getWorksAt());

        if (staffList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }

        return ResponseEntity.ok().body(staffList);

    }

}