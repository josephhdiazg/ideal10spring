package com.ideal_10.ideal10spring.services;

import com.ideal_10.ideal10spring.dtos.FiscalYearRequest;
import com.ideal_10.ideal10spring.dtos.FiscalYearResponse;
import com.ideal_10.ideal10spring.entities.FiscalYear;
import com.ideal_10.ideal10spring.exceptions.DuplicateResourceException;
import com.ideal_10.ideal10spring.exceptions.ResourceNotFoundException;
import com.ideal_10.ideal10spring.mapper.FiscalYearMapper;
import com.ideal_10.ideal10spring.repositories.FiscalYearRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FiscalYearService {

    private final FiscalYearRepository fiscalYearRepository;
    private final FiscalYearMapper fiscalYearMapper;

    @Transactional(readOnly = true)
    public List<FiscalYearResponse> findAll() {
        return fiscalYearRepository.findAll().stream()
                .map(fiscalYearMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public FiscalYearResponse findById(Long id) {
        return fiscalYearMapper.toResponse(getEntity(id));
    }

    @Transactional
    public FiscalYearResponse create(FiscalYearRequest request) {
        validateUniqueYear(request.year(), null);
        FiscalYear fiscalYear = new FiscalYear();
        applyRequest(fiscalYear, request);
        return fiscalYearMapper.toResponse(fiscalYearRepository.save(fiscalYear));
    }

    @Transactional
    public FiscalYearResponse update(Long id, FiscalYearRequest request) {
        FiscalYear fiscalYear = getEntity(id);
        validateUniqueYear(request.year(), id);
        applyRequest(fiscalYear, request);
        return fiscalYearMapper.toResponse(fiscalYear);
    }

    @Transactional
    public void delete(Long id) {
        fiscalYearRepository.delete(getEntity(id));
    }

    @Transactional(readOnly = true)
    public FiscalYearResponse findActive() {
        return fiscalYearRepository.findByActiveTrue()
                .map(fiscalYearMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("No active fiscal year found"));
    }

    public FiscalYear getEntity(Long id) {
        return fiscalYearRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fiscal year not found with id " + id));
    }

    public FiscalYear getActiveEntity() {
        return fiscalYearRepository.findByActiveTrue()
                .orElseThrow(() -> new ResourceNotFoundException("No active fiscal year found"));
    }

    private void applyRequest(FiscalYear fiscalYear, FiscalYearRequest request) {
        if (Boolean.TRUE.equals(request.active())) {
            fiscalYearRepository.findByActiveTrue()
                    .filter(existing -> !existing.getId().equals(fiscalYear.getId()))
                    .ifPresent(existing -> {
                        existing.setActive(false);
                        fiscalYearRepository.save(existing);
                    });
        }
        fiscalYear.setYear(request.year());
        fiscalYear.setDescription(request.description());
        fiscalYear.setStartDate(request.startDate());
        fiscalYear.setEndDate(request.endDate());
        fiscalYear.setActive(Boolean.TRUE.equals(request.active()));
    }

    private void validateUniqueYear(Integer year, Long currentId) {
        fiscalYearRepository.findByYear(year)
                .filter(existing -> !existing.getId().equals(currentId))
                .ifPresent(existing -> {
                    throw new DuplicateResourceException("Fiscal year already exists for year " + year);
                });
    }
}
