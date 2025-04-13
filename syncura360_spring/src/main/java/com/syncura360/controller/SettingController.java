package com.syncura360.controller;

import com.syncura360.dto.ErrorConvertor;
import com.syncura360.dto.GenericMessageResponseDTO;
import com.syncura360.dto.Hospital.HospitalSettingFetch;
import com.syncura360.dto.Staff.StaffPasswordChangeForm;
import com.syncura360.dto.Staff.StaffSettingFetch;
import com.syncura360.security.JwtUtil;
import com.syncura360.security.PasswordService;
import com.syncura360.service.HospitalService;
import com.syncura360.service.StaffService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

// TODO Javadoc!!!
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/setting")
public class SettingController {
    JwtUtil jwtUtil;
    HospitalService hospitalService;
    StaffService staffService;
    PasswordService passwordService;

    public SettingController(JwtUtil jwtUtil, HospitalService hospitalService, StaffService staffService, PasswordService passwordService) {
        this.jwtUtil = jwtUtil;
        this.hospitalService = hospitalService;
        this.staffService = staffService;
        this.passwordService = passwordService;
    }

    @PutMapping("/password")
    public ResponseEntity<GenericMessageResponseDTO> modifyPassword(
            @RequestHeader(name="Authorization") String authorization, @Valid @RequestBody StaffPasswordChangeForm staffPasswordChangeForm, BindingResult bindingResult) {
        // Basic DTO validation
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new GenericMessageResponseDTO("Invalid request: " + ErrorConvertor.convertErrorsToString(bindingResult)));
        }

        // Get the username from the logged in staff
        String username = jwtUtil.getUsername(authorization);

        // Attempt to change the staff's password
        try {
            passwordService.changeStaffPassword(username, staffPasswordChangeForm.getCurrentPassword(), staffPasswordChangeForm.getNewPassword());
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new GenericMessageResponseDTO(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new GenericMessageResponseDTO("An unexpected error occurred."));
        }

        // Success, password changed
        return ResponseEntity.status(HttpStatus.OK).body(new GenericMessageResponseDTO("Successfully updated the password."));
    }

    @GetMapping("/hospital")
    public ResponseEntity<HospitalSettingFetch> fetchHospitalSetting(
            @RequestHeader(name="Authorization") String authorization) {
        // Get the hospital id from the logged in staff
        int hospitalId = Integer.parseInt(jwtUtil.getHospitalID(authorization));

        // Attempt to fetch the hospital settings (info)
        HospitalSettingFetch hospitalSettingFetch;
        try {
            hospitalSettingFetch = hospitalService.getHospitalSetting(hospitalId);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        // Success, return the hospital settings
        return ResponseEntity.status(HttpStatus.OK).body(hospitalSettingFetch);
    }

    @GetMapping("/staff")
    public ResponseEntity<StaffSettingFetch> fetchStaffSetting(
            @RequestHeader(name="Authorization") String authorization) {
        // Get the username from the logged in staff
        String username = jwtUtil.getUsername(authorization);

        // Attempt to fetch the staff settings (info)
        StaffSettingFetch staffSettingFetch;
        try {
            staffSettingFetch = staffService.getStaffSetting(username);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        // Success, return the hospital settings
        return ResponseEntity.status(HttpStatus.OK).body(staffSettingFetch);
    }
}
