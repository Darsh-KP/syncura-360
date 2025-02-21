package com.example.syncura360_spring.restservice;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StaffRepository extends JpaRepository<Staff, Integer> {

    List<Staff> findByUsername(String username);

    List<Staff> findByEmail(String email);

    List<Staff> findByPhone(String email);

}
