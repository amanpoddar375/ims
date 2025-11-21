# Codex Agent Instructions

## Project Overview

This is a **Post Anything / Issue Management System** with:
- **Backend**: Spring Boot 3 / Spring 6 REST API
- **Frontend**: Angular (latest stable)
- **Database**: H2 (embedded, with console enabled)
- **Authentication**: Basic Auth (JWT behind feature flag)

## Tech Stack Requirements

### Backend
- Java 17+
- Spring Boot 3.2+
- Spring Data JPA
- Spring Security 6
- H2 Database
- Lombok
- MapStruct (optional)
- Jakarta Validation
- SpringDoc OpenAPI (Swagger)
- JUnit 5 + Mockito

### Frontend
- Angular 17+
- Angular Router
- HttpClient with interceptors
- Reactive Forms
- Angular Material (optional)
- RxJS

## Project Structure

```
project-root/
├── backend/
│   └── src/
│       ├── main/
│       │   ├── java/com/postanything/
│       │   │   ├── config/          # Security, OpenAPI, Web configs
│       │   │   ├── controller/      # REST controllers
│       │   │   ├── dto/             # Request/Response DTOs
│       │   │   ├── entity/          # JPA entities
│       │   │   ├── enums/           # Status, PostType, Role enums
│       │   │   ├── exception/       # Custom exceptions + GlobalExceptionHandler
│       │   │   ├── mapper/          # Entity <-> DTO mappers
│       │   │   ├── repository/      # Spring Data repositories
│       │   │   ├── security/        # UserDetailsService, filters
│       │   │   ├── service/         # Business logic services
│       │   │   └── Application.java
│       │   └── resources/
│       │       ├── application.yml
│       │       ├── application-dev.yml
│       │       └── data.sql         # Seed data
│       └── test/
│           └── java/com/postanything/
│               ├── controller/      # Integration tests
│               ├── service/         # Unit tests
│               └── repository/      # Repository tests
├── frontend/
│   └── src/
│       ├── app/
│       │   ├── core/               # Guards, interceptors, services
│       │   ├── shared/             # Shared components, pipes, directives
│       │   ├── features/
│       │   │   ├── auth/           # Login component
│       │   │   ├── posts/          # Post list, detail, create, edit
│       │   │   └── admin/          # Admin dashboard
│       │   ├── models/             # TypeScript interfaces
│       │   └── app.routes.ts
│       ├── environments/
│       └── styles.scss
└── README.md
```

## Domain Model

### Entities

#### User
```
- id: Long (auto-generated)
- username: String (unique, 3-50 chars)
- password: String (BCrypt encoded)
- email: String (unique, valid email)
- role: Role enum (USER, ADMIN)
- enabled: Boolean (default true)
- createdAt: LocalDateTime
- updatedAt: LocalDateTime
```

#### Post
```
- id: Long (auto-generated)
- title: String (required, 5-200 chars)
- description: String (required, 10-5000 chars)
- type: PostType enum (ISSUE, COMPLAINT, SUGGESTION, GENERAL, OTHER)
- status: PostStatus enum (DRAFT, SUBMITTED, UNDER_REVIEW, RESOLVED, CLOSED, REJECTED)
- priority: Priority enum (LOW, MEDIUM, HIGH, CRITICAL) - optional
- attachmentUrl: String (optional, must be valid URL if provided)
- author: User (ManyToOne)
- assignedTo: User (ManyToOne, nullable, ADMIN only)
- createdAt: LocalDateTime
- updatedAt: LocalDateTime
- resolvedAt: LocalDateTime (nullable)
```

#### Comment (optional extension)
```
- id: Long
- content: String (required, 1-2000 chars)
- post: Post (ManyToOne)
- author: User (ManyToOne)
- createdAt: LocalDateTime
```

## Business Rules

### Status Transitions
```
DRAFT -> SUBMITTED (by author only)
SUBMITTED -> UNDER_REVIEW (by ADMIN only)
UNDER_REVIEW -> RESOLVED | REJECTED (by ADMIN only)
RESOLVED -> CLOSED (by author or ADMIN)
REJECTED -> DRAFT (by author, allows resubmission)
Any status -> CLOSED (by ADMIN only, admin override)
```

### Role Permissions

#### USER Role
- Create posts (own only)
- View own posts (all statuses)
- View others' posts (only SUBMITTED+ statuses, not DRAFT)
- Edit own posts (only DRAFT status)
- Delete own posts (only DRAFT status)
- Transition: DRAFT->SUBMITTED, RESOLVED->CLOSED, REJECTED->DRAFT

#### ADMIN Role
- All USER permissions
- View all posts (any status)
- Edit any post (any status)
- Delete any post
- Assign posts to users
- All status transitions
- Access admin endpoints

## API Endpoints

### Authentication
```
POST /api/auth/login          - Returns user info + token (if JWT enabled)
POST /api/auth/register       - Public registration (USER role only)
GET  /api/auth/me             - Current user info
```

