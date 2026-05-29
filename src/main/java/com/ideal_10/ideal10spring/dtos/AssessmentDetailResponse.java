package com.ideal_10.ideal10spring.dtos;

import com.ideal_10.ideal10spring.enums.AssessmentMovementType;

import java.math.BigDecimal;

public record AssessmentDetailResponse(
        Long id,
        AssessmentMovementType movementType,
        String concept,
        BigDecimal amount
) {
}
