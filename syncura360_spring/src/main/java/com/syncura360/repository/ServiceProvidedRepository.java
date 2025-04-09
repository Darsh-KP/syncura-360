package com.syncura360.repository;

import com.syncura360.model.ServiceProvided;
import com.syncura360.model.ServiceProvidedId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceProvidedRepository extends JpaRepository<ServiceProvided, ServiceProvidedId> {
}
