package com.syncura360.security;

import com.syncura360.model.Staff;
import com.syncura360.repository.StaffRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service for handling staff password-related operations such as changing passwords.
 *
 * @author Darsh-KP
 */
@Service
public class PasswordService {
    AuthenticationManager authenticationManager;
    StaffRepository staffRepository;

    /**
     * Constructs a PasswordService with the required dependencies.
     *
     * @param authenticationManager Authentication manager for verifying credentials.
     * @param staffRepository       Repository for accessing staff data.
     */
    public PasswordService(AuthenticationManager authenticationManager, StaffRepository staffRepository) {
        this.authenticationManager = authenticationManager;
        this.staffRepository = staffRepository;
    }

    /**
     * Changes the password of a staff member after verifying the current password.
     *
     * @param username    The username of the staff member.
     * @param oldPassword The current password for verification.
     * @param newPassword The new password to set.
     * @throws BadCredentialsException   If the current password is incorrect.
     * @throws EntityNotFoundException   If the staff member is not found.
     */
    public void changeStaffPassword(String username, String oldPassword, String newPassword) {
        // Verify the old password
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, oldPassword));
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Current password is incorrect.");
        }

        // Try to fetch the staff
        Optional<Staff> optionalStaff = staffRepository.findByUsername(username);
        if (optionalStaff.isEmpty()) {
            throw new EntityNotFoundException("Staff with given username does not exist.");
        }

        // Get the staff
        Staff staff = optionalStaff.get();

        // Set the new password
        staff.setPasswordHash(passwordSecurity.getPasswordEncoder().encode(newPassword));

        // Save the new password
        staffRepository.save(staff);
    }
}
