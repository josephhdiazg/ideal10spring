package com.ideal_10.ideal10spring.dtos;

import java.time.LocalDate;

public record FiscalYearResponse(
        Long id,
        Integer year,
        String description,
        LocalDate startDate,
        LocalDate endDate,
        Boolean active
) {
}
