# Healthcare Claims Tracker

Healthcare Claims Tracker is a Java Spring Boot mini application for managing patients, healthcare claims, claim status updates, and dashboard reporting. It includes a simple browser UI, REST APIs, MySQL-ready persistence, H2 local/test mode, validation, exception handling, automated tests, Docker support, and a GitHub Actions CI/CD pipeline.

This README explains the project as a Spring Boot workflow: how a browser action becomes an API request, how the request moves through controller, service, repository, entity, and database layers, and how the response returns to the UI.

## What This App Does

The application supports:

- Viewing all patients
- Adding a new patient
- Viewing claims for a selected patient
- Creating a claim for a patient
- Updating claim status
- Showing dashboard summary cards grouped by claim status
- Running locally with H2 or with MySQL
- Running tests through Maven
- Building a Docker image
- Running CI/CD with GitHub Actions

## Tech Stack

- Java 17+
- Spring Boot 3
- Spring Web
- Spring Data JPA
- Hibernate
- MySQL
- H2 database for local/test profile
- Maven
- HTML, CSS, JavaScript
- Docker
- GitHub Actions

## High-Level Spring Boot Workflow

When the user opens the website:

```text
Browser
  -> Spring Boot static files
  -> index.html, styles.css, app.js
```

When the user performs an action, such as creating a patient:

```text
Browser form
  -> app.js fetch()
  -> REST Controller
  -> Request DTO validation
  -> Service layer
  -> Repository layer
  -> Database
  -> Response DTO
  -> JSON response
  -> UI refresh
```

This is the main backend pattern used throughout the project:

```text
Controller -> Service -> Repository -> Entity -> Database
```

The controller handles HTTP.  
The service handles business logic.  
The repository handles database access.  
The entity maps Java objects to database tables.  
The DTO controls API input and output.

## Project Structure

```text
.
├── .github
│   └── workflows
│       └── java-ci-cd.yml
├── Dockerfile
├── README.md
├── detail.md
├── pom.xml
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com/healthcare/claims
│   │   │       ├── HealthcareClaimsApiApplication.java
│   │   │       ├── config
│   │   │       │   └── DataSeeder.java
│   │   │       ├── controller
│   │   │       │   ├── ClaimController.java
│   │   │       │   ├── DashboardController.java
│   │   │       │   └── PatientController.java
│   │   │       ├── domain
│   │   │       │   ├── Claim.java
│   │   │       │   ├── ClaimStatus.java
│   │   │       │   ├── Patient.java
│   │   │       │   └── PatientStatus.java
│   │   │       ├── dto
│   │   │       │   ├── ClaimResponse.java
│   │   │       │   ├── ClaimSummaryResponse.java
│   │   │       │   ├── CreateClaimRequest.java
│   │   │       │   ├── CreatePatientRequest.java
│   │   │       │   ├── PatientResponse.java
│   │   │       │   └── UpdateClaimStatusRequest.java
│   │   │       ├── exception
│   │   │       │   ├── ApiErrorResponse.java
│   │   │       │   ├── GlobalExceptionHandler.java
│   │   │       │   └── ResourceNotFoundException.java
│   │   │       ├── repository
│   │   │       │   ├── ClaimRepository.java
│   │   │       │   └── PatientRepository.java
│   │   │       └── service
│   │   │           ├── ClaimService.java
│   │   │           └── PatientService.java
│   │   └── resources
│   │       ├── application-local.properties
│   │       ├── application.properties
│   │       └── static
│   │           ├── app.js
│   │           ├── index.html
│   │           └── styles.css
│   └── test
│       ├── java
│       │   └── com/healthcare/claims
│       │       └── HealthcareClaimsApiApplicationTests.java
│       └── resources
│           └── application.properties
```

## Root Files

### `pom.xml`

This is the Maven build file. It defines the application dependencies, Java version, and Spring Boot Maven plugin.

Important dependencies:

- `spring-boot-starter-web`: REST APIs and embedded Tomcat web server
- `spring-boot-starter-data-jpa`: database access using JPA repositories
- `spring-boot-starter-validation`: request validation annotations
- `mysql-connector-j`: MySQL JDBC driver
- `h2`: in-memory database for local/test runs
- `spring-boot-starter-test`: JUnit, MockMvc, Spring test support

Why it matters:

