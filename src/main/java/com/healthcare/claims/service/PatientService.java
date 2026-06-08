package com.healthcare.claims.service;

import com.healthcare.claims.domain.Patient;
import com.healthcare.claims.dto.CreatePatientRequest;
import com.healthcare.claims.dto.PatientResponse;
import com.healthcare.claims.repository.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PatientService {

    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Transactional(readOnly = true)
    public List<PatientResponse> getPatients() {
        return patientRepository.findAll()
                .stream()
                .map(PatientResponse::from)
                .toList();
    }

    @Transactional
    public PatientResponse createPatient(CreatePatientRequest request) {
        Patient patient = new Patient(
                request.name().trim(),
                request.age(),
                request.status()
        );

        return PatientResponse.from(patientRepository.save(patient));
    }
}
