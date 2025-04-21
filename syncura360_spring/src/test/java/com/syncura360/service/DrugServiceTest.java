package com.syncura360.service;

import com.syncura360.dto.Drug.*;
import com.syncura360.model.Drug;
import com.syncura360.model.DrugId;
import com.syncura360.model.enums.DrugCategory;
import com.syncura360.repository.DrugRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DrugServiceTest {

    // Create a fake repository
    @Mock
    DrugRepository drugRepository;

    // Inject fake drugRepository into drugService
    @InjectMocks
    DrugService drugService;

    @Test
    void testCreateDrug_SuccessfulSave() {
        int hospitalId = 1;

        // Simulate input DTO
        DrugFormDTO dto = mock(DrugFormDTO.class);
        when(dto.getNdc()).thenReturn(12345678901L);
        when(dto.getName()).thenReturn("Aspirin");
        when(dto.getCategory()).thenReturn("Drug");
        when(dto.getDescription()).thenReturn("Pain reliever");
        when(dto.getStrength()).thenReturn("500mg");
        when(dto.getPpq()).thenReturn(12);
        when(dto.getQuantity()).thenReturn(100);
        when(dto.getPrice()).thenReturn(new BigDecimal("12.50"));

        // Simulate drug doesn't already exist
        when(drugRepository.existsById_HospitalIdAndId_Ndc(hospitalId, 12345678901L)).thenReturn(false);

        // Call service
        drugService.createDrug(hospitalId, dto);

        // Verify save was called
        verify(drugRepository).save(any(Drug.class));
    }

    @Test
    void testCreateDrug_AlreadyExists_ThrowsException() {
        int hospitalId = 1;

        DrugFormDTO dto = mock(DrugFormDTO.class);
        when(dto.getNdc()).thenReturn(12345678901L);

        // Simulate drug already exists
        when(drugRepository.existsById_HospitalIdAndId_Ndc(hospitalId, 12345678901L)).thenReturn(true);

        // Expect exception
        assertThrows(EntityExistsException.class, () -> drugService.createDrug(hospitalId, dto));

        // Verify save is not called
        verify(drugRepository, never()).save(any());
    }

    @Test
    void testUpdateDrug_SuccessfulUpdate() {
        int hospitalId = 1;

        // Prepare mock drug and DTO
        DrugUpdateDTO dto = mock(DrugUpdateDTO.class);
        when(dto.getNdc()).thenReturn(12345678901L);
        when(dto.getQuantity()).thenReturn(200);
        when(dto.getPrice()).thenReturn(new BigDecimal("24.99"));

        Drug existingDrug = mock(Drug.class);
        when(drugRepository.findById_HospitalIdAndId_Ndc(hospitalId, 12345678901L))
                .thenReturn(Optional.of(existingDrug));

        // Call the method
        drugService.updateDrug(hospitalId, dto);

        // Verify updates and save
        verify(existingDrug).setQuantity(200);
        verify(existingDrug).setPrice(new BigDecimal("24.99"));
        verify(drugRepository).save(existingDrug);
    }

    @Test
    void testUpdateDrug_NotFound_ThrowsException() {
        int hospitalId = 1;

        DrugUpdateDTO dto = mock(DrugUpdateDTO.class);
        when(dto.getNdc()).thenReturn(12345678901L);

        when(drugRepository.findById_HospitalIdAndId_Ndc(hospitalId, 12345678901L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> drugService.updateDrug(hospitalId, dto));
    }

    @Test
    void testUpdateDrug_InvalidQuantity_ThrowsException() {
        int hospitalId = 1;

        DrugUpdateDTO dto = mock(DrugUpdateDTO.class);
        when(dto.getNdc()).thenReturn(12345678901L);
        when(dto.getQuantity()).thenReturn(-1);

        Drug existingDrug = mock(Drug.class);
        when(drugRepository.findById_HospitalIdAndId_Ndc(hospitalId, 12345678901L))
                .thenReturn(Optional.of(existingDrug));

        assertThrows(IllegalArgumentException.class, () -> drugService.updateDrug(hospitalId, dto));
    }

    @Test
    void testUpdateDrug_InvalidPrice_ThrowsException() {
        int hospitalId = 1;

        DrugUpdateDTO dto = mock(DrugUpdateDTO.class);
        when(dto.getNdc()).thenReturn(12345678901L);
        when(dto.getQuantity()).thenReturn(10);
        when(dto.getPrice()).thenReturn(new BigDecimal("1234567890.999")); // Invalid scale

        Drug existingDrug = mock(Drug.class);
        when(drugRepository.findById_HospitalIdAndId_Ndc(hospitalId, 12345678901L))
                .thenReturn(Optional.of(existingDrug));

        assertThrows(IllegalArgumentException.class, () -> drugService.updateDrug(hospitalId, dto));
    }

    @Test
    void testDeleteDrug_SuccessfulDelete() {
        int hospitalId = 1;

        Drug drug = mock(Drug.class);
        when(drug.getId()).thenReturn(new DrugId(hospitalId, 12345678901L));

        DrugDeletionDTO dto = mock(DrugDeletionDTO.class);
        when(dto.getNdc()).thenReturn(12345678901L);

        when(drugRepository.findById_HospitalIdAndId_Ndc(hospitalId, 12345678901L)).thenReturn(Optional.of(drug));

        // Call delete
        drugService.deleteDrug(hospitalId, dto);

        // Verify delete
        verify(drugRepository).deleteById(new DrugId(hospitalId, 12345678901L));
    }

    @Test
    void testDeleteDrug_NotFound_ThrowsException() {
        int hospitalId = 1;

        DrugDeletionDTO dto = mock(DrugDeletionDTO.class);
        when(dto.getNdc()).thenReturn(12345678901L);

        when(drugRepository.findById_HospitalIdAndId_Ndc(hospitalId, 12345678901L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> drugService.deleteDrug(hospitalId, dto));
    }

    @Test
    void testFetchDrugs_ReturnsList() {
        int hospitalId = 1;

        Drug drug = mock(Drug.class);
        DrugId drugId = new DrugId(hospitalId, 12345678901L);
        when(drug.getId()).thenReturn(drugId);
        when(drug.getName()).thenReturn("Aspirin");
        when(drug.getCategory()).thenReturn(DrugCategory.Drug);
        when(drug.getDescription()).thenReturn("Pain relief");
        when(drug.getStrength()).thenReturn("500mg");
        when(drug.getPpq()).thenReturn(12);
        when(drug.getQuantity()).thenReturn(100);
        when(drug.getPrice()).thenReturn(new BigDecimal("5.00"));

        when(drugRepository.findAllById_HospitalId(hospitalId)).thenReturn(List.of(drug));

        DrugFetchListDTO result = drugService.fetchDrugs(hospitalId);

        assertEquals(1, result.getDrugs().size());
        DrugFetchDTO fetched = result.getDrugs().getFirst();
        assertEquals(12345678901L, fetched.getNdc());
        assertEquals("Aspirin", fetched.getName());
        assertEquals("Drug", fetched.getCategory());
    }
}