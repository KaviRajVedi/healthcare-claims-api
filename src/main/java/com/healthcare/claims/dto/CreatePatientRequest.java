package com.healthcare.claims.dto;

import com.healthcare.claims.domain.PatientStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreatePatientRequest(
        @NotBlank(message = "name is required")
        String name,

        @NotNull(message = "age is required")
        @Min(value = 0, message = "age cannot be negative")
        @Max(value = 130, message = "age must be realistic")
        Integer age,

        @NotNull(message = "status is required")
        PatientStatus status
) {
}
