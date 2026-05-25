---
applyTo: "backend/**"
description: "Backend instructions for Spring Boot + JPA + MySQL in this repository. Use when building entities, repositories, services, controllers, and exam/violation workflows."
---

# Backend Copilot Instructions

## Scope
These instructions apply to files under `backend/`.

## Tech Stack
- Spring Boot 4
- Java 25
- Spring Data JPA
- Spring Security
- MySQL 8

## Coding Rules
- Keep a layered design: controller -> service -> repository -> model.
- Use DTOs for API requests/responses instead of exposing entities directly.
- Validate input at the API boundary.
- Prefer constructor injection over field injection.
- Keep business logic in services, not controllers.

## Database and Entity Rules
- Keep JPA mappings aligned with `init-db.sql`.
- Preserve enum compatibility between Java enums and DB enum values.
- Use explicit column constraints for critical fields.
- Avoid eager loading by default on collections.
- Add indexes for query-heavy columns.

## Security and Integrity
- Treat all anti-cheat evidence as append-only audit data.
- Record both client timestamp and server timestamp for violations.
- Validate submission ownership and exam status on every write operation.
- Do not trust client-side timer or violation severity blindly.
- Enforce role-based access for teacher/admin operations.

## API Design Guidelines
- Use clear REST resource naming.
- Keep response payloads stable and explicit.
- Return appropriate HTTP status codes and useful error payloads.
- Use pagination for list endpoints expected to grow.

## Testing Guidelines
For new backend features, prefer adding:
- Repository tests for query behavior
- Service tests for business rules
- Controller/integration tests for key exam lifecycle paths

## Quality Checks
Before finalizing backend changes, prefer running:
- `./mvnw test`
- `./mvnw spring-boot:run` (for local sanity)

## Output Expectations
When generating code:
- Keep methods focused and readable.
- Add concise comments only for non-obvious logic.
- Preserve naming consistency with existing model classes.
