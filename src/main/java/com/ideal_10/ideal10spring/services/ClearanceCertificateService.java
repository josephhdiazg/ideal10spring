package com.ideal_10.ideal10spring.services;

import com.ideal_10.ideal10spring.dtos.ClearanceCertificateResponse;
import com.ideal_10.ideal10spring.entities.ClearanceCertificate;
import com.ideal_10.ideal10spring.entities.PropertyAssessment;
import com.ideal_10.ideal10spring.enums.EstadoCertificado;
import com.ideal_10.ideal10spring.enums.EstadoLiquidacion;
import com.ideal_10.ideal10spring.exceptions.ResourceNotFoundException;
import com.ideal_10.ideal10spring.repositories.ClearanceCertificateRepository;
import com.ideal_10.ideal10spring.repositories.PropertyAssessmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClearanceCertificateService {

    private final ClearanceCertificateRepository certificateRepository;
    private final PropertyAssessmentRepository assessmentRepository;
    private final PropertyAssessmentService assessmentService;

    @Transactional(readOnly = true)
    public List<ClearanceCertificateResponse> findAll() {
        return certificateRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ClearanceCertificateResponse findById(Long id) {
        ClearanceCertificate certificate = certificateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Clearance certificate not found with id " + id));
        return toResponse(certificate);
    }

    @Transactional
    public ClearanceCertificateResponse generate(Long assessmentId) {
        return certificateRepository.findByAssessmentId(assessmentId)
                .map(this::toResponse)
                .orElseGet(() -> createCertificate(assessmentId));
    }

    private ClearanceCertificateResponse createCertificate(Long assessmentId) {
        PropertyAssessment assessment = assessmentService.getEntity(assessmentId);
        if (assessment.getStatus() != EstadoLiquidacion.PAGADA) {
            throw new IllegalArgumentException("Clearance certificate requires a paid liquidation");
        }
        boolean hasPendingDebt = assessmentRepository.existsByPropertyIdAndStatusIn(
                assessment.getProperty().getId(),
                List.of(EstadoLiquidacion.PENDIENTE, EstadoLiquidacion.PARCIAL, EstadoLiquidacion.VENCIDA)
        );
        if (hasPendingDebt) {
            throw new IllegalArgumentException("Property has pending debts");
        }

        ClearanceCertificate certificate = new ClearanceCertificate();
        certificate.setAssessment(assessment);
        certificate.setCertificateNumber(buildCertificateNumber(assessment));
        certificate.setIssuedAt(LocalDateTime.now());
        certificate.setStatus(EstadoCertificado.GENERADO);
        return toResponse(certificateRepository.save(certificate));
    }

    private String buildCertificateNumber(PropertyAssessment assessment) {
        return "PS-" + assessment.getFiscalYear() + "-" + String.format("%06d", assessment.getId());
    }

    private ClearanceCertificateResponse toResponse(ClearanceCertificate certificate) {
        PropertyAssessment assessment = certificate.getAssessment();
        return new ClearanceCertificateResponse(
                certificate.getId(),
                assessment.getId(),
                assessment.getProperty().getId(),
                assessment.getProperty().getCadastralCode(),
                certificate.getCertificateNumber(),
                certificate.getIssuedAt(),
                certificate.getStatus()
        );
    }
}
