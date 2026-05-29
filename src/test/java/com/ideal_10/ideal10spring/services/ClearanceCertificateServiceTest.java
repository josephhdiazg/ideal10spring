package com.ideal_10.ideal10spring.services;

import com.ideal_10.ideal10spring.dtos.ClearanceCertificateResponse;
import com.ideal_10.ideal10spring.entities.ClearanceCertificate;
import com.ideal_10.ideal10spring.entities.Property;
import com.ideal_10.ideal10spring.entities.PropertyAssessment;
import com.ideal_10.ideal10spring.enums.AssessmentStatus;
import com.ideal_10.ideal10spring.enums.CertificateStatus;
import com.ideal_10.ideal10spring.repositories.ClearanceCertificateRepository;
import com.ideal_10.ideal10spring.repositories.PropertyAssessmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClearanceCertificateServiceTest {

    @Mock
    private ClearanceCertificateRepository certificateRepository;

    @Mock
    private PropertyAssessmentRepository assessmentRepository;

    @Mock
    private PropertyAssessmentService assessmentService;

    @InjectMocks
    private ClearanceCertificateService certificateService;

    @Test
    void generateCreatesCertificateForPaidAssessmentWithoutPendingDebt() {
        PropertyAssessment assessment = assessment(1L, 10L, AssessmentStatus.PAID);

        when(certificateRepository.findByAssessmentId(1L)).thenReturn(Optional.empty());
        when(assessmentService.getEntity(1L)).thenReturn(assessment);
        when(assessmentRepository.existsByPropertyIdAndStatusIn(any(), anyCollection())).thenReturn(false);
        when(certificateRepository.save(any(ClearanceCertificate.class))).thenAnswer(invocation -> {
            ClearanceCertificate certificate = invocation.getArgument(0);
            certificate.setId(7L);
            return certificate;
        });

        ClearanceCertificateResponse response = certificateService.generate(1L);

        assertEquals(7L, response.id());
        assertEquals(1L, response.assessmentId());
        assertEquals(10L, response.propertyId());
        assertEquals("0102030405", response.cadastralCode());
        assertEquals("PS-2026-000001", response.certificateNumber());
        assertEquals(CertificateStatus.ISSUED, response.status());
        assertNotNull(response.issuedAt());
    }

    @Test
    void generateReturnsExistingCertificateWhenItAlreadyExists() {
        PropertyAssessment assessment = assessment(1L, 10L, AssessmentStatus.PAID);
        ClearanceCertificate certificate = new ClearanceCertificate();
        certificate.setId(7L);
        certificate.setAssessment(assessment);
        certificate.setCertificateNumber("PS-2026-000001");
        certificate.setStatus(CertificateStatus.ISSUED);

        when(certificateRepository.findByAssessmentId(1L)).thenReturn(Optional.of(certificate));

        ClearanceCertificateResponse response = certificateService.generate(1L);

        assertEquals("PS-2026-000001", response.certificateNumber());
        verify(assessmentService, never()).getEntity(any());
        verify(certificateRepository, never()).save(any());
    }

    @Test
    void generateRejectsUnpaidAssessment() {
        PropertyAssessment assessment = assessment(1L, 10L, AssessmentStatus.PARTIAL);

        when(certificateRepository.findByAssessmentId(1L)).thenReturn(Optional.empty());
        when(assessmentService.getEntity(1L)).thenReturn(assessment);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> certificateService.generate(1L)
        );

        assertEquals("Clearance certificate requires a paid assessment", exception.getMessage());
        verify(certificateRepository, never()).save(any());
    }

    private PropertyAssessment assessment(Long assessmentId, Long propertyId, AssessmentStatus status) {
        Property property = new Property();
        property.setId(propertyId);
        property.setCadastralCode("0102030405");

        PropertyAssessment assessment = new PropertyAssessment();
        assessment.setId(assessmentId);
        assessment.setProperty(property);
        assessment.setFiscalYear(2026);
        assessment.setStatus(status);
        return assessment;
    }
}
