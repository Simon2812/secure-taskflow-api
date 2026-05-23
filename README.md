# Secure TaskFlow API

Secure TaskFlow is a production-style Spring Boot REST API for collaborative task and project management.  
The system supports authentication, role-based authorization, team and project management, task workflows, comments, and audit logging.

The project was designed to demonstrate backend engineering practices commonly used in real enterprise applications, including layered architecture, JWT security, relational database modeling, API validation, OpenAPI documentation, integration testing, and containerized local development.

## Features

- JWT authentication and stateless authorization
- Role-based access control (ADMIN, MANAGER, MEMBER)
- Team and project management
- Task creation and workflow status transitions
- Task comments and collaboration
- Audit logging for sensitive operations
- PostgreSQL persistence with Spring Data JPA
- Swagger/OpenAPI documentation
- Docker Compose local infrastructure
- Integration and API testing

---

## Architecture Overview

The API follows a layered Spring Boot architecture:

```text
Controller Layer
    ↓
Service Layer
    ↓
Repository Layer
    ↓
PostgreSQL Database
```

Security is implemented using Spring Security with JWT-based authentication filters and role-aware endpoint protection.

---

## Technology Stack

```text
Java 17
Spring Boot 3
Spring Security
JWT Authentication
Spring Data JPA
PostgreSQL
Docker Compose
Swagger / OpenAPI
JUnit
MockMvc
H2 Integration Testing
```

Keywords: java, spring boot, spring security, jwt, rest api, postgresql, docker, swagger, backend

---

## Requirements

- Java 17
- Maven 3.9+ or included `./mvnw`
- Docker Desktop

---

## Run Locally

Start PostgreSQL:

```bash
docker compose up -d postgres
```

Run the API:

```bash
./mvnw spring-boot:run
```

Swagger UI:

```text
http://localhost:8080/swagger-ui.html
```

Run tests:

```bash
./mvnw test
```

---

## API Workflow

Typical application flow:

1. User registers and authenticates
2. JWT token is returned
3. Authorized users create teams and projects
4. Team members create and update tasks
5. Comments and audit logs are persisted
6. Admin users inspect audit history

---

## Main Endpoints

| Area | Endpoints |
|---|---|
| Auth | `POST /api/auth/register`, `POST /api/auth/login` |
| Users | `GET /api/users/me`, `GET /api/users`, `PATCH /api/users/{userId}/role` |
| Teams | `POST /api/teams`, `GET /api/teams`, `POST /api/teams/{teamId}/members` |
| Projects | `POST /api/projects`, `GET /api/projects`, `PUT /api/projects/{projectId}` |
| Tasks | `POST /api/tasks`, `GET /api/tasks`, `PATCH /api/tasks/{taskId}/status` |
| Comments | `POST /api/tasks/{taskId}/comments` |
| Audit | `GET /api/audit-logs` |

---

## Example Requests

Register a user:

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "fullName":"Maya Cohen",
    "email":"maya.cohen@example.com",
    "password":"password123",
    "role":"ADMIN"
  }'
```

Create a team:

```bash
curl -X POST http://localhost:8080/api/teams \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name":"Platform Operations",
    "description":"Owns workflow service delivery"
  }'
```

Additional example flows are available in `requests.http`.

---

## Screenshots

### Swagger UI

![Swagger UI](docs/screenshots/swagger-ui.png)

### Authentication Response

![Authentication Response](docs/screenshots/auth-response.png)

### Create Team

![Create Team](docs/screenshots/create-team.png)

### Task Status Update

![Task Status Update](docs/screenshots/task-status-update.png)

### Audit Logs

![Audit Logs](docs/screenshots/audit-logs.png)

These screenshots were captured from the running Spring Boot API using real authenticated requests and persisted PostgreSQL data.

---

## Project Structure

```text
src/main/java/...     Application source code
src/test/java/...     Integration and unit tests
docs/screenshots/     README screenshots
requests.http         Example API requests
docker-compose.yml    Local PostgreSQL environment
```

---

## Security Notes

- Passwords are hashed using Spring Security password encoders
- JWT tokens secure protected endpoints
- Role-based authorization is enforced at the API layer
- Audit logs track privileged operations
- Validation prevents malformed request payloads

---

## Future Improvements

- Refresh token support
- Redis caching
- Rate limiting
- Pagination and filtering
- WebSocket notifications
- CI/CD deployment pipeline
- Kubernetes deployment manifests
- Observability with Prometheus and Grafana
