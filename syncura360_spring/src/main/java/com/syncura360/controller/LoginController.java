package com.syncura360.controller;

import com.syncura360.dto.LoginInfo;
import com.syncura360.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.NoSuchElementException;

/**
 * Handles all incoming login requests.
 * @author Benjamin Leiby
 */
@RestController
@CrossOrigin(origins = "*")
public class LoginController {

    @Autowired
    JwtUtil jwtUtil;
    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * @param loginInfo DTO containing username and plaintext password.
     * @return loginResponse DTO containing message, JWT, and authenticated user's role.
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

            String token = jwtUtil.generateJwtToken(authentication.getName(), role);
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
     * @param message
     * @param role
     */
    public record LoginResponse(String message, String role) {}

}