package com.ideal_10.ideal10spring.dtos;

import com.ideal_10.ideal10spring.enums.TipoMovimientoLiquidacion;

import java.math.BigDecimal;

public record AssessmentDetailResponse(
        Long id,
        TipoMovimientoLiquidacion movementType,
        String concept,
        BigDecimal amount
) {
}
