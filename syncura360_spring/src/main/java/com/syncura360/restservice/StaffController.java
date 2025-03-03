package com.syncura360.restservice;

import com.syncura360.model.Hospital;
import com.syncura360.model.Staff;
import com.syncura360.security.JwtUtil;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins="*")
@RequestMapping("/staff")
public class StaffController {

    @Autowired
    StaffRepository staffRepository;

    @Autowired
    JwtUtil jwtUtil;

    @PostMapping
    public ResponseEntity<StaffCreationResponse> createStaff(@RequestHeader(name="Authorization") String authorization, @RequestBody StaffCreationRequest staffCreationRequest) {

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
                    staff.setRole(dto.getRole());
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
        List<Integer> staffIds = staffList.stream()
                .map(Staff::getId)
                .collect(Collectors.toList());

        StaffCreationResponse response = new StaffCreationResponse("Staff created successfully.", staffIds);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    @GetMapping("/all")
    public ResponseEntity<List<StaffRepository.StaffProjection>> getAllStaff(@RequestHeader(name="Authorization") String authorization) {

        Optional<Staff> authenticatedStaff = staffRepository.findByUsername(jwtUtil.getUsername(authorization));

        if (authenticatedStaff.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        List<StaffRepository.StaffProjection> staffList = staffRepository.findByWorksAt(authenticatedStaff.get().getWorksAt());

        if (staffList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }

        return ResponseEntity.ok().body(staffList);

    }

}
