# Support Ticket Queue System

![Java CI](https://github.com/Stanislav3423/support-ticket-queue/actions/workflows/ci.yml/badge.svg)

A Spring Boot-based REST API for managing customer support tickets with automated SLA calculation and priority-based sorting.
## How to Run

### Run Tests
To execute all unit and integration tests (SLA math and Controller logic):
```bash
./mvnw test
```
### Run Application
To start the server on http://localhost:8080:
```bash
./mvnw spring-boot:run
```
### API Documentation
Interactive Swagger UI is available at:
http://localhost:8080/swagger-ui.html

## Key Decisions & Trade-offs

### SLA Logic (Warsaw Time)
SLA calculation is strictly tied to Europe/Warsaw business hours (09:00 - 17:00). I used ZonedDateTime and a minute-by-minute addition loop to ensure accurate transitions between business days and weekends.

### Multi-Level Sorting
The queue is sorted using a composite approach:
Due Date (primary)
Severity (1-5, where 1 is highest)
Customer Tier (Enterprise > Pro > Free)
Creation Time (tie-breaker)

### CI/CD Integration
Configured GitHub Actions to automate testing on every push to main/master. This ensures that the SLA logic remains stable across changes.

### Global Exception Handling
Implemented a @ControllerAdvice layer to intercept validation errors (e.g., summary exceeding 200 characters or invalid Enum inputs like Severity: 99). This ensures the API always returns structured, consumer-friendly HTTP 400 JSON responses rather than raw server stack traces.

### In-Memory Storage
To meet the "runnable with one command" requirement without a local database, the repository layer uses a ConcurrentHashMap. The application uses a DataLoader to prepopulate the queue from tickets.sample.json

## Assumptions
### Public Holidays
The current business-hour algorithm successfully skips weekends but does not implement a calendar for Polish national holidays. In a production environment, this would require integration with an external holiday API or a dedicated database table.

### Continuous Business Hours
The 09:00 - 17:00 business window is treated as a continuous 8-hour block. Real-world SLA algorithms sometimes pause during scheduled agent lunch breaks.

### Static SLA Rules Matrix
The mapping between CustomerTier, Severity, and the resulting SLA hours is hardcoded as an internal service rule. In a production environment, this would likely be a configurable rules engine stored in a database.

## AI Usage Notes

### What I used AI for
Generating structural boilerplate (DTOs, Controller shells, and Global Exception Handler).
Structuring the OpenAPI/Swagger metadata and documentation annotations.

### One example of AI output I rejected or corrected
AI incorrectly formulated Enum access from service methods, in particular CustomerTier, which broke the sorting logic.

### How I verified correctness
Via code reading and logical reasoning.

Via targeted automated testing. I wrote specific JUnit 5 unit tests (SlaCalculatorServiceTest) to intentionally hit the edge boundaries. 
I also wrote MockMvc integration tests to ensure the queue sorting actually respects the multi-level priority rules under real HTTP request conditions.
