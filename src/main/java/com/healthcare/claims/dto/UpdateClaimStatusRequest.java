package com.healthcare.claims.dto;

import com.healthcare.claims.domain.ClaimStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateClaimStatusRequest(
        @NotNull(message = "claimStatus is required")
        ClaimStatus claimStatus
) {
}
