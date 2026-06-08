package com.healthcare.claims.controller;

import com.healthcare.claims.dto.ClaimResponse;
import com.healthcare.claims.dto.CreateClaimRequest;
import com.healthcare.claims.dto.UpdateClaimStatusRequest;
import com.healthcare.claims.service.ClaimService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/claims")
public class ClaimController {

    private final ClaimService claimService;

    public ClaimController(ClaimService claimService) {
        this.claimService = claimService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClaimResponse createClaim(@Valid @RequestBody CreateClaimRequest request) {
        return claimService.createClaim(request);
    }

    @PutMapping("/{id}/status")
    public ClaimResponse updateClaimStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateClaimStatusRequest request
    ) {
        return claimService.updateClaimStatus(id, request);
    }
}
