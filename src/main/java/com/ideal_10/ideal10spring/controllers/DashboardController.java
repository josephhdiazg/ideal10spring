package com.ideal_10.ideal10spring.controllers;

import com.ideal_10.ideal10spring.dtos.DashboardPredialResponse;
import com.ideal_10.ideal10spring.services.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/predial")
    public DashboardPredialResponse getPredialMetrics() {
        return dashboardService.getPredialMetrics();
    }
}
