package com.example.syncura360_spring.restservice;

import com.example.syncura360_spring.model.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HospitalRepository extends JpaRepository<Hospital, Integer> {

    List<Hospital> findByHospitalName(String username);

    List<Hospital> findByTelephone(String phone);

    List<Hospital> addressLine1(String address);

}
