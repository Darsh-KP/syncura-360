package com.syncura360.restservice;

import com.syncura360.model.Hospital;
import com.syncura360.model.Staff;
import com.syncura360.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin(origins="*")
@RequestMapping("/staff")
public class StaffController {

    @Autowired
    StaffRepository staffRepository;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    BCryptPasswordEncoder encoder;

//    @GetMapping
//    public ResponseEntity<List<Staff>> getStaffByCriteria(@RequestHeader(name="Authorization") String authorization, @RequestParam Map<String, String> params) {
//
//    }

    @PostMapping("")

    @GetMapping("/all")
    public ResponseEntity<List<Staff>> getAllStaff(@RequestHeader(name="Authorization") String authorization) {

        Optional<Staff> optionalStaff = staffRepository.findByUsername(jwtUtil.getUsername(authorization));
        Staff staff;

        if (optionalStaff.isPresent()) {
            staff = optionalStaff.get();
        }
        else {
            return ResponseEntity.internalServerError().header("message", "bad token, username not valid.").build();
        }

        return ResponseEntity.ok(staffRepository.findByWorksAt(staff.getWorksAt()));

    }

}