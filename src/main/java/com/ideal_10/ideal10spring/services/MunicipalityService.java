package com.ideal_10.ideal10spring.services;

import com.ideal_10.ideal10spring.dtos.MunicipalityRequest;
import com.ideal_10.ideal10spring.dtos.MunicipalityResponse;
import com.ideal_10.ideal10spring.entities.Municipality;
import com.ideal_10.ideal10spring.exceptions.DuplicateResourceException;
import com.ideal_10.ideal10spring.exceptions.ResourceNotFoundException;
import com.ideal_10.ideal10spring.repositories.MunicipalityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MunicipalityService {

    private final MunicipalityRepository municipalityRepository;

    @Transactional(readOnly = true)
    public List<MunicipalityResponse> findAll() {
        return municipalityRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public MunicipalityResponse findById(Long id) {
        return toResponse(getEntity(id));
    }

    @Transactional
    public MunicipalityResponse create(MunicipalityRequest request) {
        validateUnique(request.name(), request.department(), null);
        Municipality municipality = new Municipality();
        applyRequest(municipality, request);
        return toResponse(municipalityRepository.save(municipality));
    }

    @Transactional
    public MunicipalityResponse update(Long id, MunicipalityRequest request) {
        Municipality municipality = getEntity(id);
        validateUnique(request.name(), request.department(), id);
        applyRequest(municipality, request);
        return toResponse(municipality);
    }

    @Transactional
    public void delete(Long id) {
        Municipality municipality = getEntity(id);
        municipalityRepository.delete(municipality);
    }

    public Municipality getEntity(Long id) {
        return municipalityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Municipality not found with id " + id));
    }

    public MunicipalityResponse toResponse(Municipality municipality) {
        return new MunicipalityResponse(
                municipality.getId(),
                municipality.getName(),
                municipality.getDepartment(),
                municipality.getActive()
        );
    }

    private void applyRequest(Municipality municipality, MunicipalityRequest request) {
        municipality.setName(request.name());
        municipality.setDepartment(request.department());
        municipality.setActive(request.active() == null || request.active());
    }

    private void validateUnique(String name, String department, Long currentId) {
        municipalityRepository.findByNameIgnoreCaseAndDepartmentIgnoreCase(name, department)
                .filter(existing -> !existing.getId().equals(currentId))
                .ifPresent(existing -> {
                    throw new DuplicateResourceException("Municipality already exists for name and department");
                });
    }
}
