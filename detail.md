# Healthcare Claims Tracker - Detailed Explanation

This document explains the project code-wise, file-wise, and concept-wise. Use it to understand the app and to prepare for interview explanations.

## 1. Project Overview

This project is a mini healthcare claims tracker built with:

- Java
- Spring Boot
- Spring Web
- Spring Data JPA
- MySQL
- H2 for local/test mode
- HTML, CSS, JavaScript UI
- Maven
- Docker
- GitHub Actions CI/CD

The application manages:

- Patients
- Claims for each patient
- Claim status updates
- Dashboard claim summary

It has both:

- Backend REST APIs
- A simple browser UI served by Spring Boot

## 2. Main Features

Implemented features:

- View all patients
- Add a new patient
- View claims for a patient
- Create a new claim
- Update claim status
- View claim summary grouped by status
- Validate API requests
- Return structured error responses
- Seed sample data on startup
- Run tests with H2 database
- Build Docker image
- Run GitHub Actions pipeline

## 3. Folder Structure

Main project structure:

```text
.
├── .github/workflows/java-ci-cd.yml
├── Dockerfile
├── README.md
├── detail.md
├── pom.xml
├── src
│   ├── main
│   │   ├── java/com/healthcare/claims
│   │   │   ├── config
│   │   │   ├── controller
│   │   │   ├── domain
│   │   │   ├── dto
│   │   │   ├── exception
│   │   │   ├── repository
│   │   │   ├── service
│   │   │   └── HealthcareClaimsApiApplication.java
│   │   └── resources
│   │       ├── application.properties
│   │       ├── application-local.properties
│   │       └── static
│   │           ├── index.html
│   │           ├── styles.css
│   │           └── app.js
│   └── test
│       ├── java/com/healthcare/claims/HealthcareClaimsApiApplicationTests.java
│       └── resources/application.properties
```

## 4. Maven File

File:

```text
pom.xml
```

This is the Maven configuration file. Maven uses it to know:

- Project name
- Java version
- Spring Boot version
- Dependencies
- Build plugins

Important dependencies:

```xml
spring-boot-starter-web
```

Used to build REST APIs and serve the web UI.

```xml
spring-boot-starter-data-jpa
```

Used for database access through Spring Data JPA and Hibernate.

```xml
spring-boot-starter-validation
```

Used for request validation annotations like `@NotBlank`, `@NotNull`, `@Min`, and `@DecimalMin`.

```xml
mysql-connector-j
```

Used to connect Spring Boot to MySQL.

```xml
h2
```

Used as an in-memory database for local quick running and tests.

```xml
spring-boot-starter-test
```

Used for writing automated tests with JUnit, Spring Test, and MockMvc.

## 5. Application Entry Point

File:

```text
src/main/java/com/healthcare/claims/HealthcareClaimsApiApplication.java
```

Code concept:

```java
@SpringBootApplication
public class HealthcareClaimsApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(HealthcareClaimsApiApplication.class, args);
    }
}
```

This is the starting point of the Spring Boot application.

`@SpringBootApplication` combines:

- `@Configuration`
- `@EnableAutoConfiguration`
- `@ComponentScan`

Meaning:

- Spring reads configuration.
- Spring automatically configures things like Tomcat, JSON, JPA, and validation.
- Spring scans packages for controllers, services, repositories, and components.

## 6. Configuration Files

### Main MySQL Configuration

File:

```text
src/main/resources/application.properties
```

This is the default configuration.

It connects to MySQL:

```properties
spring.datasource.url=${DB_URL:jdbc:mysql://localhost:3306/healthcare_claims?...}
spring.datasource.username=${DB_USERNAME:root}
spring.datasource.password=${DB_PASSWORD:root}
```

Meaning:

- If environment variable `DB_URL` exists, use it.
- Otherwise use the default MySQL URL.
- Same for username and password.

Important property:

```properties
spring.jpa.hibernate.ddl-auto=update
```

This tells Hibernate to update database tables based on entity classes.

For learning projects this is fine. In real production projects, teams usually use migration tools like Flyway or Liquibase.

### Local H2 Configuration

File:

```text
src/main/resources/application-local.properties
```

This is used when running:

```powershell
mvn spring-boot:run "-Dspring-boot.run.profiles=local"
```

It uses H2 in-memory database:

```properties
spring.datasource.url=jdbc:h2:mem:claims_local
```

