# Post Anything Backend

## Tech Stack
- Java 17+, Spring Boot 3.2+, Spring Data JPA, Spring Security 6
- H2 in-memory DB (console enabled)
- Lombok, MapStruct (optional mapper spot), SpringDoc OpenAPI
- Tests: JUnit 5, Mockito

## Run
```bash
mvn clean install
mvn spring-boot:run
```
Server: http://localhost:8080

## Useful URLs
- Swagger UI: http://localhost:8080/swagger-ui.html
- API docs: http://localhost:8080/v3/api-docs
- H2 console: http://localhost:8080/h2-console (JDBC: `jdbc:h2:mem:postdb`, user `sa`, password empty)

## Profiles
- Default `dev` activated via `application.yml` -> `application-dev.yml`

## Auth & Security
- Basic Auth enforced; JWT flag `spring.security.jwt.enabled` (off by default)
- Roles: USER, ADMIN
- CORS allows http://localhost:4200 with credentials
- BCrypt strength 12 encoder

### Default Users
- admin / admin123 (ADMIN)
- user1 / user123 (USER)
- user2 / user123 (USER)

## Validation & Business Rules
- PostRequest: title 5-200, description 10-5000, type required, priority optional, attachmentUrl valid URL if set
- Status transitions enforced per role and lifecycle; admin override any->CLOSED
- Visibility: users see own + others not in DRAFT; admins see all
- Responses: `ApiResponse<T>` success wrapper; errors via `ApiError` with details

## Seeding
- `src/main/resources/data.sql` loads users/posts at startup

## Testing
```bash
mvn test
```

## Project Layout
- `config/` security, CORS, OpenAPI
- `controller/` REST endpoints (auth, posts, admin)
- `service/` business logic + permission checks
- `repository/` JPA repos
- `dto/` request/response wrappers
- `exception/` custom exceptions + global handler
- `entity/` JPA entities
- `enums/` role/status/type/priority enums (EXTENSIBILITY noted)