Maven reads this file to download dependencies, compile code, run tests, package the app, and run Spring Boot.

### `Dockerfile`

This builds the Spring Boot app into a Docker image.

It uses two stages:

1. Maven + JDK image to build the JAR
2. JRE image to run the JAR

Why it matters:

Docker makes the app portable. The same image can run locally, in CI/CD, or in a cloud environment.

### `.github/workflows/java-ci-cd.yml`

This is the GitHub Actions pipeline.

It runs when code is pushed to:

```text
main
develop
```

Pipeline workflow:

```text
Checkout code
  -> Set up Java 17
  -> Run tests
  -> Package app
  -> Build Docker image
```

Why it matters:

This gives CI/CD automation. Every push validates the code and prepares the app for deployment.

### `detail.md`

This is the detailed study guide for the project. It explains the files, code concepts, and interview points in more depth.

## Application Startup File

### `HealthcareClaimsApiApplication.java`

This is the main class.

```java
@SpringBootApplication
public class HealthcareClaimsApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(HealthcareClaimsApiApplication.class, args);
    }
}
```

What happens here:

- Spring Boot starts.
- Embedded Tomcat starts.
- Beans are scanned and created.
- Controllers, services, repositories, and configuration classes are registered.
- Static UI files become available from `src/main/resources/static`.

`@SpringBootApplication` includes:

- `@Configuration`
- `@EnableAutoConfiguration`
- `@ComponentScan`

## Configuration Files

### `src/main/resources/application.properties`

This is the default application configuration. It is set up for MySQL.

Main responsibility:

- Configure database URL
- Configure username/password
- Configure Hibernate behavior
- Configure server port

Important lines:

```properties
spring.datasource.url=${DB_URL:jdbc:mysql://localhost:3306/healthcare_claims?...}
spring.datasource.username=${DB_USERNAME:root}
spring.datasource.password=${DB_PASSWORD:root}
spring.jpa.hibernate.ddl-auto=update
```

Why this file exists:

When you run the app normally, Spring Boot reads this file and tries to connect to MySQL.

### `src/main/resources/application-local.properties`

This is the local profile configuration. It runs the app using H2 instead of MySQL.

Run it with:

```powershell
mvn spring-boot:run "-Dspring-boot.run.profiles=local"
```

Why this file exists:

It lets you run the project immediately without installing or starting MySQL.

### `src/test/resources/application.properties`

This configuration is used only during tests.

It uses H2:

```properties
spring.datasource.url=jdbc:h2:mem:claims_test
```

Why this file exists:

Tests should be fast, repeatable, and independent from your real MySQL database.

## Domain Layer

Folder:

```text
src/main/java/com/healthcare/claims/domain
```

The domain layer contains JPA entities and enums. Entities represent database tables.

### `Patient.java`

Represents the `patients` table.

Fields:

- `id`
- `name`
- `age`
- `status`
- `claims`

Important annotations:

```java
@Entity
@Table(name = "patients")
```

These tell Hibernate that this Java class maps to the `patients` database table.

Relationship:

```java
@OneToMany(mappedBy = "patient")
private List<Claim> claims;
```

Meaning:

One patient can have many claims.

### `Claim.java`

Represents the `claims` table.

Fields:

- `id`
- `patient`
- `claimAmount`
- `claimStatus`
- `updatedAt`

Relationship:

```java
@ManyToOne(fetch = FetchType.LAZY, optional = false)
@JoinColumn(name = "patient_id", nullable = false)
private Patient patient;
```

Meaning:

Many claims can belong to one patient. The `claims` table has a `patient_id` foreign key.

Why `BigDecimal` is used:

```java
private BigDecimal claimAmount;
```

Money should use `BigDecimal`, not `double`, because `double` can cause decimal precision issues.

### `PatientStatus.java`

Allowed patient statuses:

```text
ACTIVE
INACTIVE
```

### `ClaimStatus.java`

Allowed claim statuses:

```text
SUBMITTED
IN_REVIEW
APPROVED
REJECTED
PAID
```

Why enums are used:

Enums prevent invalid values and make status handling type-safe.

## DTO Layer

Folder:

```text
src/main/java/com/healthcare/claims/dto
```

DTO means Data Transfer Object.

DTOs define what the API accepts and returns. They protect the entity layer from being exposed directly.

### `CreatePatientRequest.java`

Used by:

```http
POST /patients
```

