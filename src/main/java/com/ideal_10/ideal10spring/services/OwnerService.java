package com.ideal_10.ideal10spring.services;

import com.ideal_10.ideal10spring.dtos.OwnerRequest;
import com.ideal_10.ideal10spring.dtos.OwnerResponse;
import com.ideal_10.ideal10spring.entities.Owner;
import com.ideal_10.ideal10spring.exceptions.DuplicateResourceException;
import com.ideal_10.ideal10spring.exceptions.ResourceNotFoundException;
import com.ideal_10.ideal10spring.repositories.OwnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OwnerService {

    private final OwnerRepository ownerRepository;

    @Transactional(readOnly = true)
    public List<OwnerResponse> findAll() {
        return ownerRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public OwnerResponse findById(Long id) {
        return toResponse(getEntity(id));
    }

    @Transactional
    public OwnerResponse create(OwnerRequest request) {
        validateUnique(request.identificationNumber(), null);
        Owner owner = new Owner();
        applyRequest(owner, request);
        return toResponse(ownerRepository.save(owner));
    }

    @Transactional
    public OwnerResponse update(Long id, OwnerRequest request) {
        Owner owner = getEntity(id);
        validateUnique(request.identificationNumber(), id);
        applyRequest(owner, request);
        return toResponse(owner);
    }

    @Transactional
    public void delete(Long id) {
        ownerRepository.delete(getEntity(id));
    }

    public Owner getEntity(Long id) {
        return ownerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found with id " + id));
    }

    public OwnerResponse toResponse(Owner owner) {
        return new OwnerResponse(
                owner.getId(),
                owner.getIdentificationNumber(),
                owner.getFullName(),
                owner.getPhone(),
                owner.getEmail(),
                owner.getActive()
        );
    }

    private void applyRequest(Owner owner, OwnerRequest request) {
        owner.setIdentificationNumber(request.identificationNumber());
        owner.setFullName(request.fullName());
        owner.setPhone(request.phone());
        owner.setEmail(request.email());
        owner.setActive(request.active() == null || request.active());
    }

    private void validateUnique(String identificationNumber, Long currentId) {
        ownerRepository.findByIdentificationNumber(identificationNumber)
                .filter(existing -> !existing.getId().equals(currentId))
                .ifPresent(existing -> {
                    throw new DuplicateResourceException("Owner already exists for identification number");
                });
    }
}