Why this exists:

- You can run the project without installing or starting MySQL.
- Data is temporary and resets when the app restarts.

### Test Configuration

File:

```text
src/test/resources/application.properties
```

Tests use H2:

```properties
spring.datasource.url=jdbc:h2:mem:claims_test
```

Why:

- Tests should be fast.
- Tests should not depend on your local MySQL.
- Tests should not modify real data.

## 7. Domain Layer

Package:

```text
src/main/java/com/healthcare/claims/domain
```

The domain layer contains database entity classes and enums.

### Patient Entity

File:

```text
Patient.java
```

This class maps to the `patients` table.

Important annotations:

```java
@Entity
@Table(name = "patients")
```

Meaning:

- `@Entity` tells JPA this class is a database entity.
- `@Table` says the database table name is `patients`.

Fields:

```java
id
name
age
status
claims
```

Primary key:

```java
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;
```

Meaning:

- `id` is the primary key.
- Database auto-generates the value.

Status enum mapping:

```java
@Enumerated(EnumType.STRING)
private PatientStatus status;
```

Meaning:

- Store enum value as text like `ACTIVE`.
- This is better than storing ordinal numbers like `0` or `1`.

Relationship:

```java
@OneToMany(mappedBy = "patient")
private List<Claim> claims;
```

Meaning:

- One patient can have many claims.
- The `Claim` entity owns the relationship using its `patient` field.

### Claim Entity

File:

```text
Claim.java
```

This class maps to the `claims` table.

Fields:

```java
id
patient
claimAmount
claimStatus
updatedAt
```

Relationship:

```java
@ManyToOne(fetch = FetchType.LAZY, optional = false)
@JoinColumn(name = "patient_id", nullable = false)
private Patient patient;
```

Meaning:

- Many claims belong to one patient.
- `patient_id` is the foreign key column in the `claims` table.
- `FetchType.LAZY` means patient details are loaded only when needed.

Money field:

```java
private BigDecimal claimAmount;
```

Why `BigDecimal`:

- Money should not use `double` because floating point values can cause precision errors.

Status update:

```java
public void updateStatus(ClaimStatus claimStatus) {
    this.claimStatus = claimStatus;
    this.updatedAt = LocalDateTime.now();
}
```

This method updates the claim status and refreshes `updatedAt`.

### Enums

Files:

```text
PatientStatus.java
ClaimStatus.java
```

`PatientStatus`:

```java
ACTIVE
INACTIVE
```

`ClaimStatus`:

```java
SUBMITTED
IN_REVIEW
APPROVED
REJECTED
PAID
```

Why enums:

- Prevent invalid strings.
- Make allowed statuses clear.
- Improve type safety.

## 8. DTO Layer

Package:

```text
src/main/java/com/healthcare/claims/dto
```

DTO means Data Transfer Object.

DTOs are used to separate API input/output from database entities.

Why use DTOs:

- Avoid exposing database entities directly.
- Control exactly what API receives and returns.
- Add validation rules on requests.
- Keep API contract clean.

### CreatePatientRequest

File:

```text
CreatePatientRequest.java
```

Used by:

```http
POST /patients
```

Fields:

```java
name
age
status
```

Validation:

```java
@NotBlank
@NotNull
@Min
@Max
```

Meaning:

- Name cannot be empty.
- Age is required.
- Age cannot be negative.
- Age cannot be unrealistic.
- Status is required.

### PatientResponse

File:

```text
PatientResponse.java
```

Used when returning patient data to UI/API consumers.

Fields:

```java
id
name
age
status
```

It has:

```java
public static PatientResponse from(Patient patient)
```

This converts a `Patient` entity into a response DTO.

### CreateClaimRequest

File:

```text
CreateClaimRequest.java
```

Used by:

```http
POST /claims
```

Fields:

```java
patientId
claimAmount
```

Validation:

```java
@NotNull
@DecimalMin("0.01")
```

Meaning:

- Patient id is required.
- Claim amount must be greater than zero.

### UpdateClaimStatusRequest

File:

```text
UpdateClaimStatusRequest.java
```

Used by:

```http
PUT /claims/{id}/status
```

Field:

```java
claimStatus
```

Validation:

```java
@NotNull
```

### ClaimResponse

File:

```text
ClaimResponse.java
```

Returned to the frontend when claims are viewed, created, or updated.