Fields:

- `name`
- `age`
- `status`

Validation:

```java
@NotBlank
@NotNull
@Min
@Max
```

Workflow:

```text
JSON request body
  -> CreatePatientRequest
  -> validation
  -> PatientService
```

### `PatientResponse.java`

Used when returning patient data.

Fields:

- `id`
- `name`
- `age`
- `status`

It contains a mapper:

```java
public static PatientResponse from(Patient patient)
```

This converts a database entity into API response data.

### `CreateClaimRequest.java`

Used by:

```http
POST /claims
```

Fields:

- `patientId`
- `claimAmount`

Validation:

- `patientId` must exist in request
- `claimAmount` must be greater than `0`

### `UpdateClaimStatusRequest.java`

Used by:

```http
PUT /claims/{id}/status
```

Field:

- `claimStatus`

### `ClaimResponse.java`

Used when returning claim data.

Fields:

- `id`
- `patientId`
- `patientName`
- `claimAmount`
- `claimStatus`
- `updatedAt`

### `ClaimSummaryResponse.java`

Used by dashboard summary.

Fields:

- `claimStatus`
- `totalClaims`
- `totalClaimAmount`
- `uniquePatients`

This DTO is returned directly from the aggregation query.

## Repository Layer

Folder:

```text
src/main/java/com/healthcare/claims/repository
```

Repositories handle database access.

### `PatientRepository.java`

```java
public interface PatientRepository extends JpaRepository<Patient, Long> {
}
```

Because it extends `JpaRepository`, Spring automatically gives methods like:

- `findAll()`
- `findById()`
- `save()`
- `existsById()`
- `count()`

### `ClaimRepository.java`

This repository handles claim queries.

Method:

```java
List<Claim> findByPatientIdOrderByUpdatedAtDesc(Long patientId);
```

Spring Data JPA creates this query from the method name.

Custom dashboard query:

```java
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
```

What it does:

- Joins claims with patients
- Groups claims by status
- Counts claims
- Sums claim amounts
- Counts unique patients

Interview line:

> The dashboard uses a JPQL aggregation query with join, group by, sum, count, and distinct count to prepare summary data for the UI.

## Service Layer

Folder:

```text
src/main/java/com/healthcare/claims/service
```

Services contain business logic. Controllers call services instead of directly calling repositories.

### `PatientService.java`

Methods:

```java
getPatients()
createPatient(CreatePatientRequest request)
```

`getPatients()` workflow:

```text
PatientRepository.findAll()
  -> convert each Patient to PatientResponse
  -> return list
```

`createPatient()` workflow:

```text
CreatePatientRequest
  -> trim name
  -> create Patient entity
  -> patientRepository.save()
  -> PatientResponse
```

Why this belongs in the service:

The service owns business logic and persistence decisions. The controller only handles HTTP.

### `ClaimService.java`

Methods:

```java
getClaimsByPatient(Long patientId)
createClaim(CreateClaimRequest request)
updateClaimStatus(Long claimId, UpdateClaimStatusRequest request)
getClaimSummary()
```

`createClaim()` workflow:

```text
Find patient by id
  -> create Claim with SUBMITTED status
  -> save claim
  -> return ClaimResponse
```

`updateClaimStatus()` workflow:

```text
Find claim by id
  -> update status
  -> update timestamp
  -> transaction commits
  -> return ClaimResponse
```

Transaction annotations:

```java
@Transactional
@Transactional(readOnly = true)
```

Why they matter:

- Write operations need transactions.
- Read-only operations can be optimized.
- Failed write operations can roll back.

## Controller Layer

Folder:

```text
src/main/java/com/healthcare/claims/controller
```

Controllers expose REST endpoints.

### `PatientController.java`

Base path:

```java
@RequestMapping("/patients")
```

Endpoints:

```http
GET /patients
POST /patients
GET /patients/{id}/claims
```

`POST /patients` uses:

```java
@Valid @RequestBody CreatePatientRequest request
```

Meaning:

- Read JSON from request body.
- Convert JSON to Java DTO.
- Validate DTO fields.
- If valid, call service.
- If invalid, return `400 Bad Request`.

### `ClaimController.java`

Base path:

```java
@RequestMapping("/claims")
```

Endpoints:

```http
POST /claims
PUT /claims/{id}/status
```

### `DashboardController.java`

