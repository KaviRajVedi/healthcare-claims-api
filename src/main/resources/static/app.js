const claimStatuses = ["SUBMITTED", "IN_REVIEW", "APPROVED", "REJECTED", "PAID"];
const patientStatuses = ["ACTIVE", "INACTIVE"];

let patients = [];
let selectedPatientId = null;

const formatCurrency = (value) =>
    new Intl.NumberFormat("en-US", { style: "currency", currency: "USD" }).format(value);

const api = async (url, options = {}) => {
    const response = await fetch(url, {
        headers: { "Content-Type": "application/json" },
        ...options
    });

    if (!response.ok) {
        const error = await response.json().catch(() => ({ message: "Request failed" }));
        throw new Error(error.message || "Request failed");
    }

    return response.json();
};

async function loadDashboard() {
    const [patientData, summaryData] = await Promise.all([
        api("/patients"),
        api("/dashboard/claims-summary")
    ]);

    patients = patientData;
    selectedPatientId = selectedPatientId || patients[0]?.id;
    renderPatients();
    renderPatientOptions();
    renderSummary(summaryData);

    if (selectedPatientId) {
        await loadClaims(selectedPatientId);
    }
}

function renderSummary(summary) {
    document.getElementById("summaryCount").textContent = `${summary.length} statuses`;
    document.getElementById("summaryGrid").innerHTML = summary.map((item) => `
        <article class="summary-card">
            <strong>${item.claimStatus}</strong>
            <div class="amount">${formatCurrency(item.totalClaimAmount)}</div>
            <div class="meta">${item.totalClaims} claims across ${item.uniquePatients} patients</div>
        </article>
    `).join("");
}

function renderPatients() {
    document.getElementById("patientCount").textContent = `${patients.length} patients`;
    document.getElementById("patientsTable").innerHTML = patients.map((patient) => `
        <tr>
            <td>${patient.id}</td>
            <td>${patient.name}</td>
            <td>${patient.age}</td>
            <td><span class="badge ${patient.status}">${patient.status}</span></td>
            <td><button type="button" data-patient-id="${patient.id}">View Claims</button></td>
        </tr>
    `).join("");

    document.querySelectorAll("[data-patient-id]").forEach((button) => {
        button.addEventListener("click", () => loadClaims(Number(button.dataset.patientId)));
    });
}

function renderPatientOptions() {
    document.getElementById("patientSelect").innerHTML = patients.map((patient) => `
        <option value="${patient.id}">${patient.name}</option>
    `).join("");
}

async function loadClaims(patientId) {
    selectedPatientId = patientId;
    const patient = patients.find((item) => item.id === patientId);
    const claims = await api(`/patients/${patientId}/claims`);

    document.getElementById("claimsTitle").textContent = `${patient?.name || "Patient"} Claims`;
    document.getElementById("claimsCount").textContent = `${claims.length} claims`;

    const claimsList = document.getElementById("claimsList");
    claimsList.classList.toggle("empty-state", claims.length === 0);
    claimsList.innerHTML = claims.length
        ? claims.map((claim) => renderClaimRow(claim)).join("")
        : "No claims found for this patient.";

    document.querySelectorAll("[data-claim-id]").forEach((select) => {
        select.addEventListener("change", async () => {
            await updateClaimStatus(Number(select.dataset.claimId), select.value);
        });
    });
}

function renderClaimRow(claim) {
    const options = claimStatuses.map((status) => `
        <option value="${status}" ${status === claim.claimStatus ? "selected" : ""}>${status}</option>
    `).join("");

    return `
        <article class="claim-row">
            <div>
                <strong>Claim #${claim.id}</strong>
                <div>${formatCurrency(claim.claimAmount)}</div>
            </div>
            <span class="badge ${claim.claimStatus}">${claim.claimStatus}</span>
            <select data-claim-id="${claim.id}" aria-label="Update claim status">${options}</select>
        </article>
    `;
}

async function updateClaimStatus(claimId, claimStatus) {
    await api(`/claims/${claimId}/status`, {
        method: "PUT",
        body: JSON.stringify({ claimStatus })
    });
    await loadDashboard();
}

document.getElementById("patientForm").addEventListener("submit", async (event) => {
    event.preventDefault();
    const formMessage = document.getElementById("patientFormMessage");
    formMessage.textContent = "";

    try {
        const name = document.getElementById("patientName").value;
        const age = Number(document.getElementById("patientAge").value);
        const status = document.getElementById("patientStatus").value;

        if (!patientStatuses.includes(status)) {
            throw new Error("Invalid patient status");
        }

        const patient = await api("/patients", {
            method: "POST",
            body: JSON.stringify({ name, age, status })
        });

        selectedPatientId = patient.id;
        document.getElementById("patientName").value = "";
        document.getElementById("patientAge").value = "";
        document.getElementById("patientStatus").value = "ACTIVE";
        formMessage.textContent = "Patient created.";
        await loadDashboard();
    } catch (error) {
        formMessage.textContent = error.message;
    }
});

document.getElementById("claimForm").addEventListener("submit", async (event) => {
    event.preventDefault();
    const formMessage = document.getElementById("formMessage");
    formMessage.textContent = "";

    try {
        const patientId = Number(document.getElementById("patientSelect").value);
        const claimAmount = Number(document.getElementById("claimAmount").value);

        await api("/claims", {
            method: "POST",
            body: JSON.stringify({ patientId, claimAmount })
        });

        selectedPatientId = patientId;
        document.getElementById("claimAmount").value = "";
        formMessage.textContent = "Claim created.";
        await loadDashboard();
    } catch (error) {
        formMessage.textContent = error.message;
    }
});

document.getElementById("refreshButton").addEventListener("click", loadDashboard);

loadDashboard().catch((error) => {
    document.getElementById("summaryGrid").innerHTML = `<p class="empty-state">${error.message}</p>`;
});
