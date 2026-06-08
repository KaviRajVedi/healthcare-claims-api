package com.healthcare.claims.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "claims")
public class Claim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Column(name = "claim_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal claimAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "claim_status", nullable = false, length = 30)
    private ClaimStatus claimStatus;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected Claim() {
    }

    public Claim(Patient patient, BigDecimal claimAmount, ClaimStatus claimStatus) {
        this.patient = patient;
        this.claimAmount = claimAmount;
        this.claimStatus = claimStatus;
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    void touchUpdatedAt() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Patient getPatient() {
        return patient;
    }

    public BigDecimal getClaimAmount() {
        return claimAmount;
    }

    public ClaimStatus getClaimStatus() {
        return claimStatus;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void updateStatus(ClaimStatus claimStatus) {
        this.claimStatus = claimStatus;
        this.updatedAt = LocalDateTime.now();
    }
}
