package com.ideal_10.ideal10spring.services;

import com.ideal_10.ideal10spring.dtos.OwnerResponse;
import com.ideal_10.ideal10spring.dtos.PropertyOwnerRequest;
import com.ideal_10.ideal10spring.dtos.PropertyOwnerResponse;
import com.ideal_10.ideal10spring.entities.Owner;
import com.ideal_10.ideal10spring.entities.Property;
import com.ideal_10.ideal10spring.entities.PropertyOwner;
import com.ideal_10.ideal10spring.exceptions.DuplicateResourceException;
import com.ideal_10.ideal10spring.repositories.PropertyOwnerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PropertyOwnerServiceTest {

    @Mock
    private PropertyOwnerRepository propertyOwnerRepository;

    @Mock
    private PropertyService propertyService;

    @Mock
    private OwnerService ownerService;

    @InjectMocks
    private PropertyOwnerService propertyOwnerService;

    @Test
    void assignOwnerCreatesPropertyOwnerWhenAssignmentDoesNotExist() {
        Property property = property(7L);
        Owner owner = owner(11L);
        PropertyOwnerRequest request = new PropertyOwnerRequest(11L, new BigDecimal("60.00"));
        when(propertyOwnerRepository.existsByPropertyIdAndOwnerId(7L, 11L)).thenReturn(false);
        when(propertyService.getEntity(7L)).thenReturn(property);
        when(ownerService.getEntity(11L)).thenReturn(owner);
        when(ownerService.toResponse(owner)).thenReturn(ownerResponse(11L));
        when(propertyOwnerRepository.save(any(PropertyOwner.class))).thenAnswer(invocation -> {
            PropertyOwner propertyOwner = invocation.getArgument(0);
            propertyOwner.setId(21L);
            return propertyOwner;
        });

        PropertyOwnerResponse response = propertyOwnerService.assignOwner(7L, request);

        assertEquals(21L, response.id());
        assertEquals(7L, response.propertyId());
        assertEquals(11L, response.owner().id());
        assertEquals(new BigDecimal("60.00"), response.ownershipPercentage());
    }

    @Test
    void assignOwnerThrowsWhenOwnerIsAlreadyAssigned() {
        PropertyOwnerRequest request = new PropertyOwnerRequest(11L, new BigDecimal("60.00"));
        when(propertyOwnerRepository.existsByPropertyIdAndOwnerId(7L, 11L)).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> propertyOwnerService.assignOwner(7L, request));
        verify(propertyService, never()).getEntity(any());
        verify(ownerService, never()).getEntity(any());
        verify(propertyOwnerRepository, never()).save(any(PropertyOwner.class));
    }

    @Test
    void updateAssignmentChangesOwnerWhenNewOwnerIsAvailable() {
        Owner currentOwner = owner(11L);
        Owner newOwner = owner(12L);
        PropertyOwner propertyOwner = propertyOwner(31L, property(7L), currentOwner, new BigDecimal("40.00"));
        PropertyOwnerRequest request = new PropertyOwnerRequest(12L, new BigDecimal("100.00"));
        when(propertyOwnerRepository.findByPropertyIdAndOwnerId(7L, 11L)).thenReturn(Optional.of(propertyOwner));
        when(ownerService.getEntity(12L)).thenReturn(newOwner);
        when(propertyOwnerRepository.existsByPropertyIdAndOwnerId(7L, 12L)).thenReturn(false);
        when(ownerService.toResponse(newOwner)).thenReturn(ownerResponse(12L));

        PropertyOwnerResponse response = propertyOwnerService.updateAssignment(7L, 11L, request);

        assertEquals(12L, response.owner().id());
        assertEquals(new BigDecimal("100.00"), response.ownershipPercentage());
    }

    @Test
    void updateAssignmentThrowsWhenNewOwnerAlreadyHasAssignment() {
        Owner currentOwner = owner(11L);
        PropertyOwner propertyOwner = propertyOwner(31L, property(7L), currentOwner, new BigDecimal("40.00"));
        PropertyOwnerRequest request = new PropertyOwnerRequest(12L, new BigDecimal("100.00"));
        when(propertyOwnerRepository.findByPropertyIdAndOwnerId(7L, 11L)).thenReturn(Optional.of(propertyOwner));
        when(ownerService.getEntity(12L)).thenReturn(owner(12L));
        when(propertyOwnerRepository.existsByPropertyIdAndOwnerId(7L, 12L)).thenReturn(true);

        assertThrows(DuplicateResourceException.class,
                () -> propertyOwnerService.updateAssignment(7L, 11L, request));
    }

    @Test
    void updateAssignmentThrowsWhenAssignmentDoesNotExist() {
        PropertyOwnerRequest request = new PropertyOwnerRequest(12L, new BigDecimal("100.00"));
        when(propertyOwnerRepository.findByPropertyIdAndOwnerId(7L, 11L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> propertyOwnerService.updateAssignment(7L, 11L, request));
    }

    @Test
    void findByPropertyChecksPropertyExistsBeforeListingAssignments() {
        Property property = property(7L);
        Owner owner = owner(11L);
        when(propertyService.getEntity(7L)).thenReturn(property);
        when(propertyOwnerRepository.findByPropertyId(7L))
                .thenReturn(List.of(propertyOwner(31L, property, owner, new BigDecimal("40.00"))));
        when(ownerService.toResponse(owner)).thenReturn(ownerResponse(11L));

        List<PropertyOwnerResponse> response = propertyOwnerService.findByProperty(7L);

        assertEquals(1, response.size());
        assertEquals(31L, response.getFirst().id());
        verify(propertyService).getEntity(7L);
    }

    @Test
    void removeAssignmentDeletesExistingAssignment() {
        PropertyOwner propertyOwner = propertyOwner(31L, property(7L), owner(11L), new BigDecimal("40.00"));
        when(propertyOwnerRepository.findByPropertyIdAndOwnerId(7L, 11L)).thenReturn(Optional.of(propertyOwner));

        propertyOwnerService.removeAssignment(7L, 11L);

        verify(propertyOwnerRepository).delete(propertyOwner);
    }

    private Property property(Long id) {
        Property property = new Property();
        property.setId(id);
        return property;
    }

    private Owner owner(Long id) {
        Owner owner = new Owner();
        owner.setId(id);
        owner.setIdentificationNumber(id.equals(11L) ? "123" : "456");
        owner.setFullName(id.equals(11L) ? "Ana Perez" : "Luis Gomez");
        owner.setActive(true);
        return owner;
    }

    private OwnerResponse ownerResponse(Long id) {
        return new OwnerResponse(
                id,
                id.equals(11L) ? "123" : "456",
                id.equals(11L) ? "Ana Perez" : "Luis Gomez",
                null,
                null,
                true
        );
    }

    private PropertyOwner propertyOwner(Long id, Property property, Owner owner, BigDecimal percentage) {
        PropertyOwner propertyOwner = new PropertyOwner();
        propertyOwner.setId(id);
        propertyOwner.setProperty(property);
        propertyOwner.setOwner(owner);
        propertyOwner.setOwnershipPercentage(percentage);
        return propertyOwner;
    }
}
