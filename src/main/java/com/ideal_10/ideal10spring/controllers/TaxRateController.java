package com.ideal_10.ideal10spring.controllers;

import com.ideal_10.ideal10spring.dtos.TaxRateRequest;
import com.ideal_10.ideal10spring.dtos.TaxRateResponse;
import com.ideal_10.ideal10spring.services.TaxRateService;
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
@RequestMapping("/api/v1/tax-rates")
public class TaxRateController {

    private final TaxRateService taxRateService;

    @GetMapping
    public List<TaxRateResponse> findAll() {
        return taxRateService.findAll();
    }

    @GetMapping("/{id}")
    public TaxRateResponse findById(@PathVariable Long id) {
        return taxRateService.findById(id);
    }

    @GetMapping("/fiscal-year/{fiscalYearId}")
    public List<TaxRateResponse> findByFiscalYear(@PathVariable Long fiscalYearId) {
        return taxRateService.findByFiscalYear(fiscalYearId);
    }

    @PostMapping
    public ResponseEntity<TaxRateResponse> create(@Valid @RequestBody TaxRateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(taxRateService.create(request));
    }

    @PutMapping("/{id}")
    public TaxRateResponse update(@PathVariable Long id, @Valid @RequestBody TaxRateRequest request) {
        return taxRateService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        taxRateService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
