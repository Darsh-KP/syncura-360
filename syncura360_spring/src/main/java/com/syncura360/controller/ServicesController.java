package com.syncura360.controller;

import com.syncura360.dto.GenericMessageResponseDTO;
import com.syncura360.dto.Service.*;
import com.syncura360.model.*;
import com.syncura360.repository.ServiceRepository;
import com.syncura360.security.JwtUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/services")
public class ServicesController {

    @Autowired
    JwtUtil jwtUtil;
    @Autowired
    ServiceRepository serviceRepository;

    @DeleteMapping
    @Transactional
    public ResponseEntity<GenericMessageResponseDTO> deleteServices(
            @RequestHeader(name = "Authorization") String authorization,
            @RequestBody DeleteServicesDTO deleteServicesDTO)
    {

        int hospitalId = Integer.parseInt(jwtUtil.getHospitalID(authorization));
        List<Service> toDelete = new ArrayList<>();

        for (String name: deleteServicesDTO.getNames()) {

            ServiceId id = new ServiceId();
            id.setHospitalId(hospitalId);
            id.setName(name);

            Optional<Service> serviceEntity = serviceRepository.findById(id);
            if (serviceEntity.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new GenericMessageResponseDTO("Failed: service not found."));
            }

            toDelete.add(serviceEntity.get());

        }

        try {
            serviceRepository.deleteAll(toDelete);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new GenericMessageResponseDTO("Failed: error deleting records."));
        }

        return ResponseEntity.status(HttpStatus.OK).body(new GenericMessageResponseDTO("Success."));

    }

    @PutMapping("/update")
    @Transactional
    public ResponseEntity<GenericMessageResponseDTO> updateServices(
            @RequestHeader(name="Authorization") String authorization,
            @RequestBody UpdateServicesRequest updateServicesRequest)
    {

        int hospitalId = Integer.parseInt(jwtUtil.getHospitalID(authorization));

        try {
            for (ServiceUpdate update : updateServicesRequest.getUpdates()) {

                ServiceId id = new ServiceId();
                id.setName(update.getName());
                id.setHospitalId(hospitalId);

                Optional<Service> toModify = serviceRepository.findById(id);
                if (toModify.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new GenericMessageResponseDTO("Failed: Service not found."));
                }

                Service updated = getUpdatedService(update, hospitalId, toModify.get());

                serviceRepository.delete(toModify.get());
                serviceRepository.save(updated);

            } // end for
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new GenericMessageResponseDTO("Failed: error saving changes to database."));
        }

        return ResponseEntity.status(HttpStatus.OK).body(new GenericMessageResponseDTO("Success."));

    }

    @PostMapping
    @Transactional
    public ResponseEntity<ServicesDTO> getAllServices(
            @RequestHeader(name="Authorization") String authorization,
            @RequestBody ServiceDTO serviceDTO)
    {

        int hospitalId = Integer.parseInt(jwtUtil.getHospitalID(authorization));
        List<ServiceDTO> dtos = new ArrayList<>();
        List<Service> serviceEntities;

        try {
            serviceEntities = serviceRepository.findHospitalServicesByNameAndCategory(serviceDTO.getName(), serviceDTO.getCategory(), hospitalId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ServicesDTO("Failed: Error querying database."));
        }

        if (serviceEntities.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(new ServicesDTO("Success. No services found."));
        }

        for (Service service : serviceEntities) {
            ServiceDTO dto = new ServiceDTO();
            dto.setName(service.getId().getName());
            dto.setDescription(service.getDescription());
            dto.setCategory(service.getCategory());
            dto.setCost(service.getCost());
            dtos.add(dto);
        }

        return ResponseEntity.status(HttpStatus.OK).body(new ServicesDTO("Success.", dtos));

    }

    @SuppressWarnings("ExtractMethodRecommender")
    @PostMapping("/new")
    @Transactional
    public ResponseEntity<GenericMessageResponseDTO> createNewServices(
            @RequestHeader(name="Authorization") String authorization,
            @RequestBody ServicesRequest serviceRequest)
    {

        int hospitalId = Integer.parseInt(jwtUtil.getHospitalID(authorization));
        List<Service> serviceEntities = new ArrayList<Service>();

        for (ServiceDTO serviceDTO : serviceRequest.getServices()) {
            ServiceId id = new ServiceId();
            id.setName(serviceDTO.getName());
            id.setHospitalId(hospitalId);
            Service service = new Service();
            service.setId(id);
            service.setCategory(serviceDTO.getCategory());
            service.setDescription(serviceDTO.getDescription());
            service.setCost(serviceDTO.getCost());
            serviceEntities.add(service);
        }

        try {
            serviceRepository.saveAll(serviceEntities);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new GenericMessageResponseDTO("Failed: error saving to database."));
        }

        return ResponseEntity.status(HttpStatus.OK).body(new GenericMessageResponseDTO("Success."));

    }

    private Service getUpdatedService(ServiceUpdate update, int hospitalId, Service toModify) throws NoSuchElementException {
        ServiceDTO updates = update.getUpdates();
        Service updated = new Service();
        ServiceId updatedId = new ServiceId();

        updatedId.setHospitalId(hospitalId);

        if (updates.getName() != null) {
            updatedId.setName(updates.getName());
        } else {
            updatedId.setName(toModify.getId().getName());
        }

        updated.setId(updatedId);

        if (updates.getCategory() != null) {
            updated.setCategory(updates.getCategory());
        } else {
            updated.setCategory(toModify.getCategory());
        }

        if (updates.getDescription() != null) {
            updated.setDescription(updates.getDescription());
        } else {
            updated.setDescription(toModify.getDescription());
        }

        if (updates.getCost() != null) {
            updated.setCost(updates.getCost());
        } else {
            updated.setCost(toModify.getCost());
        }
        return updated;
    }

}
