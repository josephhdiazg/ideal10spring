package com.ideal_10.ideal10spring.dtos;

import com.ideal_10.ideal10spring.enums.EstadoLiquidacion;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record PropertyAssessmentResponse(
        Long id,
        Long propertyId,
        String cadastralCode,
        Integer fiscalYear,
        LocalDate issueDate,
        LocalDate dueDate,
        BigDecimal subtotal,
        BigDecimal discountAmount,
        BigDecimal interestAmount,
        BigDecimal totalAmount,
        BigDecimal balance,
        EstadoLiquidacion status,
        List<AssessmentDetailResponse> details
) {
}
