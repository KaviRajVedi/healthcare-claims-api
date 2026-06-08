package com.healthcare.claims.controller;

import com.healthcare.claims.dto.ClaimResponse;
import com.healthcare.claims.dto.CreatePatientRequest;
import com.healthcare.claims.dto.PatientResponse;
import com.healthcare.claims.service.ClaimService;
import com.healthcare.claims.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/patients")
public class PatientController {

    private final PatientService patientService;
    private final ClaimService claimService;

    public PatientController(PatientService patientService, ClaimService claimService) {
        this.patientService = patientService;
        this.claimService = claimService;
    }

    @GetMapping
    public List<PatientResponse> getPatients() {
        return patientService.getPatients();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PatientResponse createPatient(@Valid @RequestBody CreatePatientRequest request) {
        return patientService.createPatient(request);
    }

    @GetMapping("/{id}/claims")
    public List<ClaimResponse> getClaimsByPatient(@PathVariable Long id) {
        return claimService.getClaimsByPatient(id);
    }
}
