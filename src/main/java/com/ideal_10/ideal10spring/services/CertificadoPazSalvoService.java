package com.ideal_10.ideal10spring.services;

import com.ideal_10.ideal10spring.dtos.CertificadoPazSalvoResponse;
import com.ideal_10.ideal10spring.entities.CertificadoPazSalvo;
import com.ideal_10.ideal10spring.entities.LiquidacionPredial;
import com.ideal_10.ideal10spring.enums.EstadoCertificado;
import com.ideal_10.ideal10spring.enums.EstadoLiquidacion;
import com.ideal_10.ideal10spring.exceptions.ResourceNotFoundException;
import com.ideal_10.ideal10spring.repositories.CertificadoPazSalvoRepository;
import com.ideal_10.ideal10spring.repositories.LiquidacionPredialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CertificadoPazSalvoService {

    private final CertificadoPazSalvoRepository certificadoRepository;
    private final LiquidacionPredialRepository liquidacionRepository;
    private final LiquidacionPredialService liquidacionService;

    @Transactional(readOnly = true)
    public List<CertificadoPazSalvoResponse> findAll() {
        return certificadoRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CertificadoPazSalvoResponse findById(Long id) {
        CertificadoPazSalvo certificate = certificadoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Clearance certificate not found with id " + id));
        return toResponse(certificate);
    }

    @Transactional
    public CertificadoPazSalvoResponse generate(Long liquidacionId) {
        return certificadoRepository.findByLiquidacionId(liquidacionId)
                .map(this::toResponse)
                .orElseGet(() -> createCertificate(liquidacionId));
    }

    private CertificadoPazSalvoResponse createCertificate(Long liquidacionId) {
        LiquidacionPredial liquidacion = liquidacionService.getEntity(liquidacionId);
        if (liquidacion.getStatus() != EstadoLiquidacion.PAGADA) {
            throw new IllegalArgumentException("Clearance certificate requires a paid liquidation");
        }
        boolean hasPendingDebt = liquidacionRepository.existsByPropertyIdAndStatusIn(
                liquidacion.getProperty().getId(),
                List.of(EstadoLiquidacion.PENDIENTE, EstadoLiquidacion.PARCIAL, EstadoLiquidacion.VENCIDA)
        );
        if (hasPendingDebt) {
            throw new IllegalArgumentException("Property has pending debts");
        }

        CertificadoPazSalvo certificate = new CertificadoPazSalvo();
        certificate.setLiquidacion(liquidacion);
        certificate.setCertificateNumber(buildCertificateNumber(liquidacion));
        certificate.setIssuedAt(LocalDateTime.now());
        certificate.setStatus(EstadoCertificado.GENERADO);
        return toResponse(certificadoRepository.save(certificate));
    }

    private String buildCertificateNumber(LiquidacionPredial liquidacion) {
        return "PS-" + liquidacion.getFiscalYear() + "-" + String.format("%06d", liquidacion.getId());
    }

    private CertificadoPazSalvoResponse toResponse(CertificadoPazSalvo certificate) {
        LiquidacionPredial liquidacion = certificate.getLiquidacion();
        return new CertificadoPazSalvoResponse(
                certificate.getId(),
                liquidacion.getId(),
                liquidacion.getProperty().getId(),
                liquidacion.getProperty().getCadastralCode(),
                certificate.getCertificateNumber(),
                certificate.getIssuedAt(),
                certificate.getStatus()
        );
    }
}
