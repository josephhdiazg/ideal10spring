package com.ideal_10.ideal10spring.controllers;

import com.ideal_10.ideal10spring.dtos.ChargeTypeRequest;
import com.ideal_10.ideal10spring.dtos.ChargeTypeResponse;
import com.ideal_10.ideal10spring.services.ChargeTypeService;
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
@RequestMapping("/api/v1/charge-types")
public class ChargeTypeController {

    private final ChargeTypeService chargeTypeService;

    @GetMapping
    public List<ChargeTypeResponse> findAll() {
        return chargeTypeService.findAll();
    }

    @GetMapping("/{id}")
    public ChargeTypeResponse findById(@PathVariable Long id) {
        return chargeTypeService.findById(id);
    }

    @PostMapping
    public ResponseEntity<ChargeTypeResponse> create(@Valid @RequestBody ChargeTypeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(chargeTypeService.create(request));
    }

    @PutMapping("/{id}")
    public ChargeTypeResponse update(@PathVariable Long id, @Valid @RequestBody ChargeTypeRequest request) {
        return chargeTypeService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        chargeTypeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
