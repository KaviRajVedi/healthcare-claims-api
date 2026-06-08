package com.healthcare.claims.dto;

import com.healthcare.claims.domain.Claim;
import com.healthcare.claims.domain.ClaimStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ClaimResponse(
        Long id,
        Long patientId,
        String patientName,
        BigDecimal claimAmount,
        ClaimStatus claimStatus,
        LocalDateTime updatedAt
) {
    public static ClaimResponse from(Claim claim) {
        return new ClaimResponse(
                claim.getId(),
                claim.getPatient().getId(),
                claim.getPatient().getName(),
                claim.getClaimAmount(),
                claim.getClaimStatus(),
                claim.getUpdatedAt()
        );
    }
}
