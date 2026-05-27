package com.ideal_10.ideal10spring.controllers;

import com.ideal_10.ideal10spring.dtos.ClearanceCertificateResponse;
import com.ideal_10.ideal10spring.services.ClearanceCertificateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/clearance-certificates")
public class ClearanceCertificateController {

    private final ClearanceCertificateService certificateService;

    @GetMapping
    public List<ClearanceCertificateResponse> findAll() {
        return certificateService.findAll();
    }

    @GetMapping("/{id}")
    public ClearanceCertificateResponse findById(@PathVariable Long id) {
        return certificateService.findById(id);
    }
}
