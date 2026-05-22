package com.ideal_10.ideal10spring.services;

import com.ideal_10.ideal10spring.dtos.TaxRateRequest;
import com.ideal_10.ideal10spring.dtos.TaxRateResponse;
import com.ideal_10.ideal10spring.entities.FiscalYear;
import com.ideal_10.ideal10spring.entities.TaxRate;
import com.ideal_10.ideal10spring.exceptions.DuplicateResourceException;
import com.ideal_10.ideal10spring.exceptions.ResourceNotFoundException;
import com.ideal_10.ideal10spring.mapper.TaxRateMapper;
import com.ideal_10.ideal10spring.repositories.TaxRateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaxRateService {

    private final TaxRateRepository taxRateRepository;
    private final FiscalYearService fiscalYearService;
    private final TaxRateMapper taxRateMapper;

    @Transactional(readOnly = true)
    public List<TaxRateResponse> findAll() {
        return taxRateRepository.findAll().stream()
                .map(taxRateMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public TaxRateResponse findById(Long id) {
        return taxRateMapper.toResponse(getEntity(id));
    }

    @Transactional(readOnly = true)
    public List<TaxRateResponse> findByFiscalYear(Long fiscalYearId) {
        fiscalYearService.getEntity(fiscalYearId);
        return taxRateRepository.findByFiscalYearId(fiscalYearId).stream()
                .map(taxRateMapper::toResponse)
                .toList();
    }

    @Transactional
    public TaxRateResponse create(TaxRateRequest request) {
        FiscalYear fiscalYear = fiscalYearService.getEntity(request.fiscalYearId());
        validateUniqueClassification(request, null);
        TaxRate taxRate = new TaxRate();
        applyRequest(taxRate, request, fiscalYear);
        return taxRateMapper.toResponse(taxRateRepository.save(taxRate));
    }

    @Transactional
    public TaxRateResponse update(Long id, TaxRateRequest request) {
        TaxRate taxRate = getEntity(id);
        FiscalYear fiscalYear = fiscalYearService.getEntity(request.fiscalYearId());
        validateUniqueClassification(request, id);
        applyRequest(taxRate, request, fiscalYear);
        return taxRateMapper.toResponse(taxRate);
    }

    @Transactional
    public void delete(Long id) {
        taxRateRepository.delete(getEntity(id));
    }

    public TaxRate getEntity(Long id) {
        return taxRateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tax rate not found with id " + id));
    }

    private void applyRequest(TaxRate taxRate, TaxRateRequest request, FiscalYear fiscalYear) {
        taxRate.setFiscalYear(fiscalYear);
        taxRate.setClassification(request.classification());
        taxRate.setRatePerThousand(request.ratePerThousand());
        taxRate.setActive(Boolean.TRUE.equals(request.active()) || request.active() == null);
    }

    private void validateUniqueClassification(TaxRateRequest request, Long currentId) {
        taxRateRepository.findByFiscalYearIdAndClassification(request.fiscalYearId(), request.classification())
                .filter(existing -> !existing.getId().equals(currentId))
                .ifPresent(existing -> {
                    throw new DuplicateResourceException(
                            "Tax rate already exists for this fiscal year and classification");
                });
    }
}
