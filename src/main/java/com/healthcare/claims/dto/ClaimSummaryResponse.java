package com.healthcare.claims.dto;

import com.healthcare.claims.domain.ClaimStatus;

import java.math.BigDecimal;

public record ClaimSummaryResponse(
        ClaimStatus claimStatus,
        Long totalClaims,
        BigDecimal totalClaimAmount,
        Long uniquePatients
) {
}
