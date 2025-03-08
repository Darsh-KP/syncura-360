package com.syncura360.controller;

import com.syncura360.dto.LoginInfo;
import com.syncura360.repository.StaffRepository;
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

@CrossOrigin(origins = "*")
@RestController
public class LoginController {

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    StaffRepository staffRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginInfo loginInfo) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginInfo.getUsername(), loginInfo.getPassword())
            );

            String role = authentication.getAuthorities().iterator().next().getAuthority();
            String token = jwtUtil.generateJwtToken(authentication.getName(), role);
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);

            return ResponseEntity.ok().headers(headers).body(new LoginResponse("Authentication successful.", role));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed. Invalid credentials.");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed: role not found.");
        }
    }

    public record LoginResponse(String message, String role) {}

}
