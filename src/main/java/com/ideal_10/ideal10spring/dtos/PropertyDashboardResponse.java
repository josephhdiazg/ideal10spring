package com.ideal_10.ideal10spring.dtos;

import java.math.BigDecimal;
import java.util.Map;

public record PropertyDashboardResponse(
        Map<String, Long> liquidationsByStatus,
        BigDecimal totalLiquidated,
        BigDecimal totalCollected,
        BigDecimal pendingPortfolio
) {
}
