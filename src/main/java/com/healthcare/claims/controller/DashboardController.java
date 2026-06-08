package com.healthcare.claims.controller;

import com.healthcare.claims.dto.ClaimSummaryResponse;
import com.healthcare.claims.service.ClaimService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final ClaimService claimService;

    public DashboardController(ClaimService claimService) {
        this.claimService = claimService;
    }

    @GetMapping("/claims-summary")
    public List<ClaimSummaryResponse> getClaimSummary() {
        return claimService.getClaimSummary();
    }
}
