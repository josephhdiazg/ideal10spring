package com.ideal_10.ideal10spring.controllers;

import com.ideal_10.ideal10spring.dtos.FiscalYearRequest;
import com.ideal_10.ideal10spring.dtos.FiscalYearResponse;
import com.ideal_10.ideal10spring.services.FiscalYearService;
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
@RequestMapping("/api/v1/fiscal-years")
public class FiscalYearController {

    private final FiscalYearService fiscalYearService;

    @GetMapping
    public List<FiscalYearResponse> findAll() {
        return fiscalYearService.findAll();
    }

    @GetMapping("/active")
    public FiscalYearResponse findActive() {
        return fiscalYearService.findActive();
    }

    @GetMapping("/{id}")
    public FiscalYearResponse findById(@PathVariable Long id) {
        return fiscalYearService.findById(id);
    }

    @PostMapping
    public ResponseEntity<FiscalYearResponse> create(@Valid @RequestBody FiscalYearRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(fiscalYearService.create(request));
    }

    @PutMapping("/{id}")
    public FiscalYearResponse update(@PathVariable Long id, @Valid @RequestBody FiscalYearRequest request) {
        return fiscalYearService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        fiscalYearService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
