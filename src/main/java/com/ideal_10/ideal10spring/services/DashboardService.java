package com.ideal_10.ideal10spring.services;

import com.ideal_10.ideal10spring.dtos.PropertyDashboardResponse;
import com.ideal_10.ideal10spring.enums.EstadoLiquidacion;
import com.ideal_10.ideal10spring.enums.EstadoPago;
import com.ideal_10.ideal10spring.repositories.PropertyAssessmentRepository;
import com.ideal_10.ideal10spring.repositories.PropertyPaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final PropertyAssessmentRepository assessmentRepository;
    private final PropertyPaymentRepository paymentRepository;

    @Transactional(readOnly = true)
    public PropertyDashboardResponse getPropertyTaxMetrics() {
        Map<String, Long> liquidationsByStatus = Arrays.stream(EstadoLiquidacion.values())
                .collect(Collectors.toMap(Enum::name, assessmentRepository::countByStatus));

        return new PropertyDashboardResponse(
                liquidationsByStatus,
                assessmentRepository.sumTotalAmount(),
                paymentRepository.sumAmountByStatus(EstadoPago.REGISTRADO),
                assessmentRepository.sumBalanceByStatuses(List.of(
                        EstadoLiquidacion.PENDIENTE,
                        EstadoLiquidacion.PARCIAL,
                        EstadoLiquidacion.VENCIDA
                ))
        );
    }
}
