package com.syncura360.controller;

import com.syncura360.model.Hospital;
import com.syncura360.model.Staff;
import com.syncura360.model.enums.Role;
import com.syncura360.unorganized.StaffCreationRequest;
import com.syncura360.unorganized.StaffCreationResponse;
import com.syncura360.repository.StaffRepository;
import com.syncura360.unorganized.StaffUpdateRequest;
import com.syncura360.security.JwtUtil;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins="*")
@RequestMapping("/staff")
public class StaffController {

    @Autowired
    StaffRepository staffRepository;

    @Autowired
    JwtUtil jwtUtil;

    @PutMapping("/batch")
    public ResponseEntity<StaffUpdateResponse> updateStaff(
            @RequestHeader(name="Authorization") String authorization,
            @RequestBody StaffUpdateRequest staffUpdateRequest)
    {

        Optional<Staff> authenticatedStaff = staffRepository.findByUsername(jwtUtil.getUsername(authorization));

        if (authenticatedStaff.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        Hospital hospital = authenticatedStaff.get().getWorksAt();
        List<Integer> idList = new ArrayList<Integer>();

        for (StaffUpdateRequest.StaffUpdateDto updateDto : staffUpdateRequest.getUpdates()) {

            Optional<Staff> optionalStaff = staffRepository.findById(updateDto.getId());

            if (optionalStaff.isPresent() && optionalStaff.get().getWorksAt() == hospital) {
                try {
                    Staff staff = optionalStaff.get();
                    applyUpdates(staff, updateDto.getFields());
                    staffRepository.save(staff);
                    idList.add(updateDto.getId());
                } catch (Exception e) {
                    System.err.println("Failed to update staff record: " + e);
                }
            }
        }

        StaffUpdateResponse response = new StaffUpdateResponse();
        response.setStaffIds(idList);
        response.setMessage("Update successful");
        return ResponseEntity.ok(response);

    }

    private void applyUpdates(Staff staff, Map<String, Object> fields) {

        for (Map.Entry<String, Object> entry : fields.entrySet()) {

            String fieldName = entry.getKey();
            Object fieldValue = entry.getValue();

            switch (fieldName) {


                case "username":
                    staff.setUsername((String) fieldValue);
                    break;
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

    @Data
    public static class StaffUpdateResponse {
        private String message;
        private List<Integer> staffIds;
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
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(16);

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
