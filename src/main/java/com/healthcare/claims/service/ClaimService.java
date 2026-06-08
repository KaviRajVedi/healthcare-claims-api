package com.healthcare.claims.service;

import com.healthcare.claims.domain.Claim;
import com.healthcare.claims.domain.ClaimStatus;
import com.healthcare.claims.domain.Patient;
import com.healthcare.claims.dto.ClaimResponse;
import com.healthcare.claims.dto.ClaimSummaryResponse;
import com.healthcare.claims.dto.CreateClaimRequest;
import com.healthcare.claims.dto.UpdateClaimStatusRequest;
import com.healthcare.claims.exception.ResourceNotFoundException;
import com.healthcare.claims.repository.ClaimRepository;
import com.healthcare.claims.repository.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClaimService {

    private final ClaimRepository claimRepository;
    private final PatientRepository patientRepository;

    public ClaimService(ClaimRepository claimRepository, PatientRepository patientRepository) {
        this.claimRepository = claimRepository;
        this.patientRepository = patientRepository;
    }

    @Transactional(readOnly = true)
    public List<ClaimResponse> getClaimsByPatient(Long patientId) {
        if (!patientRepository.existsById(patientId)) {
            throw new ResourceNotFoundException("Patient not found with id " + patientId);
        }

        return claimRepository.findByPatientIdOrderByUpdatedAtDesc(patientId)
                .stream()
                .map(ClaimResponse::from)
                .toList();
    }

    @Transactional
    public ClaimResponse createClaim(CreateClaimRequest request) {
        Patient patient = patientRepository.findById(request.patientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id " + request.patientId()));

        Claim claim = new Claim(patient, request.claimAmount(), ClaimStatus.SUBMITTED);
        return ClaimResponse.from(claimRepository.save(claim));
    }

    @Transactional
    public ClaimResponse updateClaimStatus(Long claimId, UpdateClaimStatusRequest request) {
        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found with id " + claimId));

        claim.updateStatus(request.claimStatus());
        return ClaimResponse.from(claim);
    }

    @Transactional(readOnly = true)
    public List<ClaimSummaryResponse> getClaimSummary() {
        return claimRepository.summarizeClaimsByStatus();
    }
}
