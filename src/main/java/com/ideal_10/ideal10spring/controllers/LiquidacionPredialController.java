package com.ideal_10.ideal10spring.controllers;

import com.ideal_10.ideal10spring.dtos.CertificadoPazSalvoResponse;
import com.ideal_10.ideal10spring.dtos.LiquidacionPredialRequest;
import com.ideal_10.ideal10spring.dtos.LiquidacionPredialResponse;
import com.ideal_10.ideal10spring.dtos.PagoPredialRequest;
import com.ideal_10.ideal10spring.dtos.PagoPredialResponse;
import com.ideal_10.ideal10spring.services.CertificadoPazSalvoService;
import com.ideal_10.ideal10spring.services.LiquidacionPredialService;
import com.ideal_10.ideal10spring.services.PagoPredialService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/liquidations")
public class LiquidacionPredialController {

    private final LiquidacionPredialService liquidacionService;
    private final PagoPredialService pagoService;
    private final CertificadoPazSalvoService certificadoService;

    @GetMapping
    public List<LiquidacionPredialResponse> findAll(@RequestParam(required = false) Long propertyId) {
        if (propertyId != null) {
            return liquidacionService.findByProperty(propertyId);
        }
        return liquidacionService.findAll();
    }

    @GetMapping("/{id}")
    public LiquidacionPredialResponse findById(@PathVariable Long id) {
        return liquidacionService.findById(id);
    }

    @PostMapping
    public ResponseEntity<LiquidacionPredialResponse> create(@Valid @RequestBody LiquidacionPredialRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(liquidacionService.create(request));
    }

    @GetMapping("/{id}/payments")
    public List<PagoPredialResponse> findPayments(@PathVariable Long id) {
        return pagoService.findByLiquidacion(id);
    }

    @PostMapping("/{id}/payments")
    public ResponseEntity<PagoPredialResponse> registerPayment(
            @PathVariable Long id,
            @Valid @RequestBody PagoPredialRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pagoService.registerPayment(id, request));
    }

    @PostMapping("/{id}/clearance-certificates")
    public ResponseEntity<CertificadoPazSalvoResponse> generateClearanceCertificate(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.CREATED).body(certificadoService.generate(id));
    }
}
