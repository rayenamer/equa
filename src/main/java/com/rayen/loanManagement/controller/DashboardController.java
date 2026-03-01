package com.rayen.loanManagement.controller;

import com.rayen.loanManagement.model.DashboardResponse;
import com.rayen.loanManagement.model.UserDashboardResponse;
import com.rayen.loanManagement.service.DashboardService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/global")
    public DashboardResponse getGlobalDashboard() {
        return dashboardService.getGlobalDashboard();
    }

    @GetMapping("/user/{userId}")
    public UserDashboardResponse getUserDashboard(@PathVariable Long userId) {
        return dashboardService.getUserDashboard(userId);
    }
}
