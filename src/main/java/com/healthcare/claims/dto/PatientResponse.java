package com.healthcare.claims.dto;

import com.healthcare.claims.domain.Patient;
import com.healthcare.claims.domain.PatientStatus;

public record PatientResponse(
        Long id,
        String name,
        Integer age,
        PatientStatus status
) {
    public static PatientResponse from(Patient patient) {
        return new PatientResponse(
                patient.getId(),
                patient.getName(),
                patient.getAge(),
                patient.getStatus()
        );
    }
}
