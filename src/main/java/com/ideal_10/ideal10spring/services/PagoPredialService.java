package com.ideal_10.ideal10spring.services;

import com.ideal_10.ideal10spring.dtos.PagoPredialRequest;
import com.ideal_10.ideal10spring.dtos.PagoPredialResponse;
import com.ideal_10.ideal10spring.entities.LiquidacionPredial;
import com.ideal_10.ideal10spring.entities.PagoPredial;
import com.ideal_10.ideal10spring.enums.EstadoLiquidacion;
import com.ideal_10.ideal10spring.enums.EstadoPago;
import com.ideal_10.ideal10spring.repositories.PagoPredialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PagoPredialService {

    private final PagoPredialRepository pagoRepository;
    private final LiquidacionPredialService liquidacionService;

    @Transactional(readOnly = true)
    public List<PagoPredialResponse> findByLiquidacion(Long liquidacionId) {
        liquidacionService.getEntity(liquidacionId);
        return pagoRepository.findByLiquidacionId(liquidacionId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public PagoPredialResponse registerPayment(Long liquidacionId, PagoPredialRequest request) {
        LiquidacionPredial liquidacion = liquidacionService.getEntity(liquidacionId);
        if (liquidacion.getStatus() == EstadoLiquidacion.PAGADA) {
            throw new IllegalArgumentException("Liquidation is already paid");
        }
        if (liquidacion.getStatus() == EstadoLiquidacion.ANULADA) {
            throw new IllegalArgumentException("Cannot register payment for cancelled liquidation");
        }
        if (request.amount().compareTo(liquidacion.getBalance()) > 0) {
            throw new IllegalArgumentException("Payment amount cannot exceed pending balance");
        }

        PagoPredial payment = new PagoPredial();
        payment.setLiquidacion(liquidacion);
        payment.setAmount(request.amount());
        payment.setPaymentMethod(request.paymentMethod());
        payment.setReference(request.reference());
        payment.setPaymentDate(LocalDateTime.now());
        payment.setStatus(EstadoPago.REGISTRADO);
        PagoPredial saved = pagoRepository.save(payment);

        liquidacionService.updateAfterPayment(liquidacion, request.amount());
        return toResponse(saved);
    }

    public PagoPredialResponse toResponse(PagoPredial payment) {
        return new PagoPredialResponse(
                payment.getId(),
                payment.getLiquidacion().getId(),
                payment.getAmount(),
                payment.getPaymentMethod(),
                payment.getReference(),
                payment.getPaymentDate(),
                payment.getStatus()
        );
    }
}
