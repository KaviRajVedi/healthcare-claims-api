package com.healthcare.claims.config;

import com.healthcare.claims.domain.Claim;
import com.healthcare.claims.domain.ClaimStatus;
import com.healthcare.claims.domain.Patient;
import com.healthcare.claims.domain.PatientStatus;
import com.healthcare.claims.repository.ClaimRepository;
import com.healthcare.claims.repository.PatientRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final PatientRepository patientRepository;
    private final ClaimRepository claimRepository;

    public DataSeeder(PatientRepository patientRepository, ClaimRepository claimRepository) {
        this.patientRepository = patientRepository;
        this.claimRepository = claimRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (patientRepository.count() > 0) {
            return;
        }

        Patient maya = new Patient("Maya Patel", 34, PatientStatus.ACTIVE);
        Patient daniel = new Patient("Daniel Smith", 52, PatientStatus.ACTIVE);
        Patient sofia = new Patient("Sofia Garcia", 41, PatientStatus.INACTIVE);

        patientRepository.saveAll(List.of(maya, daniel, sofia));

        claimRepository.saveAll(List.of(
                new Claim(maya, new BigDecimal("1250.75"), ClaimStatus.SUBMITTED),
                new Claim(maya, new BigDecimal("800.00"), ClaimStatus.APPROVED),
                new Claim(daniel, new BigDecimal("2150.25"), ClaimStatus.IN_REVIEW),
                new Claim(sofia, new BigDecimal("300.00"), ClaimStatus.REJECTED)
        ));
    }
}
