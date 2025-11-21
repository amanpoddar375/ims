# Post Anything / Issue Management System

## Prerequisites
- Java 17+
- Maven 3.9+
- Node 18+ with npm (Angular CLI 17 compatible)
- Ports: 8080 (backend), 4200 (frontend)

## Backend Setup
```bash
cd backend
mvn clean install
mvn spring-boot:run
```
- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI: http://localhost:8080/v3/api-docs
- H2 console: http://localhost:8080/h2-console (URL `jdbc:h2:mem:postdb`, user `sa`, empty password)

### Default Users
- admin / admin123 (ADMIN)
- user1 / user123 (USER)
- user2 / user123 (USER)

## Frontend Setup
```bash
cd frontend
npm install
npm start
```
App runs at http://localhost:4200

## API Overview
- Auth: `POST /api/auth/login`, `POST /api/auth/register`, `GET /api/auth/me`
- Posts: `GET /api/posts`, `GET /api/posts/{id}`, `POST /api/posts`, `PUT /api/posts/{id}`, `DELETE /api/posts/{id}`, `PATCH /api/posts/{id}/status`
- Admin: `GET /api/admin/posts`, `GET /api/admin/users`, `PATCH /api/admin/posts/{id}/assign?assigneeId=`, `GET /api/admin/stats`
- Pagination defaults: `page=0`, `size=20`, ordering `createdAt,desc`

## Security
- Basic Auth enforced (JWT flag available via `spring.security.jwt.enabled`).
- Roles: USER and ADMIN. // SECURITY: admin-only routes mounted under `/api/admin/**` guarded via method security in controller and service.
- CORS allows `http://localhost:4200` with credentials.
- BCrypt strength 12 for password encoding; seed data pre-hashed.

## Validation & Business Rules
- PostRequest: title 5-200 chars; description 10-5000; type required; priority optional; attachmentUrl must be URL when provided.
- Status transitions: USER can DRAFT->SUBMITTED, REJECTED->DRAFT (own), RESOLVED->CLOSED (own); ADMIN can perform all plus override any->CLOSED and assignment.
- Visibility: users see own posts plus all non-DRAFT posts from others; admins see all.
- Error format: `ApiError` with status/error/message/details/path/timestamp.

## Testing
```bash
cd backend
mvn test
```
(Frontend karma tests available via `npm test`).

## Notes
- Seed data in `backend/src/main/resources/data.sql` includes 5 sample posts across statuses.
- Attachment URL validation uses `@URL` constraint.
- Enums are documented with `// EXTENSIBILITY` comments for future-safe additions.