Base path:

```java
@RequestMapping("/dashboard")
```

Endpoint:

```http
GET /dashboard/claims-summary
```

This endpoint returns dashboard summary cards data.

## Exception Layer

Folder:

```text
src/main/java/com/healthcare/claims/exception
```

### `ResourceNotFoundException.java`

Custom runtime exception for missing patient or claim records.

Example:

```java
throw new ResourceNotFoundException("Patient not found with id " + patientId);
```

### `ApiErrorResponse.java`

Defines the JSON structure returned when an error happens.

Fields:

- `timestamp`
- `status`
- `error`
- `message`
- `path`
- `validationErrors`

### `GlobalExceptionHandler.java`

Uses:

```java
@RestControllerAdvice
```

This catches exceptions across all controllers.

It handles:

- Not found errors
- Validation errors

Why this matters:

Controllers stay clean, and API errors return a consistent JSON format.

## Data Seeder

File:

```text
src/main/java/com/healthcare/claims/config/DataSeeder.java
```

This class inserts sample data on startup.

It implements:

```java
CommandLineRunner
```

Workflow:

```text
Application starts
  -> DataSeeder runs
  -> checks if patients already exist
  -> inserts sample patients and claims if database is empty
```

Why this matters:

The UI has usable sample data immediately after startup.

## Frontend Static Files

Folder:

```text
src/main/resources/static
```

Spring Boot automatically serves static files from this folder.

### `index.html`

This is the website structure.

It contains:

- Header
- Claims summary area
- Patients table
- Patient claims section
- Create Patient form
- Create Claim form

Open it through:

```text
http://localhost:8080/
```

### `styles.css`

This controls the visual design:

- Page layout
- Panels
- Tables
- Forms
- Buttons
- Status badges
- Responsive behavior

### `app.js`

This connects the UI to the backend APIs.

Important functions:

- `loadDashboard()`
- `renderSummary()`
- `renderPatients()`
- `renderPatientOptions()`
- `loadClaims()`
- `updateClaimStatus()`

Patient creation UI workflow:

```text
User fills Create Patient form
  -> app.js reads name, age, status
  -> fetch POST /patients
  -> backend saves patient
  -> app.js reloads dashboard
  -> new patient appears in table and claim dropdown
```

Claim creation UI workflow:

```text
User chooses patient and amount
  -> app.js sends POST /claims
  -> backend creates claim
  -> dashboard refreshes
```

Claim status update UI workflow:

```text
User changes status dropdown
  -> app.js sends PUT /claims/{id}/status
  -> backend updates claim
  -> summary cards refresh
```

## Database Tables

### `patients`

| Column | Meaning |
| --- | --- |
| `id` | Primary key |
| `name` | Patient name |
| `age` | Patient age |
| `status` | `ACTIVE` or `INACTIVE` |

### `claims`

| Column | Meaning |
| --- | --- |
| `id` | Primary key |
| `patient_id` | Foreign key to `patients.id` |
| `claim_amount` | Claim amount |
| `claim_status` | Claim lifecycle status |
| `updated_at` | Last status/update timestamp |

Relationship:

```text
patients.id -> claims.patient_id
```

One patient can have many claims.

## REST API Endpoints

### Patients

```http
GET /patients
```

Returns all patients.

```http
POST /patients
Content-Type: application/json

{
  "name": "John Doe",
  "age": 45,
  "status": "ACTIVE"
}
```

Creates a patient.

```http
GET /patients/{id}/claims
```

Returns claims for a patient.

### Claims

```http
POST /claims
Content-Type: application/json

{
  "patientId": 1,
  "claimAmount": 1250.75
}
```

Creates a claim with default status `SUBMITTED`.

```http
PUT /claims/{id}/status
Content-Type: application/json

{
  "claimStatus": "APPROVED"
}
```

Updates claim status.

### Dashboard

```http
GET /dashboard/claims-summary
```

Returns claim summary grouped by claim status.

## Testing

Test file:

```text
src/test/java/com/healthcare/claims/HealthcareClaimsApiApplicationTests.java
```

Testing tools:

- `@SpringBootTest`
- `@AutoConfigureMockMvc`
- MockMvc
- H2 database

Tests cover:

- Listing seeded patients
- Creating a patient
- Validating patient creation
- Creating a claim
- Updating claim status
- Returning dashboard summary
- Validating claim creation

