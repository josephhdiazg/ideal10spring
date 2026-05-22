package com.ideal_10.ideal10spring.controllers;

import com.ideal_10.ideal10spring.dtos.CertificadoPazSalvoResponse;
import com.ideal_10.ideal10spring.services.CertificadoPazSalvoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/clearance-certificates")
public class CertificadoPazSalvoController {

    private final CertificadoPazSalvoService certificadoService;

    @GetMapping
    public List<CertificadoPazSalvoResponse> findAll() {
        return certificadoService.findAll();
    }

    @GetMapping("/{id}")
    public CertificadoPazSalvoResponse findById(@PathVariable Long id) {
        return certificadoService.findById(id);
    }
}
