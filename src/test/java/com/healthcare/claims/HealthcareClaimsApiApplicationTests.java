package com.healthcare.claims;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthcare.claims.domain.ClaimStatus;
import com.healthcare.claims.domain.Patient;
import com.healthcare.claims.domain.PatientStatus;
import com.healthcare.claims.repository.PatientRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class HealthcareClaimsApiApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldListSeededPatients() throws Exception {
        mockMvc.perform(get("/patients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(3))))
                .andExpect(jsonPath("$[0].name").exists());
    }

    @Test
    void shouldCreateClaimAndUpdateStatus() throws Exception {
        Patient patient = patientRepository.save(new Patient("Test Patient", 29, PatientStatus.ACTIVE));

        String createResponse = mockMvc.perform(post("/claims")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "patientId": %d,
                                  "claimAmount": 450.25
                                }
                                """.formatted(patient.getId())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.claimStatus").value(ClaimStatus.SUBMITTED.name()))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode createdClaim = objectMapper.readTree(createResponse);
        long claimId = createdClaim.get("id").asLong();

        mockMvc.perform(put("/claims/{id}/status", claimId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "claimStatus": "APPROVED"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.claimStatus").value(ClaimStatus.APPROVED.name()));
    }

    @Test
    void shouldReturnClaimSummary() throws Exception {
        mockMvc.perform(get("/dashboard/claims-summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].claimStatus").exists())
                .andExpect(jsonPath("$[0].totalClaims").exists())
                .andExpect(jsonPath("$[0].totalClaimAmount").exists());
    }

    @Test
    void shouldValidateCreateClaimRequest() throws Exception {
        mockMvc.perform(post("/claims")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "claimAmount": 0
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.patientId").exists())
                .andExpect(jsonPath("$.validationErrors.claimAmount").exists());
    }
}
