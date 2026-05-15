package com.ideal_10.ideal10spring.services;

import com.ideal_10.ideal10spring.dtos.PropertyRequest;
import com.ideal_10.ideal10spring.dtos.PropertyResponse;
import com.ideal_10.ideal10spring.entities.Municipality;
import com.ideal_10.ideal10spring.entities.Property;
import com.ideal_10.ideal10spring.enums.PropertyStatus;
import com.ideal_10.ideal10spring.exceptions.DuplicateResourceException;
import com.ideal_10.ideal10spring.exceptions.ResourceNotFoundException;
import com.ideal_10.ideal10spring.repositories.PropertyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PropertyService {

    private final PropertyRepository propertyRepository;
    private final MunicipalityService municipalityService;

    @Transactional(readOnly = true)
    public List<PropertyResponse> findAll() {
        return propertyRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PropertyResponse findById(Long id) {
        return toResponse(getEntity(id));
    }

    @Transactional
    public PropertyResponse create(PropertyRequest request) {
        validateUnique(request.cadastralCode(), null);
        Property property = new Property();
        applyRequest(property, request);
        return toResponse(propertyRepository.save(property));
    }

    @Transactional
    public PropertyResponse update(Long id, PropertyRequest request) {
        Property property = getEntity(id);
        validateUnique(request.cadastralCode(), id);
        applyRequest(property, request);
        return toResponse(property);
    }

    @Transactional
    public void delete(Long id) {
        propertyRepository.delete(getEntity(id));
    }

    public Property getEntity(Long id) {
        return propertyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with id " + id));
    }

    public PropertyResponse toResponse(Property property) {
        return new PropertyResponse(
                property.getId(),
                property.getCadastralCode(),
                property.getAddress(),
                property.getPropertyUse(),
                property.getStatus(),
                property.getCadastralValue(),
                municipalityService.toResponse(property.getMunicipality())
        );
    }

    private void applyRequest(Property property, PropertyRequest request) {
        Municipality municipality = municipalityService.getEntity(request.municipalityId());
        property.setCadastralCode(request.cadastralCode());
        property.setAddress(request.address());
        property.setPropertyUse(request.propertyUse());
        property.setStatus(request.status() == null ? PropertyStatus.ACTIVE : request.status());
        property.setCadastralValue(request.cadastralValue());
        property.setMunicipality(municipality);
    }

    private void validateUnique(String cadastralCode, Long currentId) {
        propertyRepository.findByCadastralCodeIgnoreCase(cadastralCode)
                .filter(existing -> !existing.getId().equals(currentId))
                .ifPresent(existing -> {
                    throw new DuplicateResourceException("Property already exists for cadastral code");
                });
    }
}