### Posts
```
GET    /api/posts             - List posts (paginated, filtered by visibility rules)
GET    /api/posts/{id}        - Get single post (visibility rules apply)
POST   /api/posts             - Create new post
PUT    /api/posts/{id}        - Update post (ownership/admin rules apply)
DELETE /api/posts/{id}        - Delete post (ownership/admin rules apply)
PATCH  /api/posts/{id}/status - Transition status (validation rules apply)
```

### Admin Endpoints
```
GET    /api/admin/posts       - All posts (no visibility filter)
GET    /api/admin/users       - List all users
PATCH  /api/admin/posts/{id}/assign - Assign post to user
GET    /api/admin/stats       - Dashboard statistics
```

## API Response Formats

### Success Response
```json
{
  "data": { ... },
  "message": "Success message",
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### Paginated Response
```json
{
  "content": [ ... ],
  "page": 0,
  "size": 20,
  "totalElements": 100,
  "totalPages": 5,
  "first": true,
  "last": false
}
```

### Error Response
```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "details": [
    { "field": "title", "message": "Title must be between 5 and 200 characters" }
  ],
  "path": "/api/posts",
  "timestamp": "2024-01-15T10:30:00Z"
}
```

## Validation Rules

### Post Creation/Update DTO
```
title:         @NotBlank, @Size(min=5, max=200)
description:   @NotBlank, @Size(min=10, max=5000)
type:          @NotNull, valid PostType enum
priority:      @Nullable, valid Priority enum if provided
attachmentUrl: @Nullable, @URL if provided (custom validator)
```

### Status Transition DTO
```
newStatus:     @NotNull, valid PostStatus enum
reason:        @Nullable, @Size(max=500) - required for REJECTED status
```

## Security Configuration

### Password Encoding
- BCrypt with strength 12

### Endpoint Security
```
/api/auth/register    - permitAll
/api/auth/login       - permitAll
/api/admin/**         - hasRole('ADMIN')
/api/posts/**         - authenticated
/h2-console/**        - permitAll (dev only)
/swagger-ui/**        - permitAll
/v3/api-docs/**       - permitAll
```

### CORS Configuration
- Allow origins: http://localhost:4200
- Allow methods: GET, POST, PUT, PATCH, DELETE, OPTIONS
- Allow headers: Authorization, Content-Type
- Allow credentials: true

## Seed Data

### Default Users
```
Username: admin    Password: admin123    Role: ADMIN
Username: user1    Password: user123     Role: USER
Username: user2    Password: user123     Role: USER
```

### Sample Posts
- Create 5-10 sample posts with various statuses and types

## Testing Requirements

### Backend Tests
- Unit tests for all service methods
- Integration tests for all controller endpoints
- Repository tests for custom queries
- Test coverage: aim for 80%+

### Frontend Tests
- Component tests for all major components
- Service tests with HttpTestingController
- Guard tests

## Code Style Guidelines

### Java
- Use Lombok for boilerplate reduction
- JavaDoc for all public methods
- Builder pattern for DTOs
- Method-level security annotations (@PreAuthorize)

### TypeScript
- Strict mode enabled
- Interfaces for all API responses
- Async/await over raw observables where cleaner
- TSDoc for services

### Comments
- Add `// EXTENSIBILITY:` comments where enums can be extended
- Add `// SECURITY:` comments explaining permission checks
- Add `// VALIDATION:` comments for custom validators

## Configuration Properties

### application.yml
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:postdb
    driver-class-name: org.h2.Driver
  h2:
    console:
      enabled: true
      path: /h2-console
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
  security:
    jwt:
      enabled: false  # Feature flag for JWT
      secret: your-secret-key
      expiration: 86400000

server:
  port: 8080

springdoc:
  api-docs:
    path: /v3/api-docs
  swagger-ui:
    path: /swagger-ui.html
```

## Agent Task Execution Order

When building this project, follow this order:

1. **Backend Foundation**
   - Create project structure and pom.xml
   - Define enums (Role, PostType, PostStatus, Priority)
   - Create entities (User, Post)
   - Create repositories

2. **Backend Security**
   - Security configuration
   - UserDetailsService implementation
   - BCrypt password encoder
   - Basic Auth configuration

3. **Backend Business Logic**
   - DTOs (request/response)
   - Mappers
   - Service layer with business rules
   - Exception handling

4. **Backend API**
   - Controllers with validation
   - OpenAPI configuration
   - Global exception handler

5. **Backend Data**
   - data.sql seed file
   - application.yml configuration

6. **Backend Tests**
   - Unit tests
   - Integration tests

7. **Frontend Foundation**
   - Angular project setup
   - Models/interfaces
   - Environment configuration

8. **Frontend Core**
   - Auth service
   - HTTP interceptor
   - Auth guard
   - Error handling service

9. **Frontend Features**
   - Login component
   - Post list component
   - Post detail component
   - Post form component
   - Admin dashboard

10. **Documentation**
    - README.md with full setup instructions

## Important Notes

- All code must be complete and runnable - no placeholders or pseudo-code
- Use proper layered architecture (Controller -> Service -> Repository)
- Implement proper error handling at all layers
- Follow REST best practices (proper HTTP methods, status codes)
- Ensure frontend-backend contract alignment
- All enums should be easily extensible with clear documentation
- Pagination defaults: page=0, size=20, sort=createdAt,desc