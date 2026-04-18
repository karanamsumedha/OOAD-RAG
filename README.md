# Research Paper Recommendation & Citation Management Platform

Production-ready full-stack project (OOAD + MVC + SOLID + Design Patterns) with:

- **Backend**: Java, Spring Boot, Spring MVC, Spring Security (JWT), Hibernate/JPA, REST APIs
- **Frontend**: React + Axios
- **Database**: PostgreSQL (default) or MySQL (supported via config)

## Repository structure

```
backend/
frontend/
```

## Quick start (PostgreSQL)

### 1) Start database

Install PostgreSQL and create a database/user (example):

```sql
CREATE DATABASE rag_platform;
CREATE USER rag_user WITH PASSWORD 'rag_pass';
GRANT ALL PRIVILEGES ON DATABASE rag_platform TO rag_user;
```

### 2) Run backend

```bash
cd backend
mvn clean spring-boot:run
```

Backend runs at `http://localhost:8080`.

Swagger/OpenAPI is available at `http://localhost:8080/swagger-ui.html`.

### 3) Run frontend

```bash
cd frontend
npm install
npm run dev
```

Frontend runs at `http://localhost:5173`.

## Default roles

- `ROLE_RESEARCHER`
- `ROLE_CURATOR`
- `ROLE_ADMIN`

On first run, seed users are created (see `backend/src/main/resources/data.sql`):

- Admin: `admin@demo.com` / `Admin@123`
- Curator: `curator@demo.com` / `Curator@123`
- Researcher: `user@demo.com` / `User@123`

## Major features implemented

- Paper search + filtering (domain/year/author/keywords)
- Content-based recommendations using user history + paper metadata
- Citation generation (APA, IEEE)
- Personal library management + reading progress tracking
- Feedback & rating system
- Admin user listing + basic usage reports

## Design patterns (where to look)

- **Factory**: `CitationFormatterFactory` (creates APA/IEEE formatters)
- **Strategy**: `RecommendationStrategy` + `ContentBasedRecommendationStrategy`
- **Observer**: `UserInteractionRecordedEvent` + listener updates recommendations
- **Proxy**: `CachingRecommendationServiceProxy` wraps `RecommendationService`
- **Adapter**: `ExternalMetadataAdapter` + `CrossrefMetadataAdapter` (extensible external metadata)

## OOAD / UML mapping notes

- **Use cases** map to REST controllers under `backend/.../controller`
- **Class diagram** maps to `model` + `service` abstractions
- **Activity flows**: Search → View details → Save → Track progress → Recommend
- **State**: Library item progress `NOT_STARTED → IN_PROGRESS → COMPLETED`

## What was completed recently

- **Spring Security + JWT**: `SecurityConfig`, `JwtAuthenticationFilter`, `JwtService`, `AppUserDetails`
- **Demo seed data**: roles + sample users + papers (`DatabaseSeed`)
- **React UI** (`frontend/`): MUI dark theme, dashboard, search, paper detail, library, recommendations, admin
- **CORS** enabled for `http://localhost:5173`
- **Submission docs**:
  - `docs/DATABASE_SCHEMA.md`
  - `docs/API_SAMPLES.md`
  - `docs/UML_DIAGRAMS.md`
  - `backend/src/main/resources/schema.sql`

## Next steps for you

1. **PostgreSQL**: Create DB `rag_platform` and user (see Quick start), or point `spring.datasource.*` in `backend/src/main/resources/application.yml` at your instance.
2. **Run backend**: `cd backend` → `mvn spring-boot:run` (requires Java 17).
3. **Run frontend**: `cd frontend` → `npm install` → `npm run dev`.
4. **Try demo accounts**: `user@demo.com` / `User@123`, `curator@demo.com` / `Curator@123`, `admin@demo.com` / `Admin@123`.
5. **API docs**: Open `http://localhost:8080/swagger-ui.html` and use **Authorize** with `Bearer <token>` from `/api/login`.
6. **Postman**: Import the OpenAPI spec from `/v3/api-docs` or call the endpoints listed in this README.
7. **Viva prep**: Skim `CitationFormatterFactory`, `ContentBasedRecommendationStrategy`, `CachingRecommendationServiceProxy`, `RecommendationUpdateListener`, and `ExternalMetadataAdapter` for pattern explanations.

