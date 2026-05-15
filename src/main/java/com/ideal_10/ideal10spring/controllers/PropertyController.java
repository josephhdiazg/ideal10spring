package com.ideal_10.ideal10spring.controllers;

import com.ideal_10.ideal10spring.dtos.PropertyOwnerRequest;
import com.ideal_10.ideal10spring.dtos.PropertyOwnerResponse;
import com.ideal_10.ideal10spring.dtos.PropertyRequest;
import com.ideal_10.ideal10spring.dtos.PropertyResponse;
import com.ideal_10.ideal10spring.services.PropertyOwnerService;
import com.ideal_10.ideal10spring.services.PropertyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/properties")
public class PropertyController {

    private final PropertyService propertyService;
    private final PropertyOwnerService propertyOwnerService;

    @GetMapping
    public List<PropertyResponse> findAll() {
        return propertyService.findAll();
    }

    @GetMapping("/{id}")
    public PropertyResponse findById(@PathVariable Long id) {
        return propertyService.findById(id);
    }

    @PostMapping
    public ResponseEntity<PropertyResponse> create(@Valid @RequestBody PropertyRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(propertyService.create(request));
    }

    @PutMapping("/{id}")
    public PropertyResponse update(@PathVariable Long id, @Valid @RequestBody PropertyRequest request) {
        return propertyService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        propertyService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{propertyId}/owners")
    public List<PropertyOwnerResponse> findOwners(@PathVariable Long propertyId) {
        return propertyOwnerService.findByProperty(propertyId);
    }

    @PostMapping("/{propertyId}/owners")
    public ResponseEntity<PropertyOwnerResponse> assignOwner(
            @PathVariable Long propertyId,
            @Valid @RequestBody PropertyOwnerRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(propertyOwnerService.assignOwner(propertyId, request));
    }

    @PutMapping("/{propertyId}/owners/{ownerId}")
    public PropertyOwnerResponse updateOwnerAssignment(
            @PathVariable Long propertyId,
            @PathVariable Long ownerId,
            @Valid @RequestBody PropertyOwnerRequest request
    ) {
        return propertyOwnerService.updateAssignment(propertyId, ownerId, request);
    }

    @DeleteMapping("/{propertyId}/owners/{ownerId}")
    public ResponseEntity<Void> removeOwnerAssignment(@PathVariable Long propertyId, @PathVariable Long ownerId) {
        propertyOwnerService.removeAssignment(propertyId, ownerId);
        return ResponseEntity.noContent().build();
    }
}
