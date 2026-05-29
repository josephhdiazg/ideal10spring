package com.ideal_10.ideal10spring.dtos;

import com.ideal_10.ideal10spring.enums.CertificateStatus;

import java.time.LocalDateTime;

public record ClearanceCertificateResponse(
        Long id,
        Long assessmentId,
        Long propertyId,
        String cadastralCode,
        String certificateNumber,
        LocalDateTime issuedAt,
        CertificateStatus status
) {
}
