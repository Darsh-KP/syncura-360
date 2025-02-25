package com.syncura360.restservice;

import com.syncura360.model.Staff;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins="*")
@RequestMapping("/staff")
public class StaffController {

    @Autowired
    StaffRepository staffRepository;

    BCryptPasswordEncoder encoder;

    @GetMapping("/all")
    public List<Staff> getAllStaffInHospital() {
        return staffRepository.findAll();
    }


}