package com.ideal_10.ideal10spring.services;

import com.ideal_10.ideal10spring.dtos.DashboardPredialResponse;
import com.ideal_10.ideal10spring.enums.EstadoLiquidacion;
import com.ideal_10.ideal10spring.enums.EstadoPago;
import com.ideal_10.ideal10spring.repositories.LiquidacionPredialRepository;
import com.ideal_10.ideal10spring.repositories.PagoPredialRepository;
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

    private final LiquidacionPredialRepository liquidacionRepository;
    private final PagoPredialRepository pagoRepository;

    @Transactional(readOnly = true)
    public DashboardPredialResponse getPredialMetrics() {
        Map<String, Long> liquidationsByStatus = Arrays.stream(EstadoLiquidacion.values())
                .collect(Collectors.toMap(Enum::name, liquidacionRepository::countByStatus));

        return new DashboardPredialResponse(
                liquidationsByStatus,
                liquidacionRepository.sumTotalAmount(),
                pagoRepository.sumAmountByStatus(EstadoPago.REGISTRADO),
                liquidacionRepository.sumBalanceByStatuses(List.of(
                        EstadoLiquidacion.PENDIENTE,
                        EstadoLiquidacion.PARCIAL,
                        EstadoLiquidacion.VENCIDA
                ))
        );
    }
}
