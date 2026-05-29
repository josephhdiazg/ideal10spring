package com.ideal_10.ideal10spring.controllers;

import com.ideal_10.ideal10spring.dtos.ClearanceCertificateResponse;
import com.ideal_10.ideal10spring.dtos.PropertyAssessmentRequest;
import com.ideal_10.ideal10spring.dtos.PropertyAssessmentResponse;
import com.ideal_10.ideal10spring.dtos.PropertyPaymentRequest;
import com.ideal_10.ideal10spring.dtos.PropertyPaymentResponse;
import com.ideal_10.ideal10spring.services.ClearanceCertificateService;
import com.ideal_10.ideal10spring.services.PropertyAssessmentService;
import com.ideal_10.ideal10spring.services.PropertyPaymentService;
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
public class PropertyAssessmentController {

    private final PropertyAssessmentService assessmentService;
    private final PropertyPaymentService paymentService;
    private final ClearanceCertificateService certificateService;

    @GetMapping
    public List<PropertyAssessmentResponse> findAll(@RequestParam(required = false) Long propertyId) {
        if (propertyId != null) {
            return assessmentService.findByProperty(propertyId);
        }
        return assessmentService.findAll();
    }

    @GetMapping("/{id}")
    public PropertyAssessmentResponse findById(@PathVariable Long id) {
        return assessmentService.findById(id);
    }

    @PostMapping
    public ResponseEntity<PropertyAssessmentResponse> create(@Valid @RequestBody PropertyAssessmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(assessmentService.create(request));
    }

    @GetMapping("/{id}/payments")
    public List<PropertyPaymentResponse> findPayments(@PathVariable Long id) {
        return paymentService.findByAssessment(id);
    }

    @PostMapping("/{id}/payments")
    public ResponseEntity<PropertyPaymentResponse> registerPayment(
            @PathVariable Long id,
            @Valid @RequestBody PropertyPaymentRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.registerPayment(id, request));
    }

    @PostMapping("/{id}/clearance-certificates")
    public ResponseEntity<ClearanceCertificateResponse> generateClearanceCertificate(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.CREATED).body(certificateService.generate(id));
    }
}