Fields:

```java
id
patientId
patientName
claimAmount
claimStatus
updatedAt
```

### ClaimSummaryResponse

File:

```text
ClaimSummaryResponse.java
```

Returned by:

```http
GET /dashboard/claims-summary
```

Fields:

```java
claimStatus
totalClaims
totalClaimAmount
uniquePatients
```

This DTO represents dashboard data.

## 9. Repository Layer

Package:

```text
src/main/java/com/healthcare/claims/repository
```

Repositories talk to the database.

Spring Data JPA automatically creates implementations for interfaces that extend `JpaRepository`.

### PatientRepository

File:

```text
PatientRepository.java
```

Code concept:

```java
public interface PatientRepository extends JpaRepository<Patient, Long> {
}
```

This automatically provides:

- `findAll()`
- `findById()`
- `save()`
- `deleteById()`
- `existsById()`
- `count()`

### ClaimRepository

File:

```text
ClaimRepository.java
```

Method:

```java
List<Claim> findByPatientIdOrderByUpdatedAtDesc(Long patientId);
```

Spring Data JPA reads the method name and creates the query.

Meaning:

- Find claims where patient id matches.
- Sort newest first by `updatedAt`.

Custom query:

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
List<ClaimSummaryResponse> summarizeClaimsByStatus();
```

This is JPQL, not raw SQL.

It does:

- Join claims with patients.
- Group records by claim status.
- Count total claims.
- Sum claim amounts.
- Count unique patients.
- Return results directly into `ClaimSummaryResponse`.

Interview explanation:

> I used a JPQL aggregation query to produce dashboard data grouped by claim status. It joins claims to patients, counts claims, sums claim amounts, and counts distinct patients.

## 10. Service Layer

Package:

```text
src/main/java/com/healthcare/claims/service
```

Services contain business logic.

Controllers should not directly perform database operations.

### PatientService

File:

```text
PatientService.java
```

Methods:

```java
getPatients()
createPatient(CreatePatientRequest request)
```

`getPatients()`:

- Gets all patients from the database.
- Converts entities to DTOs.

`createPatient()`:

- Receives validated request.
- Trims patient name.
- Creates a `Patient` entity.
- Saves it using `PatientRepository`.
- Returns `PatientResponse`.

Transaction concept:

```java
@Transactional
```

Used when writing data.

```java
@Transactional(readOnly = true)
```

Used when only reading data.

Why transactions:

- They keep database operations consistent.
- If something fails during a write, the operation can roll back.

### ClaimService

File:

```text
ClaimService.java
```

Methods:

```java
getClaimsByPatient(Long patientId)
createClaim(CreateClaimRequest request)
updateClaimStatus(Long claimId, UpdateClaimStatusRequest request)
getClaimSummary()
```

`getClaimsByPatient()`:

- Checks if patient exists.
- Throws `ResourceNotFoundException` if not.
- Returns claims for that patient.

`createClaim()`:

- Looks up patient.
- Creates claim with default status `SUBMITTED`.
- Saves claim.

`updateClaimStatus()`:

- Looks up claim.
- Updates claim status.
- Updates `updatedAt`.

`getClaimSummary()`:

- Calls repository aggregation query.

## 11. Controller Layer

Package:

```text
src/main/java/com/healthcare/claims/controller
```

Controllers expose REST APIs.

They receive HTTP requests, call services, and return responses.

### PatientController

File:

```text
PatientController.java
```

Base path:

```java
@RequestMapping("/patients")
```

APIs:

```http
GET /patients
POST /patients
GET /patients/{id}/claims
```

`GET /patients`:

- Returns all patients.

`POST /patients`:

- Creates a patient.
- Uses `@Valid` to trigger validation.
- Returns HTTP `201 Created`.

`GET /patients/{id}/claims`:

- Returns all claims for one patient.

Important annotations:

```java
@RestController
```

Means:

- This class handles REST requests.
- Return values are automatically converted to JSON.

```java
@RequestBody
```

Means:

- Read JSON body from request.

```java
@Valid
```

Means:

- Run validation rules from DTO.

```java
@ResponseStatus(HttpStatus.CREATED)
```

Means:

- Return HTTP status code `201`.

### ClaimController

File:

```text
ClaimController.java
```

Base path:

```java
@RequestMapping("/claims")
```

APIs:

```http
POST /claims
PUT /claims/{id}/status
```

`POST /claims`:

- Creates a new claim.
- New claims start as `SUBMITTED`.

`PUT /claims/{id}/status`:

- Updates claim status.

### DashboardController

File:

```text
DashboardController.java
```

API:

```http
GET /dashboard/claims-summary
```

Returns grouped claim summary for the dashboard.

## 12. Exception Handling

Package:

```text
src/main/java/com/healthcare/claims/exception
```

### ResourceNotFoundException

File:

```text
ResourceNotFoundException.java
```

Custom exception used when patient or claim is not found.

Example:

```java
throw new ResourceNotFoundException("Patient not found with id " + patientId);
```

### ApiErrorResponse

File:

```text
ApiErrorResponse.java
```

This defines the structure of error JSON.

Fields:

```java
timestamp
status
error
message
path
validationErrors
```

### GlobalExceptionHandler

File:

```text
GlobalExceptionHandler.java
```

Annotation:

```java
@RestControllerAdvice
```

Meaning:

- This class handles exceptions globally across controllers.

Handles:

- `ResourceNotFoundException`
- `MethodArgumentNotValidException`

When validation fails, response looks like:

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Request validation failed",
  "validationErrors": {
    "name": "name is required",
    "age": "age cannot be negative"
  }
}
```

