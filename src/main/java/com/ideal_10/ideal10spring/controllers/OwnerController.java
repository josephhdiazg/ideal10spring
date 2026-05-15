package com.ideal_10.ideal10spring.controllers;

import com.ideal_10.ideal10spring.dtos.OwnerRequest;
import com.ideal_10.ideal10spring.dtos.OwnerResponse;
import com.ideal_10.ideal10spring.services.OwnerService;
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
@RequestMapping("/api/v1/owners")
public class OwnerController {

    private final OwnerService ownerService;

    @GetMapping
    public List<OwnerResponse> findAll() {
        return ownerService.findAll();
    }

    @GetMapping("/{id}")
    public OwnerResponse findById(@PathVariable Long id) {
        return ownerService.findById(id);
    }

    @PostMapping
    public ResponseEntity<OwnerResponse> create(@Valid @RequestBody OwnerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ownerService.create(request));
    }

    @PutMapping("/{id}")
    public OwnerResponse update(@PathVariable Long id, @Valid @RequestBody OwnerRequest request) {
        return ownerService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        ownerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
