package com.ideal_10.ideal10spring.controllers;

import com.ideal_10.ideal10spring.dtos.TaxBenefitRequest;
import com.ideal_10.ideal10spring.dtos.TaxBenefitResponse;
import com.ideal_10.ideal10spring.enums.PropertyClassification;
import com.ideal_10.ideal10spring.services.TaxBenefitService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tax-benefits")
public class TaxBenefitController {

    private final TaxBenefitService taxBenefitService;

    @GetMapping
    public List<TaxBenefitResponse> findAll() {
        return taxBenefitService.findAll();
    }

    @GetMapping("/{id}")
    public TaxBenefitResponse findById(@PathVariable Long id) {
        return taxBenefitService.findById(id);
    }

    @GetMapping("/by-classification")
    public List<TaxBenefitResponse> findActiveByClassification(
            @RequestParam PropertyClassification classification) {
        return taxBenefitService.findActiveByClassification(classification);
    }

    @PostMapping
    public ResponseEntity<TaxBenefitResponse> create(@Valid @RequestBody TaxBenefitRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(taxBenefitService.create(request));
    }

    @PutMapping("/{id}")
    public TaxBenefitResponse update(@PathVariable Long id, @Valid @RequestBody TaxBenefitRequest request) {
        return taxBenefitService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        taxBenefitService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
