package com.ideal_10.ideal10spring.services;

import com.ideal_10.ideal10spring.dtos.TaxBenefitRequest;
import com.ideal_10.ideal10spring.dtos.TaxBenefitResponse;
import com.ideal_10.ideal10spring.entities.TaxBenefit;
import com.ideal_10.ideal10spring.enums.PropertyClassification;
import com.ideal_10.ideal10spring.exceptions.DuplicateResourceException;
import com.ideal_10.ideal10spring.exceptions.ResourceNotFoundException;
import com.ideal_10.ideal10spring.mapper.TaxBenefitMapper;
import com.ideal_10.ideal10spring.repositories.TaxBenefitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaxBenefitService {

    private final TaxBenefitRepository taxBenefitRepository;
    private final TaxBenefitMapper taxBenefitMapper;

    @Transactional(readOnly = true)
    public List<TaxBenefitResponse> findAll() {
        return taxBenefitRepository.findAll().stream()
                .map(taxBenefitMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public TaxBenefitResponse findById(Long id) {
        return taxBenefitMapper.toResponse(getEntity(id));
    }

    @Transactional(readOnly = true)
    public List<TaxBenefitResponse> findActiveByClassification(PropertyClassification classification) {
        return taxBenefitRepository.findActiveByClassification(classification).stream()
                .map(taxBenefitMapper::toResponse)
                .toList();
    }

    @Transactional
    public TaxBenefitResponse create(TaxBenefitRequest request) {
        validateUniqueCode(request.code(), null);
        TaxBenefit benefit = new TaxBenefit();
        applyRequest(benefit, request);
        return taxBenefitMapper.toResponse(taxBenefitRepository.save(benefit));
    }

    @Transactional
    public TaxBenefitResponse update(Long id, TaxBenefitRequest request) {
        TaxBenefit benefit = getEntity(id);
        validateUniqueCode(request.code(), id);
        applyRequest(benefit, request);
        return taxBenefitMapper.toResponse(benefit);
    }

    @Transactional
    public void delete(Long id) {
        taxBenefitRepository.delete(getEntity(id));
    }

    public TaxBenefit getEntity(Long id) {
        return taxBenefitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tax benefit not found with id " + id));
    }

    private void applyRequest(TaxBenefit benefit, TaxBenefitRequest request) {
        benefit.setCode(request.code());
        benefit.setName(request.name());
        benefit.setDescription(request.description());
        benefit.setDiscountPercentage(request.discountPercentage());
        benefit.setApplicableClassification(request.applicableClassification());
        benefit.setActive(Boolean.TRUE.equals(request.active()) || request.active() == null);
    }

    private void validateUniqueCode(String code, Long currentId) {
        taxBenefitRepository.findByCode(code)
                .filter(existing -> !existing.getId().equals(currentId))
                .ifPresent(existing -> {
                    throw new DuplicateResourceException("Tax benefit already exists with code " + code);
                });
    }
}
