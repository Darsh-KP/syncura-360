package com.example.syncura360_spring.restservice;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HospitalRepository extends JpaRepository<Hospital, Integer> {

    List<Hospital> findByName(String username);

    List<Hospital> findByPhone(String phone);

    List<Hospital> findByAddress(String address);

}
