package com.syncura360.service;

import com.syncura360.controller.DeleteRoomDTO;
import com.syncura360.dto.Visit.AddRoomDTO;
import com.syncura360.dto.Visit.DoctorDTO;
import com.syncura360.dto.Visit.NoteDTO;
import com.syncura360.dto.Visit.VisitDTO;
import com.syncura360.dto.Drug.DrugFetchDTO;
import com.syncura360.dto.Service.ServiceDTO;
import com.syncura360.dto.Visit.AddDrugDTO;
import com.syncura360.dto.Visit.AddServiceDTO;
import com.syncura360.dto.Visit.VisitCreationDTO;
import com.syncura360.model.*;
import com.syncura360.model.enums.BedStatus;
import com.syncura360.model.enums.Role;
import com.syncura360.repository.*;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.hibernate.sql.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class VisitService {

    @Autowired
    VisitRepository visitRepository;
    @Autowired
    PatientInfoRepository patientInfoRepository;
    @Autowired
    StaffRepository staffRepository;
    @Autowired
    ServiceRepository serviceRepository;
    @Autowired
    ServiceProvidedRepository serviceProvidedRepository;
    @Autowired
    DrugRepository drugRepository;
    @Autowired
    DrugAdministeredRepository drugAdministeredRepository;
    @Autowired
    RoomRepository roomRepository;
    @Autowired
    RoomAssignmentRepository roomAssignmentRepository;
    @Autowired
    BedRepository bedRepository;

    /**
     * Attempt to create start a new visit.
     * @param hospitalId Hospital the patient is visiting.
     * @param visitCreationDTO DTO to model incoming visit creation request.
     */
    @Transactional
    public void createVisit(int hospitalId, VisitCreationDTO visitCreationDTO) {

        Integer patientId = visitCreationDTO.getPatientID();

        if (!patientInfoRepository.existsById(patientId)) {
            throw new EntityNotFoundException("Patient id does not exist");
        }

        Optional<Visit> optionalVisit = visitRepository.findCurrentVisitById(visitCreationDTO.getPatientID(), hospitalId);
        if (optionalVisit.isPresent()) {
            throw new EntityExistsException("Patient visit is ongoing.");
        }

        VisitId id = new VisitId(hospitalId, patientId);
        Visit visit = new Visit(id, visitCreationDTO.getReasonForVisit());
        visitRepository.save(visit);
    }

    /**
     * Attempt to add a service to a given visit.
     * @param hospitalId Hospital the patient is visiting.
     * @param addServiceDTO DTO to model incoming service addition request.
     */
    @Transactional
    public void addService(int hospitalId, AddServiceDTO addServiceDTO) throws DateTimeParseException {

        Optional<Visit> visit = visitRepository.findCurrentVisitById(addServiceDTO.getPatientID(), hospitalId);
        if (visit.isEmpty()) {
            throw new EntityNotFoundException("Visit not found.");
        }

        ServiceProvidedId serviceProvidedId = new ServiceProvidedId(
            hospitalId, addServiceDTO.getPatientID(),
            LocalDateTime.parse(addServiceDTO.getVisitAdmissionDateTime())
        );

        Optional<Staff> staff = staffRepository.findByUsername(addServiceDTO.getPerformedBy());
        if (staff.isEmpty() || staff.get().getWorksAt().getId() != hospitalId) {
            throw new EntityNotFoundException("Providing staff not found.");
        }

        ServiceId serviceId = new ServiceId(hospitalId, addServiceDTO.getService());

        Optional<com.syncura360.model.Service> service = serviceRepository.findById(serviceId);
        if (service.isEmpty() || service.get().getId().getHospitalId() != hospitalId) {
            throw new EntityNotFoundException("Service not found.");
        }

        ServiceProvided serviceProvided = new ServiceProvided(serviceProvidedId, staff.get(), service.get());
        serviceProvidedRepository.save(serviceProvided);
    }

    /**
     * Attempt to add a drug to a given visit.
     * @param hospitalId Hospital the patient is visiting.
     * @param addDrugDTO DTO to model incoming drug addition request.
     */
    @Transactional
    public void addDrug(int hospitalId, AddDrugDTO addDrugDTO) throws DateTimeParseException {

        Optional<Visit> visit = visitRepository.findCurrentVisitById(addDrugDTO.getPatientID(), hospitalId);
        if (visit.isEmpty()) {
            throw new EntityNotFoundException("Visit not found.");
        }

        DrugAdministeredId drugAdministeredId = new DrugAdministeredId(
            hospitalId, addDrugDTO.getPatientID(),
            LocalDateTime.parse(addDrugDTO.getVisitAdmissionDateTime())
        );

        Optional<Staff> staff = staffRepository.findByUsername(addDrugDTO.getAdministeredBy());
        if (staff.isEmpty() || staff.get().getWorksAt().getId() != hospitalId) {
            throw new EntityNotFoundException("Providing staff not found.");
        }

        DrugId drugId = new DrugId(hospitalId, addDrugDTO.getDrug());

        Optional<Drug> drug = drugRepository.findById(drugId);
        if (drug.isEmpty()) {
            throw new EntityNotFoundException("Drug not found.");
        }

        DrugAdministered drugAdministered = new DrugAdministered(
            drugAdministeredId, drug.get(), staff.get()
        );

        drugAdministeredRepository.save(drugAdministered);
    }

    @Transactional
    public void removeRoom(int hospitalId, DeleteRoomDTO deleteRoomDTO) {

        Optional<Visit> visit = visitRepository.findCurrentVisitById(deleteRoomDTO.getPatientID(), hospitalId);
        if (visit.isEmpty()) {
            throw new EntityNotFoundException("Visit not found.");
        }

        Optional<RoomAssignment> currentAssignment = roomAssignmentRepository.findCurrentAssignmentById(
            deleteRoomDTO.getPatientID(), hospitalId
        );

        if (currentAssignment.isEmpty()) {
            throw new EntityNotFoundException("Patient is not assigned to a room.");
        }

        Optional<Room> room = roomRepository.findById(currentAssignment.get().getRoom().getId());
        if (room.isEmpty()) {
            throw new EntityNotFoundException("Room not found.");
        }

        List<Bed> occupiedBeds = bedRepository.findAllByRoomAndStatus(room.get(), BedStatus.Occupied);
        if (occupiedBeds.isEmpty()) {
            throw new EntityNotFoundException("No beds occupied.");
        }

        RoomAssignment entity = currentAssignment.get();
        entity.setIsRemoved(true);
        roomAssignmentRepository.save(entity);

        Bed occupied = occupiedBeds.getFirst();
        occupied.setStatus(BedStatus.Vacant);
        bedRepository.save(occupied);
    }

    @Transactional
    public void addRoom(int hospitalId, AddRoomDTO addRoomDTO) throws DateTimeParseException {

        Optional<Visit> visit = visitRepository.findCurrentVisitById(addRoomDTO.getPatientID(), hospitalId);
        if (visit.isEmpty()) {
            throw new EntityNotFoundException("Visit not found.");
        }

        RoomId roomId = new RoomId(hospitalId, addRoomDTO.getRoomName());

        Optional<Room> room = roomRepository.findById(roomId);
        if (room.isEmpty()) {
            throw new EntityNotFoundException("Room not found.");
        }

        Optional<RoomAssignment> currentAssignment = roomAssignmentRepository.findCurrentAssignmentById(
            addRoomDTO.getPatientID(), hospitalId
        );

        if (currentAssignment.isPresent()) {
            throw new EntityExistsException("Patient is already assigned to a room.");
        }

        List<Bed> availableBeds = bedRepository.findAllByRoomAndStatus(room.get(), BedStatus.Vacant);

        if (availableBeds.isEmpty()) {
            throw new EntityNotFoundException("No beds available");
        }

        Bed nextAvailable = availableBeds.getFirst();

        RoomAssignmentId roomAssignmentId = new RoomAssignmentId(
            hospitalId, addRoomDTO.getPatientID(),
            LocalDateTime.parse(addRoomDTO.getVisitAdmissionTime())
        );

        RoomAssignment roomAssignment = new RoomAssignment(roomAssignmentId, false, room.get());
        roomAssignmentRepository.save(roomAssignment);

        nextAvailable.setStatus(BedStatus.Occupied);
        bedRepository.save(nextAvailable);
    }

    @Transactional
    public void editNote(int hospitalId, NoteDTO noteDTO) {

        Optional<Visit> visit = visitRepository.findCurrentVisitById(noteDTO.getPatientID(), hospitalId);
        if (visit.isEmpty()) {
            throw new EntityNotFoundException("Visit not found.");
        }

        visit.get().setVisitNote(noteDTO.getNote());
        visitRepository.save(visit.get());
    }

    public List<VisitDTO> getVisits(int hospitalId) throws NoSuchElementException {

        List<Visit> entities = visitRepository.findCurrentVisitsByHospitalId(hospitalId);

        if (entities.isEmpty()) {
            throw new NoSuchElementException("No visits found.");
        }

        List<VisitDTO> result = new ArrayList<>();

        for (Visit entity : entities) {

            Optional<PatientInfo> patientInfo = patientInfoRepository.findById(entity.getId().getPatientId());
            if (patientInfo.isEmpty()) {
                throw new NoSuchElementException("Patient not found.");
            }

            result.add(new VisitDTO(
                entity.getId().getPatientId(),
                entity.getId().getAdmissionDateTime().toString(),
                patientInfo.get().getFirstName(),
                patientInfo.get().getLastName(),
                patientInfo.get().getDateOfBirth().toString(),
                entity.getVisitNote()
            ));
        }

        return result;
    }

    public List<DoctorDTO> getDoctors(int hospitalId) throws NoSuchElementException {

        List<Staff> entities = staffRepository.findByHospitalAndRole(hospitalId, Role.Doctor);

        if (entities.isEmpty()) {
            throw new NoSuchElementException("No doctors found.");
        }

        List<DoctorDTO> result = new ArrayList<>();

        for (Staff entity : entities) {
            result.add(new DoctorDTO(
                entity.getUsername(),
                entity.getFirstName(),
                entity.getLastName()
            ));
        }

        return result;
    }

    public List<ServiceDTO> getServices(int hospitalId) throws NoSuchElementException {

        List<com.syncura360.model.Service> entities = serviceRepository.findByHospitalId(hospitalId);

        if (entities.isEmpty()) {
            throw new NoSuchElementException("No doctors found.");
        }

        List<ServiceDTO> result = new ArrayList<>();

        for (com.syncura360.model.Service entity : entities) {
            result.add(new ServiceDTO(
                entity.getId().getName(),
                entity.getCategory(),
                entity.getDescription(),
                entity.getCost()
            ));
        }


        return result;
    }

    public List<DrugFetchDTO> getDrugs(int hospitalId) throws NoSuchElementException {

        List<Drug> entities = drugRepository.findAllById_HospitalId(hospitalId);

        if (entities.isEmpty()) {
            throw new NoSuchElementException("No drugs found.");
        }

        List<DrugFetchDTO> result = new ArrayList<>();

        for (Drug entity : entities) {
            result.add(new DrugFetchDTO(
                entity.getId().getNdc(),
                entity.getName(),
                entity.getCategory().toString(),
                entity.getDescription(),
                entity.getStrength(),
                entity.getPpq(),
                entity.getQuantity(),
                entity.getPrice()
            ));
        }

        return result;
    }
}
