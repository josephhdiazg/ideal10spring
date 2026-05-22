package com.ideal_10.ideal10spring.dtos;

import com.ideal_10.ideal10spring.enums.EstadoCertificado;

import java.time.LocalDateTime;

public record CertificadoPazSalvoResponse(
        Long id,
        Long liquidacionId,
        Long propertyId,
        String cadastralCode,
        String certificateNumber,
        LocalDateTime issuedAt,
        EstadoCertificado status
) {
}
