# Healthcare Claims API with CI/CD Pipeline

Spring Boot backend for tracking healthcare patients and insurance claims. The app uses REST APIs, MySQL-ready JPA entities, validation, exception handling, SQL-style aggregation, Docker, and a GitHub Actions CI/CD workflow.

## Tech Stack

- Java 17
- Spring Boot 3
- Spring Web
- Spring Data JPA
- MySQL
- Maven
- Docker
- GitHub Actions

## Database Tables

`patients`

| Column | Type | Notes |
| --- | --- | --- |
| id | bigint | Primary key |
| name | varchar | Patient name |
| age | int | Patient age |
| status | varchar | `ACTIVE` or `INACTIVE` |

`claims`

| Column | Type | Notes |
| --- | --- | --- |
| id | bigint | Primary key |
| patient_id | bigint | Foreign key to `patients.id` |
| claim_amount | decimal | Claim amount |
| claim_status | varchar | `SUBMITTED`, `IN_REVIEW`, `APPROVED`, `REJECTED`, `PAID` |
| updated_at | datetime | Last claim update time |

## APIs

### Get all patients

```http
GET /patients
```

### Get claims for one patient

```http
GET /patients/{id}/claims
```

### Create a claim

```http
POST /claims
Content-Type: application/json

{
  "patientId": 1,
  "claimAmount": 1250.75
}
```

New claims start as `SUBMITTED`.

### Update claim status

```http
PUT /claims/{id}/status
Content-Type: application/json

{
  "claimStatus": "APPROVED"
}
```

### Claims dashboard summary

```http
GET /dashboard/claims-summary
```

Returns aggregated claim totals grouped by status.

## Run Locally

### Option 1: Run quickly with H2

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

This starts the API with an in-memory database and seed data. The H2 console is available at:

```text
http://localhost:8080/h2-console
```

Use this JDBC URL in the console:

```text
jdbc:h2:mem:claims_local
```

### Option 2: Run with MySQL

Start MySQL first, then create a database named `healthcare_claims`, or let the configured JDBC URL create it.

```bash
mvn spring-boot:run
```

Environment variables can override the MySQL settings:

```bash
DB_URL=jdbc:mysql://localhost:3306/healthcare_claims
DB_USERNAME=root
DB_PASSWORD=root
```

## Run Tests

```bash
mvn clean test
```

Tests use an in-memory H2 database in MySQL compatibility mode.

## Docker

```bash
docker build -t healthcare-claims-api .
docker run -p 8080:8080 \
  -e DB_URL=jdbc:mysql://host.docker.internal:3306/healthcare_claims \
  -e DB_USERNAME=root \
  -e DB_PASSWORD=root \
  healthcare-claims-api
```

## CI/CD

The GitHub Actions workflow in `.github/workflows/java-ci-cd.yml` runs on pushes to `main` and `develop`.

Pipeline steps:

1. Checkout code
2. Set up Java 17
3. Run tests
4. Package the application
5. Build a Docker image tagged with the commit SHA

For a real deployment flow, the same pipeline can be extended with separate `dev`, `qa`, and `prod` jobs using environment approvals and different database/deployment secrets.

## Interview Explanation

I built a Spring Boot healthcare claims API with MySQL integration, REST endpoints, SQL-based reporting, validation, exception handling, and a CI/CD pipeline. The application exposes endpoints for patient lookup, claim creation, claim status updates, and dashboard aggregation. The claim summary uses a join between patients and claims and groups results by claim status, which mirrors how backend data is prepared for UI dashboards. The GitHub Actions pipeline validates the code, runs tests, packages the JAR, and builds a Docker image for deployment.