Interview explanation:

> I added centralized exception handling using `@RestControllerAdvice`, so controllers remain clean and all validation/not-found errors return a consistent JSON structure.

## 13. Data Seeding

File:

```text
src/main/java/com/healthcare/claims/config/DataSeeder.java
```

This class inserts sample patients and claims when the application starts.

It implements:

```java
CommandLineRunner
```

Meaning:

- Spring Boot runs this code after the application starts.

It checks:

```java
if (patientRepository.count() > 0) {
    return;
}
```

Why:

- Avoid inserting duplicate sample data every time the app starts.

Sample patients:

- Maya Patel
- Daniel Smith
- Sofia Garcia

Sample claims:

- Submitted
- Approved
- In review
- Rejected

## 14. Frontend UI

Folder:

```text
src/main/resources/static
```

Spring Boot automatically serves files from this folder.

That is why:

```text
http://localhost:8080/
```

loads:

```text
static/index.html
```

### index.html

File:

```text
src/main/resources/static/index.html
```

This defines the page structure:

- Header
- Claims summary section
- Patients table
- Patient claims panel
- Create Patient form
- Create Claim form

Important form:

```html
<form id="patientForm">
```

This is used by JavaScript to add a new patient.

### styles.css

File:

```text
src/main/resources/static/styles.css
```

This controls the visual design:

- Layout
- Panels
- Tables
- Buttons
- Status badges
- Forms
- Responsive mobile layout

### app.js

File:

```text
src/main/resources/static/app.js
```

This is the frontend logic.

Important API helper:

```javascript
const api = async (url, options = {}) => {
    const response = await fetch(url, ...);
}
```

This function calls backend endpoints.

Important functions:

```javascript
loadDashboard()
renderSummary()
renderPatients()
renderPatientOptions()
loadClaims()
updateClaimStatus()
```

Patient creation:

```javascript
document.getElementById("patientForm").addEventListener("submit", async (event) => {
    ...
    await api("/patients", {
        method: "POST",
        body: JSON.stringify({ name, age, status })
    });
});
```

Meaning:

- User fills form.
- JavaScript reads values.
- Sends `POST /patients`.
- Refreshes dashboard.
- New patient appears in table and claim dropdown.

Claim creation:

```javascript
await api("/claims", {
    method: "POST",
    body: JSON.stringify({ patientId, claimAmount })
});
```

Claim status update:

```javascript
await api(`/claims/${claimId}/status`, {
    method: "PUT",
    body: JSON.stringify({ claimStatus })
});
```

## 15. API Flow Examples

### Add Patient Flow

```text
Browser form
    ↓
app.js sends POST /patients
    ↓
PatientController.createPatient()
    ↓
@Valid validates CreatePatientRequest
    ↓
PatientService.createPatient()
    ↓
PatientRepository.save()
    ↓
Database inserts row into patients table
    ↓
PatientResponse returned as JSON
    ↓
UI refreshes patient table
```

### Create Claim Flow