Run tests:

```powershell
mvn test
```

or:

```powershell
mvn clean test
```

If the app is currently running and Windows locks files under `target`, stop the app or run `mvn test` without `clean`.

## Run The Application

### Recommended Local Run With H2

Use this for quick local testing:

```powershell
mvn spring-boot:run "-Dspring-boot.run.profiles=local"
```

Open:

```text
http://localhost:8080/
```

API example:

```text
http://localhost:8080/patients
```

H2 console:

```text
http://localhost:8080/h2-console
```

JDBC URL:

```text
jdbc:h2:mem:claims_local
```

Username:

```text
sa
```

Password:

```text
leave blank
```

### Run With MySQL

Start MySQL first.

Default configuration expects:

```text
Database: healthcare_claims
Username: root
Password: root
```

Run:

```powershell
mvn spring-boot:run
```

You can override values with environment variables:

```text
DB_URL
DB_USERNAME
DB_PASSWORD
```

## Docker

Build the image:

```powershell
docker build -t healthcare-claims-api .
```

Run the image:

```powershell
docker run -p 8080:8080 healthcare-claims-api
```

For MySQL, pass database environment variables when running the container.

## CI/CD Pipeline

Workflow file:

```text
.github/workflows/java-ci-cd.yml
```

The pipeline runs on pushes to:

```text
main
develop
```

Steps:

1. Checkout code
2. Set up Java 17
3. Run `mvn clean test`
4. Run `mvn clean package -DskipTests`
5. Build Docker image

Current pipeline builds the Docker image but does not push it to a registry. A production extension would push to Docker Hub, Azure Container Registry, or AWS ECR, then deploy to dev, QA, and prod.

## How To Push This To GitHub

Initialize Git:

```powershell
git init
git add .
git commit -m "Initial healthcare claims tracker"
```

Create an empty GitHub repository, then connect it:

```powershell
git branch -M main
git remote add origin https://github.com/YOUR_USERNAME/healthcare-claims-api.git
git push -u origin main
```

After pushing, open the GitHub repository and go to:

```text
Actions
```

You should see the `Java CI/CD` workflow running.

## Common Issues

### Browser Shows JSON

If you open:

```text
http://localhost:8080/patients
```

you are opening the API endpoint.

For the website, open:

```text
http://localhost:8080/
```

### MySQL Connection Refused

If you see:

```text
Communications link failure
Connection refused
```

MySQL is not running or credentials are wrong.

Use the local profile:

```powershell
mvn spring-boot:run "-Dspring-boot.run.profiles=local"
```

### PowerShell Maven Profile Error

If PowerShell says:

```text
Unknown lifecycle phase ".run.profiles=local"
```

wrap the Maven property in quotes:

```powershell
mvn spring-boot:run "-Dspring-boot.run.profiles=local"
```

### UI Does Not Show Latest Code

Stop the running app and restart it:

```text
Ctrl + C
```

Then:

```powershell
mvn spring-boot:run "-Dspring-boot.run.profiles=local"
```

Hard refresh the browser.

## Interview Explanation

Short version:

> I built a healthcare claims tracker using Java Spring Boot, MySQL, JPA, REST APIs, validation, exception handling, a simple frontend, tests, Docker, and GitHub Actions CI/CD.

Backend explanation:

> The backend follows a layered Spring Boot architecture. Controllers expose REST endpoints, services contain business logic, repositories handle database operations, entities map to database tables, and DTOs define API input/output contracts.

Patient creation explanation:

> For adding patients, I created a request DTO with validation, added service logic to convert the request into a Patient entity, exposed a `POST /patients` endpoint, connected the frontend form using JavaScript fetch, and added tests for success and validation cases.

Dashboard explanation:

> The dashboard summary uses a JPQL query that joins claims with patients, groups by claim status, counts claims, sums claim amounts, and counts unique patients.

CI/CD explanation:

> The GitHub Actions pipeline runs on pushes to main and develop. It checks out the code, sets up Java, runs tests, packages the Spring Boot app, and builds a Docker image tagged with the commit SHA.

## Suggested Next Improvements

- Edit patient details
- Delete patient
- Add claim history/audit table
- Add search and filters
- Add pagination
- Add authentication
- Replace static UI with React
- Push Docker image to Azure Container Registry
- Add dev, QA, and prod deployment stages

