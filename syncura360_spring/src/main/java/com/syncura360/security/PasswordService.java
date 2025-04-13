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

@Service
public class PasswordService {
    AuthenticationManager authenticationManager;
    StaffRepository staffRepository;

    public PasswordService(AuthenticationManager authenticationManager, StaffRepository staffRepository) {
        this.authenticationManager = authenticationManager;
        this.staffRepository = staffRepository;
    }

    // Change staff's password
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