```text
Browser form
    ↓
app.js sends POST /claims
    ↓
ClaimController.createClaim()
    ↓
ClaimService.createClaim()
    ↓
PatientRepository.findById()
    ↓
ClaimRepository.save()
    ↓
Database inserts row into claims table
    ↓
ClaimResponse returned
```

### Update Claim Status Flow

```text
User changes status dropdown
    ↓
app.js sends PUT /claims/{id}/status
    ↓
ClaimController.updateClaimStatus()
    ↓
ClaimService.updateClaimStatus()
    ↓
Claim entity updates claimStatus and updatedAt
    ↓
Transaction commits
    ↓
Dashboard refreshes
```

### Dashboard Summary Flow

```text
Browser calls GET /dashboard/claims-summary
    ↓
DashboardController
    ↓
ClaimService.getClaimSummary()
    ↓
ClaimRepository.summarizeClaimsByStatus()
    ↓
JPQL join + group by + sum + count
    ↓
ClaimSummaryResponse list
    ↓
Summary cards displayed in UI
```

## 16. Database Tables

### patients

Columns:

```text
id
name
age
status
```

Example row:

```text
1 | Maya Patel | 34 | ACTIVE
```

### claims

Columns:

```text
id
patient_id
claim_amount
claim_status
updated_at
```

Example row:

```text
1 | 1 | 1250.75 | SUBMITTED | 2026-06-08 10:00:00
```

Relationship:

```text
patients.id = claims.patient_id
```

This is a one-to-many relationship:

- One patient can have many claims.
- One claim belongs to one patient.

## 17. Testing

File:

```text
src/test/java/com/healthcare/claims/HealthcareClaimsApiApplicationTests.java
```

Testing tools:

- JUnit
- Spring Boot Test
- MockMvc
- H2 database

Annotation:

```java
@SpringBootTest
```

Starts the Spring application context for testing.

Annotation:

```java
@AutoConfigureMockMvc
```

Allows testing APIs without manually starting a browser or HTTP server.

Tests included:

- List seeded patients
- Create patient
- Validate patient creation
- Create claim and update status
- Return claim summary
- Validate claim creation

Run tests:

```powershell
mvn test
```

or:

```powershell
mvn clean test
```

Note:

If the app is running and writing logs under `target`, Windows may block `mvn clean test`. In that case stop the app or run:

```powershell
mvn test
```

## 18. Docker

File:

```text
Dockerfile
```

Purpose:

- Package the app into a container image.
- Makes deployment consistent across machines.

Build stage:

```dockerfile
FROM maven:3.9.9-eclipse-temurin-17 AS build
```

Uses Maven and Java to build the JAR.

Runtime stage:

```dockerfile
FROM eclipse-temurin:17-jre
```

Runs only the built JAR with a smaller Java runtime.

Command:

```dockerfile
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Build Docker image:

```powershell
docker build -t healthcare-claims-api .
```

Run Docker image:

```powershell
docker run -p 8080:8080 healthcare-claims-api
```

## 19. CI/CD Pipeline

File:

```text
.github/workflows/java-ci-cd.yml
```

This is the GitHub Actions workflow.

Trigger:

```yaml
on:
  push:
    branches: [ main, develop ]
