package com.ideal_10.ideal10spring.services;

import com.ideal_10.ideal10spring.dtos.OwnerRequest;
import com.ideal_10.ideal10spring.dtos.OwnerResponse;
import com.ideal_10.ideal10spring.entities.Owner;
import com.ideal_10.ideal10spring.exceptions.DuplicateResourceException;
import com.ideal_10.ideal10spring.exceptions.ResourceNotFoundException;
import com.ideal_10.ideal10spring.repositories.OwnerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OwnerServiceTest {

    @Mock
    private OwnerRepository ownerRepository;

    @InjectMocks
    private OwnerService ownerService;

    @Test
    void createPersistsOwnerWithRequestedActiveValue() {
        OwnerRequest request = new OwnerRequest("123", "Ana Perez", "3001234567", "ana@example.com", false);
        when(ownerRepository.findByIdentificationNumber("123")).thenReturn(Optional.empty());
        when(ownerRepository.save(any(Owner.class))).thenAnswer(invocation -> {
            Owner owner = invocation.getArgument(0);
            owner.setId(5L);
            return owner;
        });

        OwnerResponse response = ownerService.create(request);

        assertEquals(5L, response.id());
        assertEquals("123", response.identificationNumber());
        assertEquals("Ana Perez", response.fullName());
        assertFalse(response.active());
    }

    @Test
    void createDefaultsActiveToTrueWhenRequestActiveIsNull() {
        OwnerRequest request = new OwnerRequest("123", "Ana Perez", null, null, null);
        when(ownerRepository.findByIdentificationNumber("123")).thenReturn(Optional.empty());
        when(ownerRepository.save(any(Owner.class))).thenAnswer(invocation -> {
            Owner owner = invocation.getArgument(0);
            owner.setId(5L);
            return owner;
        });

        OwnerResponse response = ownerService.create(request);

        assertTrue(response.active());
    }

    @Test
    void createThrowsWhenIdentificationNumberAlreadyExists() {
        Owner existing = owner(1L, "123", "Existing Owner");
        OwnerRequest request = new OwnerRequest("123", "Ana Perez", null, null, true);
        when(ownerRepository.findByIdentificationNumber("123")).thenReturn(Optional.of(existing));

        assertThrows(DuplicateResourceException.class, () -> ownerService.create(request));
        verify(ownerRepository, never()).save(any(Owner.class));
    }

    @Test
    void updateAllowsSameOwnerToKeepIdentificationNumber() {
        Owner existing = owner(1L, "123", "Ana Perez");
        OwnerRequest request = new OwnerRequest("123", "Ana Maria Perez", "300", "ana@example.com", true);
        when(ownerRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(ownerRepository.findByIdentificationNumber("123")).thenReturn(Optional.of(existing));

        OwnerResponse response = ownerService.update(1L, request);

        assertEquals(1L, response.id());
        assertEquals("Ana Maria Perez", response.fullName());
        assertEquals("300", response.phone());
        assertEquals("ana@example.com", response.email());
    }

    @Test
    void findAllMapsOwnersToResponses() {
        when(ownerRepository.findAll()).thenReturn(List.of(owner(1L, "123", "Ana Perez")));

        List<OwnerResponse> response = ownerService.findAll();

        assertEquals(1, response.size());
        assertEquals("123", response.getFirst().identificationNumber());
    }

    @Test
    void getEntityThrowsWhenOwnerDoesNotExist() {
        when(ownerRepository.findById(42L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> ownerService.getEntity(42L));
    }

    private Owner owner(Long id, String identificationNumber, String fullName) {
        Owner owner = new Owner();
        owner.setId(id);
        owner.setIdentificationNumber(identificationNumber);
        owner.setFullName(fullName);
        owner.setActive(true);
        return owner;
    }
}
