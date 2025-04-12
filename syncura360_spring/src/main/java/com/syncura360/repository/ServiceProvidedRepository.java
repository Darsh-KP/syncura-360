package com.syncura360.repository;

import com.syncura360.model.ServiceProvided;
import com.syncura360.model.ServiceProvidedId;
import com.syncura360.model.Visit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceProvidedRepository extends JpaRepository<ServiceProvided, ServiceProvidedId> {

    List<ServiceProvided> findAllByVisit(Visit visit);
}
