package com.ideal_10.ideal10spring.controllers;

import com.ideal_10.ideal10spring.dtos.MunicipalityRequest;
import com.ideal_10.ideal10spring.dtos.MunicipalityResponse;
import com.ideal_10.ideal10spring.services.MunicipalityService;
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
@RequestMapping("/api/v1/municipalities")
public class MunicipalityController {

    private final MunicipalityService municipalityService;

    @GetMapping
    public List<MunicipalityResponse> findAll() {
        return municipalityService.findAll();
    }

    @GetMapping("/{id}")
    public MunicipalityResponse findById(@PathVariable Long id) {
        return municipalityService.findById(id);
    }

    @PostMapping
    public ResponseEntity<MunicipalityResponse> create(@Valid @RequestBody MunicipalityRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(municipalityService.create(request));
    }

    @PutMapping("/{id}")
    public MunicipalityResponse update(@PathVariable Long id, @Valid @RequestBody MunicipalityRequest request) {
        return municipalityService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        municipalityService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
