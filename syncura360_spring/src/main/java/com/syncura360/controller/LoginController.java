package com.syncura360.controller;

import com.syncura360.dto.Authentication.LoginInfo;
import com.syncura360.model.Staff;
import com.syncura360.repository.StaffRepository;
import com.syncura360.security.JwtUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Handles all incoming login requests.
 *
 * @author Benjamin Leiby
 */
@RestController
@CrossOrigin(origins = "*")
public class LoginController {
    JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    StaffRepository staffRepository;

    /**
     * Constructor for initializing LoginController with required dependencies.
     * Uses constructor injection for necessary components.
     *
     * @param jwtUtil Used to add jwt details for logged-in user.
     * @param authenticationManager Used to authenticate the user trying to log in.
     * @param staffRepository The repository used to access user details.
     */
    public LoginController(JwtUtil jwtUtil, AuthenticationManager authenticationManager, StaffRepository staffRepository) {
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.staffRepository = staffRepository;
    }

    /**
     * Attempts to authenticate a user with given credentials.
     *
     * @param loginInfo DTO containing username and plaintext password.
     * @return {@link LoginResponse} DTO containing message, JWT, and authenticated user's role.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginInfo loginInfo) {

        HttpHeaders headers = new HttpHeaders();
        HttpStatus responseType = HttpStatus.UNAUTHORIZED;
        String responseMessage;
        String role = "None";

        try {

            // Throws authentication exception if invalid credentials.
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginInfo.getUsername(), loginInfo.getPassword())
            );

            // Throws no element exception if the credentials are not linked to a role (has no permissions).
            role = authentication.getAuthorities().iterator().next().getAuthority();

            // Get the hospital ID where the staff works
            Optional<Staff> staff = staffRepository.findByUsername(loginInfo.getUsername());
            if (staff.isEmpty()) {
                responseMessage = "Unable to load staff's details.";
                role = "None";
                return ResponseEntity.status(responseType).headers(headers).body(new LoginResponse(responseMessage, role));
            }
            String hospitalID = staff.get().getWorksAt().getId().toString();

            String token = jwtUtil.generateJwtToken(authentication.getName(), role, hospitalID);
            headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);

            responseType = HttpStatus.OK;
            responseMessage = "Authentication successful.";

        }
        catch (AuthenticationException e) { responseMessage = "Failed: Invalid credentials."; }
        catch (NoSuchElementException e) { responseMessage = "Failed: User access denied."; }

        return ResponseEntity.status(responseType).headers(headers).body(new LoginResponse(responseMessage, role));

    }

    /**
     * DTO for login response.
     *
     * @param message
     * @param role
     */
    public record LoginResponse(String message, String role) {}

}