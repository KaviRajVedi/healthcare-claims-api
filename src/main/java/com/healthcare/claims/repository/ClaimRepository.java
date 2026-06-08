package com.healthcare.claims.repository;

import com.healthcare.claims.domain.Claim;
import com.healthcare.claims.dto.ClaimSummaryResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ClaimRepository extends JpaRepository<Claim, Long> {

    List<Claim> findByPatientIdOrderByUpdatedAtDesc(Long patientId);

    @Query("""
            select new com.healthcare.claims.dto.ClaimSummaryResponse(
                c.claimStatus,
                count(c.id),
                sum(c.claimAmount),
                count(distinct p.id)
            )
            from Claim c
            join c.patient p
            group by c.claimStatus
            order by c.claimStatus
            """)
    List<ClaimSummaryResponse> summarizeClaimsByStatus();
}