```

Meaning:

- Pipeline runs whenever code is pushed to `main` or `develop`.

Pipeline steps:

```text
Checkout code
Set up Java
Run tests
Package app
Build Docker image
```

Important commands:

```yaml
run: mvn clean test
```

Runs automated tests.

```yaml
run: mvn clean package -DskipTests
```

Packages the Spring Boot app as a JAR.

```yaml
run: docker build -t healthcare-claims-api:${{ github.sha }} .
```

Builds Docker image with commit SHA as tag.

Interview explanation:

> I configured a GitHub Actions CI/CD workflow that runs on pushes to main and develop. It checks out the code, sets up Java 17, runs the test suite, packages the Spring Boot app, and builds a Docker image tagged with the Git commit SHA. This gives fast feedback and prepares the app for deployment.

## 20. How To Run The App

### Quick Local Run With H2

Use this when you do not want to start MySQL:

```powershell
mvn spring-boot:run "-Dspring-boot.run.profiles=local"
```

Open:

```text
http://localhost:8080/
```

### Run With MySQL

Start MySQL first.

Then run:

```powershell
mvn spring-boot:run
```

Default MySQL values:

```text
Database: healthcare_claims
Username: root
Password: root
```

## 21. API Endpoints

### Patients

```http
GET /patients
```

Lists all patients.

```http
POST /patients
```

Creates a new patient.

Request:

```json
{
  "name": "John Doe",
  "age": 45,
  "status": "ACTIVE"
}
```

```http
GET /patients/{id}/claims
```

Gets all claims for a patient.

### Claims

```http
POST /claims
```

Creates a claim.

Request:

```json
{
  "patientId": 1,
  "claimAmount": 1250.75
}
```

```http
PUT /claims/{id}/status
```

Updates a claim status.

Request:

```json
{
  "claimStatus": "APPROVED"
}
```

### Dashboard

```http
GET /dashboard/claims-summary
```

Returns grouped claim summary.

## 22. Important Technical Concepts

### REST API

REST APIs expose resources through HTTP.

In this project:

- Patients are resources.
- Claims are resources.
- Dashboard summary is a reporting resource.

HTTP methods:

- `GET` reads data.
- `POST` creates data.
- `PUT` updates data.

### Layered Architecture

The app uses layers:

```text
Controller → Service → Repository → Database
```

Why:

- Easier to understand.
- Easier to test.
- Business logic does not sit inside controllers.
- Database code does not leak into UI code.

### JPA and Hibernate

JPA is the Java specification for ORM.

Hibernate is the implementation used by Spring Boot.

ORM means Object Relational Mapping:

- Java class maps to database table.
- Java object maps to database row.

Example:

```java
Patient
```

maps to:

```text
patients table
```

### Validation

Validation protects the API from bad input.

Example:

```java
@NotBlank
@Min
@DecimalMin
```

If validation fails, Spring throws `MethodArgumentNotValidException`, and the global exception handler returns a structured `400 Bad Request`.

### Transactions

Transactions keep database operations consistent.

Example:

```java
@Transactional
```

If an operation fails, changes can be rolled back.

### DTO Mapping

The app converts:

```text
Entity → Response DTO
Request DTO → Entity
```

This avoids exposing internal database classes directly through APIs.

### SQL Aggregation

The dashboard summary is based on aggregation:

- `count`
- `sum`
- `group by`
- `count distinct`

This is important because dashboards usually need summarized data, not raw rows.

### H2 vs MySQL

H2:

- Lightweight
- In-memory
- Good for local/testing

MySQL:

- Real database
- Persistent
- Better for production-like use

## 23. Interview Talking Points

You can say:

> I built a Spring Boot healthcare claims tracker with REST APIs, JPA entities, MySQL integration, request validation, centralized exception handling, SQL-style dashboard aggregation, and a basic UI. The backend follows a layered architecture with controllers, services, repositories, DTOs, and entities.

You can also say:

> I added patient creation by creating a request DTO with validation, adding service-layer logic to save the patient, exposing a `POST /patients` endpoint, connecting the frontend form to that endpoint, and adding tests for success and validation cases.

For CI/CD:

> I added a GitHub Actions pipeline that runs tests, packages the application, and builds a Docker image on pushes to main and develop.

For SQL/reporting:

> The dashboard summary uses a JPQL query with join, group by, count, sum, and distinct count to prepare backend data for the UI.

## 24. Common Debugging Notes

### Raw JSON Instead Of Website

If you open:

```text
http://localhost:8080/patients
```

you will see JSON because that is an API endpoint.

For the website, open:

```text
http://localhost:8080/
```

### MySQL Connection Error

If you see:

```text
Communications link failure
Connection refused
```

it means MySQL is not running or credentials are wrong.

Use local profile:

```powershell
mvn spring-boot:run "-Dspring-boot.run.profiles=local"
```

### PowerShell Maven Property Error

If PowerShell complains about:

```text
Unknown lifecycle phase ".run.profiles=local"
```

wrap the Maven property in quotes:

```powershell
mvn spring-boot:run "-Dspring-boot.run.profiles=local"
```

### UI Not Updating

If the website does not show latest changes:

1. Stop the running app with `Ctrl + C`.
2. Restart it.
3. Hard refresh the browser.

## 25. What To Add Next

Good next features:

- Edit patient details
- Delete patient
- Claim notes/history table
- Search patients
- Filter claims by status
- Add pagination
- Add React frontend
- Push Docker image to Docker Hub or Azure Container Registry
- Add separate dev, QA, prod deployment jobs

