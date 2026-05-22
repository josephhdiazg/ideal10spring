package com.ideal_10.ideal10spring.dtos;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record FiscalYearRequest(
        @NotNull Integer year,
        String description,
        @NotNull LocalDate startDate,
        @NotNull LocalDate endDate,
        Boolean active
) {
}
