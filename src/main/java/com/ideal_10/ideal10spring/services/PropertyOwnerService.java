package com.ideal_10.ideal10spring.services;

import com.ideal_10.ideal10spring.dtos.PropertyOwnerRequest;
import com.ideal_10.ideal10spring.dtos.PropertyOwnerResponse;
import com.ideal_10.ideal10spring.entities.Owner;
import com.ideal_10.ideal10spring.entities.Property;
import com.ideal_10.ideal10spring.entities.PropertyOwner;
import com.ideal_10.ideal10spring.exceptions.DuplicateResourceException;
import com.ideal_10.ideal10spring.repositories.PropertyOwnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PropertyOwnerService {

    private final PropertyOwnerRepository propertyOwnerRepository;
    private final PropertyService propertyService;
    private final OwnerService ownerService;

    @Transactional(readOnly = true)
    public List<PropertyOwnerResponse> findByProperty(Long propertyId) {
        propertyService.getEntity(propertyId);
        return propertyOwnerRepository.findByPropertyId(propertyId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public PropertyOwnerResponse assignOwner(Long propertyId, PropertyOwnerRequest request) {
        if (propertyOwnerRepository.existsByPropertyIdAndOwnerId(propertyId, request.ownerId())) {
            throw new DuplicateResourceException("Owner is already assigned to this property");
        }
        Property property = propertyService.getEntity(propertyId);
        Owner owner = ownerService.getEntity(request.ownerId());

        PropertyOwner propertyOwner = new PropertyOwner();
        propertyOwner.setProperty(property);
        propertyOwner.setOwner(owner);
        propertyOwner.setOwnershipPercentage(request.ownershipPercentage());
        return toResponse(propertyOwnerRepository.save(propertyOwner));
    }

    @Transactional
    public PropertyOwnerResponse updateAssignment(Long propertyId, Long ownerId, PropertyOwnerRequest request) {
        PropertyOwner propertyOwner = propertyOwnerRepository.findByPropertyIdAndOwnerId(propertyId, ownerId)
                .orElseThrow(() -> new IllegalArgumentException("Owner is not assigned to this property"));
        if (!ownerId.equals(request.ownerId())) {
            Owner newOwner = ownerService.getEntity(request.ownerId());
            if (propertyOwnerRepository.existsByPropertyIdAndOwnerId(propertyId, request.ownerId())) {
                throw new DuplicateResourceException("Owner is already assigned to this property");
            }
            propertyOwner.setOwner(newOwner);
        }
        propertyOwner.setOwnershipPercentage(request.ownershipPercentage());
        return toResponse(propertyOwner);
    }

    @Transactional
    public void removeAssignment(Long propertyId, Long ownerId) {
        PropertyOwner propertyOwner = propertyOwnerRepository.findByPropertyIdAndOwnerId(propertyId, ownerId)
                .orElseThrow(() -> new IllegalArgumentException("Owner is not assigned to this property"));
        propertyOwnerRepository.delete(propertyOwner);
    }

    private PropertyOwnerResponse toResponse(PropertyOwner propertyOwner) {
        return new PropertyOwnerResponse(
                propertyOwner.getId(),
                propertyOwner.getProperty().getId(),
                ownerService.toResponse(propertyOwner.getOwner()),
                propertyOwner.getOwnershipPercentage()
        );
    }
}
