package com.syncura360.service;

import com.syncura360.dto.Visit.*;
import com.syncura360.model.*;
import com.syncura360.model.enums.BedStatus;
import com.syncura360.repository.*;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VisitServiceTest {

    @Mock
    private DrugRepository drugRepository;
    @Mock
    private DrugAdministeredRepository drugAdministeredRepository;
    @Mock
    private StaffRepository staffRepository;
    @Mock
    private ServiceRepository serviceRepository;
    @Mock
    private ServiceProvidedRepository serviceProvidedRepository;
    @Mock
    private VisitRepository visitRepository;
    @Mock
    private RoomRepository roomRepository;
    @Mock
    private PatientInfoRepository patientInfoRepository;
    @Mock
    private RoomAssignmentRepository roomAssignmentRepository;
    @Mock
    private BedRepository bedRepository;
    @InjectMocks
    private VisitService visitService;

    private static final int HOSPITAL_ID = 1;
    private static final int PATIENT_ID = 100;
    private static final String ROOM_NAME = "Room A";
    private static final LocalDateTime ADMISSION_DATE_TIME = LocalDateTime.now();
    private static final String ADMISSION_DATE_TIME_STR = ADMISSION_DATE_TIME.toString();
    private static final long TEST_DRUG_NDC = 12345678901L;
    private static final String TEST_DRUG_NDC_STR = String.valueOf(TEST_DRUG_NDC);

    private AddRoomDTO createAddRoomDTO() {
        AddRoomDTO dto = new AddRoomDTO();
        dto.setPatientID(PATIENT_ID);
        dto.setRoomName(ROOM_NAME);
        dto.setVisitAdmissionDateTime(ADMISSION_DATE_TIME_STR);
        return dto;
    }

    @Test
    void createVisitPatientNotFound() {
        // Arrange
        VisitCreationDTO visitCreationDTO = new VisitCreationDTO();
        visitCreationDTO.setPatientID(PATIENT_ID);

        when(patientInfoRepository.existsById(visitCreationDTO.getPatientID())).thenReturn(false);

        // Act and Assert
        assertThrows(EntityNotFoundException.class, () -> visitService.createVisit(HOSPITAL_ID, visitCreationDTO));

        // Verify
        verify(patientInfoRepository, times(1)).existsById(visitCreationDTO.getPatientID());
        verify(visitRepository, never()).findCurrentVisitById(anyInt(), anyInt());
        verify(visitRepository, never()).save(any(Visit.class));
    }

    @Test
    void createVisitOngoingVisitExists() {
        // Arrange
        VisitCreationDTO visitCreationDTO = new VisitCreationDTO();
        visitCreationDTO.setPatientID(PATIENT_ID);
        visitCreationDTO.setReasonForVisit("Check-up");

        VisitId visitId = new VisitId(HOSPITAL_ID, PATIENT_ID);
        Visit existingVisit = new Visit(visitId, "Flu");
        Optional<Visit> optionalVisit = Optional.of(existingVisit);

        when(patientInfoRepository.existsById(PATIENT_ID)).thenReturn(true);
        when(visitRepository.findCurrentVisitById(PATIENT_ID, HOSPITAL_ID)).thenReturn(optionalVisit);

        // Act and Assert
        assertThrows(EntityExistsException.class, () -> visitService.createVisit(HOSPITAL_ID, visitCreationDTO));

        // Verify
        verify(patientInfoRepository, times(1)).existsById(PATIENT_ID);
        verify(visitRepository, times(1)).findCurrentVisitById(PATIENT_ID, HOSPITAL_ID);
        verify(visitRepository, never()).save(any(Visit.class));
    }

    @Test
    void createVisitNewVisitCreatedSuccessfully() {
        // Arrange
        String reasonForVisit = "Headache";
        VisitCreationDTO visitCreationDTO = new VisitCreationDTO();
        visitCreationDTO.setPatientID(PATIENT_ID);
        visitCreationDTO.setReasonForVisit(reasonForVisit);

        when(patientInfoRepository.existsById(PATIENT_ID)).thenReturn(true);
        when(visitRepository.findCurrentVisitById(PATIENT_ID, HOSPITAL_ID)).thenReturn(Optional.empty());

        VisitId expectedVisitId = new VisitId(HOSPITAL_ID, PATIENT_ID);
        Visit expectedVisit = new Visit(expectedVisitId, reasonForVisit);

        // Act
        visitService.createVisit(HOSPITAL_ID, visitCreationDTO);

        // Verify
        verify(patientInfoRepository, times(1)).existsById(PATIENT_ID);
        verify(visitRepository, times(1)).findCurrentVisitById(PATIENT_ID, HOSPITAL_ID);
        verify(visitRepository, times(1)).save(expectedVisit);
    }

    @Test
    void dischargeVisitNotFound() {
        // Arrange
        DischargeDTO dischargeDTO = new DischargeDTO();
        dischargeDTO.setPatientID(PATIENT_ID);

        when(visitRepository.findCurrentVisitById(dischargeDTO.getPatientID(), HOSPITAL_ID)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(EntityNotFoundException.class, () -> visitService.discharge(HOSPITAL_ID, dischargeDTO));

        // Verify
        verify(visitRepository, times(1)).findCurrentVisitById(dischargeDTO.getPatientID(), HOSPITAL_ID);
        verify(roomAssignmentRepository, never()).findCurrentAssignmentById(anyInt(), anyInt());
        verify(visitRepository, never()).save(any(Visit.class));
    }

    @Test
    void dischargeNoRoomAssignmentVisitUpdated() {
        // Arrange
        String visitSummary = "Feeling much better, discharged.";
        DischargeDTO dischargeDTO = new DischargeDTO();
        dischargeDTO.setPatientID(PATIENT_ID);
        dischargeDTO.setVisitSummary(visitSummary);

        VisitId visitId = new VisitId(HOSPITAL_ID, PATIENT_ID);
        Visit existingVisit = new Visit(visitId, "Stomach bug");
        when(visitRepository.findCurrentVisitById(PATIENT_ID, HOSPITAL_ID)).thenReturn(Optional.of(existingVisit));
        when(roomAssignmentRepository.findCurrentAssignmentById(PATIENT_ID, HOSPITAL_ID)).thenReturn(Optional.empty());

        ArgumentCaptor<Visit> visitArgumentCaptor = ArgumentCaptor.forClass(Visit.class);

        // Act
        visitService.discharge(HOSPITAL_ID, dischargeDTO);

        // Verify
        verify(visitRepository, times(1)).findCurrentVisitById(PATIENT_ID, HOSPITAL_ID);
        verify(roomAssignmentRepository, times(1)).findCurrentAssignmentById(PATIENT_ID, HOSPITAL_ID);

        verify(visitRepository, times(1)).save(visitArgumentCaptor.capture());
        assertEquals(visitSummary, visitArgumentCaptor.getValue().getVisitSummary());
        assertNotNull(visitArgumentCaptor.getValue().getDischargeDateTime());
    }

    @Test
    void dischargeWithRoomAssignmentVisitUpdated() {
        // Arrange
        String visitSummary = "Feeling much better, discharged.";
        DischargeDTO dischargeDTO = new DischargeDTO();
        dischargeDTO.setPatientID(PATIENT_ID);
        dischargeDTO.setVisitSummary(visitSummary);

        VisitId visitId = new VisitId(HOSPITAL_ID, PATIENT_ID);
        Visit existingVisit = new Visit(visitId, "Stomach bug");

        Room room = new Room(new RoomId(HOSPITAL_ID, ROOM_NAME), "Test Department");
        Bed bed = new Bed(room);
        RoomAssignment roomAssignment = new RoomAssignment(
            new RoomAssignmentId(HOSPITAL_ID, PATIENT_ID, visitId.getAdmissionDateTime()),
            false, room
        );

        when(visitRepository.findCurrentVisitById(PATIENT_ID, HOSPITAL_ID)).thenReturn(Optional.of(existingVisit));
        when(roomAssignmentRepository.findCurrentAssignmentById(PATIENT_ID, HOSPITAL_ID)).thenReturn(Optional.of(roomAssignment));
        when(roomRepository.findById(roomAssignment.getRoom().getId())).thenReturn(Optional.of(room));
        when(bedRepository.findAllByRoomAndStatus(room, BedStatus.Occupied)).thenReturn(List.of(bed));

        ArgumentCaptor<Visit> visitArgumentCaptor = ArgumentCaptor.forClass(Visit.class);
        ArgumentCaptor<RoomAssignment> roomAssignmentArgumentCaptor = ArgumentCaptor.forClass(RoomAssignment.class);
        ArgumentCaptor<Bed> bedArgumentCaptor = ArgumentCaptor.forClass(Bed.class);

        // Act
        visitService.discharge(HOSPITAL_ID, dischargeDTO);

        // Verify
        verify(visitRepository, times(2)).findCurrentVisitById(PATIENT_ID, HOSPITAL_ID);
        verify(roomAssignmentRepository, times(2)).findCurrentAssignmentById(PATIENT_ID, HOSPITAL_ID);
        verify(visitRepository, times(1)).save(visitArgumentCaptor.capture());
        verify(bedRepository, times(1)).save(bedArgumentCaptor.capture());
        verify(roomAssignmentRepository, times(1)).save(roomAssignmentArgumentCaptor.capture());

        assertEquals(visitSummary, visitArgumentCaptor.getValue().getVisitSummary());
        assertEquals(BedStatus.Vacant, bedArgumentCaptor.getValue().getStatus());
        assertEquals(true, roomAssignmentArgumentCaptor.getValue().getIsRemoved());
        assertNotNull(visitArgumentCaptor.getValue().getDischargeDateTime());
    }


    @Test
    void addRoomVisitNotFound() {
        // Arrange
        AddRoomDTO addRoomDTO = createAddRoomDTO();
        when(visitRepository.findCurrentVisitById(PATIENT_ID, HOSPITAL_ID)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(EntityNotFoundException.class, () -> visitService.addRoom(HOSPITAL_ID, addRoomDTO));

        // Verify
        verify(visitRepository, times(1)).findCurrentVisitById(PATIENT_ID, HOSPITAL_ID);
        verify(roomRepository, never()).findById(any());
        verify(roomAssignmentRepository, never()).findCurrentAssignmentById(anyInt(), anyInt());
        verify(bedRepository, never()).findAllByRoomAndStatus(any(), any());
        verify(roomAssignmentRepository, never()).save(any());
        verify(bedRepository, never()).save(any());
    }

    @Test
    void addRoomRoomNotFound() {
        // Arrange
        AddRoomDTO addRoomDTO = createAddRoomDTO();
        when(visitRepository.findCurrentVisitById(PATIENT_ID, HOSPITAL_ID)).thenReturn(Optional.of(new Visit()));
        when(roomRepository.findById(new RoomId(HOSPITAL_ID, ROOM_NAME))).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(EntityNotFoundException.class, () -> visitService.addRoom(HOSPITAL_ID, addRoomDTO));

        // Verify
        verify(visitRepository, times(1)).findCurrentVisitById(PATIENT_ID, HOSPITAL_ID);
        verify(roomRepository, times(1)).findById(new RoomId(HOSPITAL_ID, ROOM_NAME));
        verify(roomAssignmentRepository, never()).findCurrentAssignmentById(anyInt(), anyInt());
        verify(bedRepository, never()).findAllByRoomAndStatus(any(), any());
        verify(roomAssignmentRepository, never()).save(any());
        verify(bedRepository, never()).save(any());
    }

    @Test
    void addRoomPatientAlreadyAssigned() {
        // Arrange
        AddRoomDTO addRoomDTO = createAddRoomDTO();
        when(visitRepository.findCurrentVisitById(PATIENT_ID, HOSPITAL_ID)).thenReturn(Optional.of(new Visit()));
        when(roomRepository.findById(new RoomId(HOSPITAL_ID, ROOM_NAME))).thenReturn(Optional.of(new Room()));
        when(roomAssignmentRepository.findCurrentAssignmentById(PATIENT_ID, HOSPITAL_ID)).thenReturn(Optional.of(new RoomAssignment()));

        // Act and Assert
        assertThrows(EntityExistsException.class, () -> visitService.addRoom(HOSPITAL_ID, addRoomDTO));

        // Verify
        verify(visitRepository, times(1)).findCurrentVisitById(PATIENT_ID, HOSPITAL_ID);
        verify(roomRepository, times(1)).findById(new RoomId(HOSPITAL_ID, ROOM_NAME));
        verify(roomAssignmentRepository, times(1)).findCurrentAssignmentById(PATIENT_ID, HOSPITAL_ID);
        verify(bedRepository, never()).findAllByRoomAndStatus(any(), any());
        verify(roomAssignmentRepository, never()).save(any());
        verify(bedRepository, never()).save(any());
    }

    @Test
    void addRoomNoAvailableBeds() {
        // Arrange
        AddRoomDTO addRoomDTO = createAddRoomDTO();
        Room mockRoom = new Room();
        when(visitRepository.findCurrentVisitById(PATIENT_ID, HOSPITAL_ID)).thenReturn(Optional.of(new Visit()));
        when(roomRepository.findById(new RoomId(HOSPITAL_ID, ROOM_NAME))).thenReturn(Optional.of(mockRoom));
        when(roomAssignmentRepository.findCurrentAssignmentById(PATIENT_ID, HOSPITAL_ID)).thenReturn(Optional.empty());
        when(bedRepository.findAllByRoomAndStatus(mockRoom, BedStatus.Vacant)).thenReturn(List.of());

        // Act and Assert
        assertThrows(EntityNotFoundException.class, () -> visitService.addRoom(HOSPITAL_ID, addRoomDTO));

        // Verify
        verify(visitRepository, times(1)).findCurrentVisitById(PATIENT_ID, HOSPITAL_ID);
        verify(roomRepository, times(1)).findById(new RoomId(HOSPITAL_ID, ROOM_NAME));
        verify(roomAssignmentRepository, times(1)).findCurrentAssignmentById(PATIENT_ID, HOSPITAL_ID);
        verify(bedRepository, times(1)).findAllByRoomAndStatus(mockRoom, BedStatus.Vacant);
        verify(roomAssignmentRepository, never()).save(any());
        verify(bedRepository, never()).save(any());
    }

    @Test
    void addRoomSuccessfulAssignment() {
        // Arrange
        AddRoomDTO addRoomDTO = createAddRoomDTO();
        Room mockRoom = new Room(new RoomId(HOSPITAL_ID, ROOM_NAME), "Department A");
        Bed mockBed = new Bed();
        mockBed.setStatus(BedStatus.Vacant);

        when(visitRepository.findCurrentVisitById(PATIENT_ID, HOSPITAL_ID)).thenReturn(Optional.of(new Visit()));
        when(roomRepository.findById(new RoomId(HOSPITAL_ID, ROOM_NAME))).thenReturn(Optional.of(mockRoom));
        when(roomAssignmentRepository.findCurrentAssignmentById(PATIENT_ID, HOSPITAL_ID)).thenReturn(Optional.empty());
        when(bedRepository.findAllByRoomAndStatus(mockRoom, BedStatus.Vacant)).thenReturn(List.of(mockBed));

        ArgumentCaptor<RoomAssignment> roomAssignmentCaptor = ArgumentCaptor.forClass(RoomAssignment.class);
        ArgumentCaptor<Bed> bedCaptor = ArgumentCaptor.forClass(Bed.class);

        // Act
        visitService.addRoom(HOSPITAL_ID, addRoomDTO);

        // Verify
        verify(visitRepository, times(1)).findCurrentVisitById(PATIENT_ID, HOSPITAL_ID);
        verify(roomRepository, times(1)).findById(new RoomId(HOSPITAL_ID, ROOM_NAME));
        verify(roomAssignmentRepository, times(1)).findCurrentAssignmentById(PATIENT_ID, HOSPITAL_ID);
        verify(bedRepository, times(1)).findAllByRoomAndStatus(mockRoom, BedStatus.Vacant);
        verify(roomAssignmentRepository, times(1)).save(roomAssignmentCaptor.capture());

        RoomAssignment savedAssignment = roomAssignmentCaptor.getValue();
        assertEquals(HOSPITAL_ID, savedAssignment.getId().getHospitalId());
        assertEquals(PATIENT_ID, savedAssignment.getId().getPatientId());
        assertEquals(ADMISSION_DATE_TIME, savedAssignment.getId().getVisitAdmissionDateTime());
        assertFalse(savedAssignment.getIsRemoved());
        assertEquals(mockRoom, savedAssignment.getRoom());

        verify(bedRepository, times(1)).save(bedCaptor.capture());
        assertEquals(BedStatus.Occupied, bedCaptor.getValue().getStatus());
        assertEquals(mockBed, bedCaptor.getValue());
    }

    @Test
    void addRoomInvalidDateTimeFormat() {
        // Arrange
        AddRoomDTO addRoomDTO = createAddRoomDTO();
        addRoomDTO.setVisitAdmissionDateTime("invalid-date-time");
        when(visitRepository.findCurrentVisitById(PATIENT_ID, HOSPITAL_ID)).thenReturn(Optional.of(new Visit()));
        when(roomRepository.findById(new RoomId(HOSPITAL_ID, ROOM_NAME))).thenReturn(Optional.of(new Room()));
        when(roomAssignmentRepository.findCurrentAssignmentById(PATIENT_ID, HOSPITAL_ID)).thenReturn(Optional.empty());
        when(bedRepository.findAllByRoomAndStatus(any(), any())).thenReturn(List.of(new Bed())); // To avoid other exceptions

        // Act and Assert
        assertThrows(DateTimeParseException.class, () -> visitService.addRoom(HOSPITAL_ID, addRoomDTO));

        // Verify
        verify(visitRepository, times(1)).findCurrentVisitById(PATIENT_ID, HOSPITAL_ID);
        verify(roomRepository, times(1)).findById(new RoomId(HOSPITAL_ID, ROOM_NAME));
        verify(roomAssignmentRepository, times(1)).findCurrentAssignmentById(PATIENT_ID, HOSPITAL_ID);
        // Depending on the implementation, bedRepository might or might not be called before the exception
        verify(roomAssignmentRepository, never()).save(any());
        verify(bedRepository, never()).save(any());
    }

    @Test
    void addService_VisitNotFound() {
        // Arrange
        AddServiceDTO addServiceDTO = new AddServiceDTO();
        addServiceDTO.setPatientID(PATIENT_ID);
        addServiceDTO.setVisitAdmissionDateTime(ADMISSION_DATE_TIME_STR);
        addServiceDTO.setPerformedBy("testStaff");
        addServiceDTO.setService("X-Ray");

        when(visitRepository.findCurrentVisitById(PATIENT_ID, HOSPITAL_ID)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(EntityNotFoundException.class, () -> visitService.addService(HOSPITAL_ID, addServiceDTO));

        // Verify
        verify(visitRepository, times(1)).findCurrentVisitById(PATIENT_ID, HOSPITAL_ID);
        verify(staffRepository, never()).findByUsername(anyString());
        verify(serviceRepository, never()).findById(any());
        verify(serviceProvidedRepository, never()).save(any());
    }

    @Test
    void addService_StaffNotFound() {
        // Arrange
        AddServiceDTO addServiceDTO = new AddServiceDTO();
        addServiceDTO.setPatientID(PATIENT_ID);
        addServiceDTO.setVisitAdmissionDateTime(ADMISSION_DATE_TIME_STR);
        addServiceDTO.setPerformedBy("nonExistentStaff");
        addServiceDTO.setService("X-Ray");

        when(visitRepository.findCurrentVisitById(PATIENT_ID, HOSPITAL_ID)).thenReturn(Optional.of(new Visit()));
        when(staffRepository.findByUsername("nonExistentStaff")).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(EntityNotFoundException.class, () -> visitService.addService(HOSPITAL_ID, addServiceDTO));

        // Verify
        verify(visitRepository, times(1)).findCurrentVisitById(PATIENT_ID, HOSPITAL_ID);
        verify(staffRepository, times(1)).findByUsername("nonExistentStaff");
        verify(serviceRepository, never()).findById(any());
        verify(serviceProvidedRepository, never()).save(any());
    }

    @Test
    void addService_StaffWrongHospital() {
        // Arrange
        AddServiceDTO addServiceDTO = new AddServiceDTO();
        addServiceDTO.setPatientID(PATIENT_ID);
        addServiceDTO.setVisitAdmissionDateTime(ADMISSION_DATE_TIME_STR);
        addServiceDTO.setPerformedBy("wrongHospitalStaff");
        addServiceDTO.setService("X-Ray");

        Staff staffWrongHospital = new Staff();
        Hospital otherHospital = new Hospital();
        otherHospital.setId(99);
        staffWrongHospital.setWorksAt(otherHospital);

        when(visitRepository.findCurrentVisitById(PATIENT_ID, HOSPITAL_ID)).thenReturn(Optional.of(new Visit()));
        when(staffRepository.findByUsername("wrongHospitalStaff")).thenReturn(Optional.of(staffWrongHospital));

        // Act and Assert
        assertThrows(EntityNotFoundException.class, () -> visitService.addService(HOSPITAL_ID, addServiceDTO));

        // Verify
        verify(visitRepository, times(1)).findCurrentVisitById(PATIENT_ID, HOSPITAL_ID);
        verify(staffRepository, times(1)).findByUsername("wrongHospitalStaff");
        verify(serviceRepository, never()).findById(any());
        verify(serviceProvidedRepository, never()).save(any());
    }

    @Test
    void addService_ServiceNotFound() {
        // Arrange
        AddServiceDTO addServiceDTO = new AddServiceDTO();
        addServiceDTO.setPatientID(PATIENT_ID);
        addServiceDTO.setVisitAdmissionDateTime(ADMISSION_DATE_TIME_STR);
        addServiceDTO.setPerformedBy("testStaff");
        addServiceDTO.setService("NonExistentService");

        Staff staff = new Staff();
        Hospital currentHospital = new Hospital();
        currentHospital.setId(HOSPITAL_ID);
        staff.setWorksAt(currentHospital);

        when(visitRepository.findCurrentVisitById(PATIENT_ID, HOSPITAL_ID)).thenReturn(Optional.of(new Visit()));
        when(staffRepository.findByUsername("testStaff")).thenReturn(Optional.of(staff));
        when(serviceRepository.findById(new ServiceId(HOSPITAL_ID, "NonExistentService"))).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(EntityNotFoundException.class, () -> visitService.addService(HOSPITAL_ID, addServiceDTO));

        // Verify
        verify(visitRepository, times(1)).findCurrentVisitById(PATIENT_ID, HOSPITAL_ID);
        verify(staffRepository, times(1)).findByUsername("testStaff");
        verify(serviceRepository, times(1)).findById(new ServiceId(HOSPITAL_ID, "NonExistentService"));
        verify(serviceProvidedRepository, never()).save(any());
    }

    @Test
    void addService_ServiceWrongHospital() {
        // Arrange
        AddServiceDTO addServiceDTO = new AddServiceDTO();
        addServiceDTO.setPatientID(PATIENT_ID);
        addServiceDTO.setVisitAdmissionDateTime(ADMISSION_DATE_TIME_STR);
        addServiceDTO.setPerformedBy("testStaff");
        addServiceDTO.setService("WrongHospitalService");

        Staff staff = new Staff();
        Hospital currentHospital = new Hospital();
        currentHospital.setId(HOSPITAL_ID);
        staff.setWorksAt(currentHospital);

        com.syncura360.model.Service serviceWrongHospital = new com.syncura360.model.Service();
        ServiceId otherServiceId = new ServiceId(99, "WrongHospitalService");
        serviceWrongHospital.setId(otherServiceId);

        when(visitRepository.findCurrentVisitById(PATIENT_ID, HOSPITAL_ID)).thenReturn(Optional.of(new Visit()));
        when(staffRepository.findByUsername("testStaff")).thenReturn(Optional.of(staff));
        when(serviceRepository.findById(new ServiceId(HOSPITAL_ID, "WrongHospitalService"))).thenReturn(Optional.of(serviceWrongHospital));

        // Act and Assert
        assertThrows(EntityNotFoundException.class, () -> visitService.addService(HOSPITAL_ID, addServiceDTO));

        // Verify
        verify(visitRepository, times(1)).findCurrentVisitById(PATIENT_ID, HOSPITAL_ID);
        verify(staffRepository, times(1)).findByUsername("testStaff");
        verify(serviceRepository, times(1)).findById(new ServiceId(HOSPITAL_ID, "WrongHospitalService"));
        verify(serviceProvidedRepository, never()).save(any());
    }

    @Test
    void addService_Successful() {
        // Arrange
        AddServiceDTO addServiceDTO = new AddServiceDTO();
        addServiceDTO.setPatientID(PATIENT_ID);
        addServiceDTO.setVisitAdmissionDateTime(ADMISSION_DATE_TIME_STR);
        addServiceDTO.setPerformedBy("testStaff");
        addServiceDTO.setService("X-Ray");

        Staff staff = new Staff();
        Hospital currentHospital = new Hospital();
        currentHospital.setId(HOSPITAL_ID);
        staff.setWorksAt(currentHospital);

        com.syncura360.model.Service service = new com.syncura360.model.Service();
        ServiceId serviceId = new ServiceId(HOSPITAL_ID, "X-Ray");
        service.setId(serviceId);

        when(visitRepository.findCurrentVisitById(PATIENT_ID, HOSPITAL_ID)).thenReturn(Optional.of(new Visit()));
        when(staffRepository.findByUsername("testStaff")).thenReturn(Optional.of(staff));
        when(serviceRepository.findById(serviceId)).thenReturn(Optional.of(service));

        ArgumentCaptor<ServiceProvided> serviceProvidedCaptor = ArgumentCaptor.forClass(ServiceProvided.class);

        // Act
        visitService.addService(HOSPITAL_ID, addServiceDTO);

        // Verify
        verify(visitRepository, times(1)).findCurrentVisitById(PATIENT_ID, HOSPITAL_ID);
        verify(staffRepository, times(1)).findByUsername("testStaff");
        verify(serviceRepository, times(1)).findById(serviceId);
        verify(serviceProvidedRepository, times(1)).save(serviceProvidedCaptor.capture());

        ServiceProvided savedServiceProvided = serviceProvidedCaptor.getValue();
        assertEquals(HOSPITAL_ID, savedServiceProvided.getId().getHospitalId());
        assertEquals(PATIENT_ID, savedServiceProvided.getId().getPatientId());
        assertEquals(ADMISSION_DATE_TIME, savedServiceProvided.getId().getVisitAdmissionDateTime());
        assertEquals(staff, savedServiceProvided.getPerformedBy());
        assertEquals(service, savedServiceProvided.getService());
    }

    @Test
    void addService_InvalidDateTimeFormat() {
        // Arrange
        AddServiceDTO addServiceDTO = new AddServiceDTO();
        addServiceDTO.setPatientID(PATIENT_ID);
        addServiceDTO.setVisitAdmissionDateTime("invalid-date-time");
        addServiceDTO.setPerformedBy("testStaff");
        addServiceDTO.setService("X-Ray");

        when(visitRepository.findCurrentVisitById(PATIENT_ID, HOSPITAL_ID)).thenReturn(Optional.of(new Visit()));

        // Act and Assert
        assertThrows(DateTimeParseException.class, () -> visitService.addService(HOSPITAL_ID, addServiceDTO));

        // Verify
        verify(visitRepository, times(1)).findCurrentVisitById(PATIENT_ID, HOSPITAL_ID);
        verify(staffRepository, never()).findByUsername(anyString());
        verify(serviceRepository, never()).findById(any());
        verify(serviceProvidedRepository, never()).save(any());
    }

    @Test
    void addDrug_DrugNotFound() {
        // Arrange
        AddDrugDTO addDrugDTO = new AddDrugDTO();
        addDrugDTO.setPatientID(PATIENT_ID);
        addDrugDTO.setVisitAdmissionDateTime(ADMISSION_DATE_TIME_STR);
        addDrugDTO.setAdministeredBy("testStaff");
        addDrugDTO.setDrug(TEST_DRUG_NDC);
        addDrugDTO.setQuantity(2);

        Staff staff = new Staff();
        Hospital currentHospital = new Hospital();
        currentHospital.setId(HOSPITAL_ID);
        staff.setWorksAt(currentHospital);

        when(visitRepository.findCurrentVisitById(PATIENT_ID, HOSPITAL_ID)).thenReturn(Optional.of(new Visit()));
        when(staffRepository.findByUsername("testStaff")).thenReturn(Optional.of(staff));
        when(drugRepository.findById(new DrugId(HOSPITAL_ID, TEST_DRUG_NDC))).thenReturn(Optional.empty()); // Use the Long NDC

        // Act and Assert
        assertThrows(EntityNotFoundException.class, () -> visitService.addDrug(HOSPITAL_ID, addDrugDTO));

        // Verify
        verify(visitRepository, times(1)).findCurrentVisitById(PATIENT_ID, HOSPITAL_ID);
        verify(staffRepository, times(1)).findByUsername("testStaff");
        verify(drugRepository, times(1)).findById(new DrugId(HOSPITAL_ID, TEST_DRUG_NDC));
        verify(drugAdministeredRepository, never()).save(any());
        verify(drugRepository, never()).save(any()); // For inventory update
    }

    @Test
    void addDrug_DrugNotInInventory() {
        // Arrange
        AddDrugDTO addDrugDTO = new AddDrugDTO();
        addDrugDTO.setPatientID(PATIENT_ID);
        addDrugDTO.setVisitAdmissionDateTime(ADMISSION_DATE_TIME_STR);
        addDrugDTO.setAdministeredBy("testStaff");
        addDrugDTO.setDrug(TEST_DRUG_NDC);
        addDrugDTO.setQuantity(2);

        Staff staff = new Staff();
        Hospital currentHospital = new Hospital();
        currentHospital.setId(HOSPITAL_ID);
        staff.setWorksAt(currentHospital);

        Drug lowStockDrug = new Drug();
        lowStockDrug.setQuantity(0);
        DrugId drugId = new DrugId(HOSPITAL_ID, TEST_DRUG_NDC);

        when(visitRepository.findCurrentVisitById(PATIENT_ID, HOSPITAL_ID)).thenReturn(Optional.of(new Visit()));
        when(staffRepository.findByUsername("testStaff")).thenReturn(Optional.of(staff));
        when(drugRepository.findById(drugId)).thenReturn(Optional.of(lowStockDrug));

        // Act and Assert
        assertThrows(EntityNotFoundException.class, () -> visitService.addDrug(HOSPITAL_ID, addDrugDTO));

        // Verify
        verify(visitRepository, times(1)).findCurrentVisitById(PATIENT_ID, HOSPITAL_ID);
        verify(staffRepository, times(1)).findByUsername("testStaff");
        verify(drugRepository, times(1)).findById(drugId);
        verify(drugAdministeredRepository, never()).save(any());
        verify(drugRepository, never()).save(any()); // For inventory update
    }

    @Test
    void addDrug_Successful() {
        // Arrange
        AddDrugDTO addDrugDTO = new AddDrugDTO();
        addDrugDTO.setPatientID(PATIENT_ID);
        addDrugDTO.setVisitAdmissionDateTime(ADMISSION_DATE_TIME_STR);
        addDrugDTO.setAdministeredBy("testStaff");
        addDrugDTO.setDrug(TEST_DRUG_NDC);
        addDrugDTO.setQuantity(2);

        Staff staff = new Staff();
        Hospital currentHospital = new Hospital();
        currentHospital.setId(HOSPITAL_ID);
        staff.setWorksAt(currentHospital);

        DrugId drugId = new DrugId(HOSPITAL_ID, TEST_DRUG_NDC);
        Drug sufficientStockDrug = new Drug();
        sufficientStockDrug.setQuantity(10);
        sufficientStockDrug.setId(drugId);

        when(visitRepository.findCurrentVisitById(PATIENT_ID, HOSPITAL_ID)).thenReturn(Optional.of(new Visit()));
        when(staffRepository.findByUsername("testStaff")).thenReturn(Optional.of(staff));
        when(drugRepository.findById(drugId)).thenReturn(Optional.of(sufficientStockDrug));

        ArgumentCaptor<DrugAdministered> drugAdministeredCaptor = ArgumentCaptor.forClass(DrugAdministered.class);
        ArgumentCaptor<Drug> drugCaptor = ArgumentCaptor.forClass(Drug.class);

        // Act
        visitService.addDrug(HOSPITAL_ID, addDrugDTO);

        // Verify
        verify(visitRepository, times(1)).findCurrentVisitById(PATIENT_ID, HOSPITAL_ID);
        verify(staffRepository, times(1)).findByUsername("testStaff");
        verify(drugRepository, times(1)).findById(drugId);
        verify(drugAdministeredRepository, times(1)).save(drugAdministeredCaptor.capture());

        assertEquals(TEST_DRUG_NDC, drugAdministeredCaptor.getValue().getDrug().getId().getNdc());
        assertEquals(2, drugAdministeredCaptor.getValue().getQuantity());

        verify(drugRepository, times(1)).save(drugCaptor.capture());
        assertEquals(8, drugCaptor.getValue().getQuantity());
    }


    @Test
    void addDrug_Successful_DefaultQuantity() {
        // Arrange
        AddDrugDTO addDrugDTO = new AddDrugDTO();
        addDrugDTO.setPatientID(PATIENT_ID);
        addDrugDTO.setVisitAdmissionDateTime(ADMISSION_DATE_TIME_STR);
        addDrugDTO.setAdministeredBy("testStaff");
        addDrugDTO.setDrug(TEST_DRUG_NDC);
        addDrugDTO.setQuantity(0); // Intentionally set to 0 to test default

        Staff staff = new Staff();
        Hospital currentHospital = new Hospital();
        currentHospital.setId(HOSPITAL_ID);
        staff.setWorksAt(currentHospital);

        Drug sufficientStockDrug = new Drug();
        sufficientStockDrug.setQuantity(5);
        DrugId drugId = new DrugId(HOSPITAL_ID, TEST_DRUG_NDC);
        sufficientStockDrug.setId(drugId);

        when(visitRepository.findCurrentVisitById(PATIENT_ID, HOSPITAL_ID)).thenReturn(Optional.of(new Visit()));
        when(staffRepository.findByUsername("testStaff")).thenReturn(Optional.of(staff));
        when(drugRepository.findById(drugId)).thenReturn(Optional.of(sufficientStockDrug));

        ArgumentCaptor<DrugAdministered> drugAdministeredCaptor = ArgumentCaptor.forClass(DrugAdministered.class);
        ArgumentCaptor<Drug> drugCaptor = ArgumentCaptor.forClass(Drug.class);

        // Act
        visitService.addDrug(HOSPITAL_ID, addDrugDTO);

        // Verify
        verify(visitRepository, times(1)).findCurrentVisitById(PATIENT_ID, HOSPITAL_ID);
        verify(staffRepository, times(1)).findByUsername("testStaff");
        verify(drugRepository, times(1)).findById(drugId);
        verify(drugAdministeredRepository, times(1)).save(drugAdministeredCaptor.capture());

        assertEquals(1, drugAdministeredCaptor.getValue().getQuantity()); // Verify default quantity
        assertEquals(TEST_DRUG_NDC, drugAdministeredCaptor.getValue().getDrug().getId().getNdc());

        verify(drugRepository, times(1)).save(drugCaptor.capture());
        assertEquals(4, drugCaptor.getValue().getQuantity());
    }

    @Test
    void addDrug_InvalidDateTimeFormat() {
        // Arrange
        AddDrugDTO addDrugDTO = new AddDrugDTO();
        addDrugDTO.setPatientID(PATIENT_ID);
        addDrugDTO.setVisitAdmissionDateTime("invalid-date-time");
        addDrugDTO.setAdministeredBy("testStaff");
        addDrugDTO.setDrug(TEST_DRUG_NDC);
        addDrugDTO.setQuantity(1);

        when(visitRepository.findCurrentVisitById(PATIENT_ID, HOSPITAL_ID)).thenReturn(Optional.of(new Visit()));

        // Act and Assert
        assertThrows(DateTimeParseException.class, () -> visitService.addDrug(HOSPITAL_ID, addDrugDTO));

        // Verify
        verify(visitRepository, times(1)).findCurrentVisitById(PATIENT_ID, HOSPITAL_ID);
        verify(staffRepository, never()).findByUsername(anyString());
        verify(drugRepository, never()).findById(any());
        verify(drugAdministeredRepository, never()).save(any());
        verify(drugRepository, never()).save(any()); // For inventory update
    }

}