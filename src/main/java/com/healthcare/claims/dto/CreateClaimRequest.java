package com.healthcare.claims.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateClaimRequest(
        @NotNull(message = "patientId is required")
        Long patientId,

        @NotNull(message = "claimAmount is required")
        @DecimalMin(value = "0.01", message = "claimAmount must be greater than 0")
        BigDecimal claimAmount
) {
}
