# TaskFlow - Backend API

Backend API for **TaskFlow**, a full-stack task manager built with Java and Spring Boot.

The project includes JWT authentication, Google OAuth2 login, PostgreSQL persistence, Flyway migrations, role-based admin endpoints, Telegram reminders, Docker deployment, and a public production setup behind Cloudflare Tunnel.

## Live Project

```text
Frontend:      https://wwwho.lol
Backend API:   https://api.wwwho.lol
Telegram hook: https://telegram.wwwho.lol/telegram/webhook
```

Swagger / OpenAPI, when enabled:

```text
https://api.wwwho.lol/swagger-ui/index.html
```

## Tech Stack

- Java 17
- Spring Boot
- Spring Web
- Spring Security
- JWT
- OAuth2 Client with Google
- Spring Data JPA / Hibernate
- PostgreSQL
- Flyway database migrations
- Bean Validation
- Maven
- Docker / Docker Compose
- Telegram Bot API
- Cloudflare Tunnel
- Swagger / OpenAPI

## Features

### Authentication

- Register and login with local username/password
- Login with Google OAuth2
- JWT token generation
- BCrypt password hashing
- Protected API routes with `Authorization: Bearer <token>`
- Role-based admin access

### Task Management

- Create, read, update, and delete tasks
- Task ownership checks: users can access only their own tasks
- Task statuses: `TODO`, `IN_PROGRESS`, `DONE`
- Pin/unpin important tasks
- Pagination, search, filtering, and sorting
- Reminder date/time for tasks

### Telegram Reminders

- Generate Telegram connection links
- Connect Telegram chat through `/start <token>`
- Validate Telegram webhook secret header
- Store one Telegram connection per user
- Send due task reminders from a scheduled backend job
- Avoid duplicate reminder messages with `reminderSent`

### Admin

- Admin-only access check
- Read-only admin statistics
- Read-only admin users/tasks overview

## Project Structure

```text
src/main/java/com/igor/taskflow
|-- config          # Security and OpenAPI configuration
|-- controller      # REST controllers
|-- dto             # Request/response DTOs
|-- entity          # JPA entities
|-- exception       # Custom exceptions and global handler
|-- repository      # Spring Data repositories
|-- security        # JWT and OAuth-related security classes
`-- service         # Business logic, Telegram integration, schedulers
```

Important classes:

```text
TaskFlowApplication.java              # Spring Boot entry point
SecurityConfig.java                   # Security, JWT, OAuth2, CORS
JwtAuthenticationFilter.java          # Reads Bearer tokens
JwtService.java                       # Generates and validates JWT
OAuth2UserService.java                # Maps Google users to local users
OAuth2AuthenticationSuccessHandler.java # Issues JWT after Google login
TaskService.java                      # Task business logic
TelegramService.java                  # Telegram linking flow
TaskReminderScheduler.java            # Sends due reminders
```

## API Overview

### Auth

| Method | Endpoint | Auth | Description |
|---|---|---:|---|
| `POST` | `/users/register` | No | Register a new user |
| `POST` | `/users/login` | No | Login and receive JWT |
| `GET` | `/oauth2/authorization/google` | No | Start Google OAuth2 login |
| `GET` | `/login/oauth2/code/google` | No | Google OAuth2 callback |

### Tasks

| Method | Endpoint | Auth | Description |
|---|---|---:|---|
| `GET` | `/tasks` | Yes | Get current user's tasks |
| `GET` | `/tasks/{id}` | Yes | Get one task by ID |
| `POST` | `/tasks` | Yes | Create task |
| `PUT` | `/tasks/{id}` | Yes | Update task |
| `DELETE` | `/tasks/{id}` | Yes | Delete task |
| `PATCH` | `/tasks/{id}/complete` | Yes | Mark task as done |
| `PATCH` | `/tasks/{id}/status` | Yes | Update task status |
| `PATCH` | `/tasks/{id}/pin` | Yes | Toggle pinned state |
| `PATCH` | `/tasks/{id}/reminder` | Yes | Set reminder date/time |
| `DELETE` | `/tasks/{id}/reminder` | Yes | Remove reminder |

### Telegram

| Method | Endpoint | Auth | Description |
|---|---|---:|---|
| `POST` | `/telegram/link` | Yes | Create Telegram connection link |
| `GET` | `/telegram/status` | Yes | Get Telegram connection status |
| `DELETE` | `/telegram/disconnect` | Yes | Disconnect Telegram |
| `POST` | `/telegram/webhook` | Secret header | Receive Telegram updates |

### Admin

| Method | Endpoint | Auth | Description |
|---|---|---:|---|
| `GET` | `/admin/test` | Admin | Check admin access |
| `GET` | `/admin/stats` | Admin | Get application statistics |
| `GET` | `/admin/users` | Admin | Get users overview |
| `GET` | `/admin/tasks` | Admin | Get tasks overview |

## Database

The schema is managed by Flyway migrations.

Current migrations:

```text
V1__init_schema.sql
V2__add_oauth_provider_to_users.sql
```

Hibernate is used as the ORM layer, but schema changes are versioned by Flyway:

```properties
spring.jpa.hibernate.ddl-auto=validate
```

This means Hibernate validates that entities match the database, while Flyway is responsible for creating and changing tables.

## Environment Variables

Use environment variables or a server `.env` file. Never commit real secrets.

```env
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/taskflow
SPRING_DATASOURCE_USERNAME=taskflow_user
SPRING_DATASOURCE_PASSWORD=change_me

JWT_SECRET=replace-with-a-long-random-secret
JWT_EXPIRATION=86400000

FRONTEND_URL=https://wwwho.lol
GOOGLE_CLIENT_ID=replace-with-google-client-id
GOOGLE_CLIENT_SECRET=replace-with-google-client-secret

TELEGRAM_BOT_USERNAME=your_bot_username
TELEGRAM_BOT_TOKEN=your_bot_token
TELEGRAM_WEBHOOK_SECRET=replace-with-random-webhook-secret
```

## Google OAuth2 Setup

Google Cloud Console OAuth client type:

```text
Web application
```

Authorized JavaScript origins:

```text
https://wwwho.lol
```

Authorized redirect URIs:

```text
https://api.wwwho.lol/login/oauth2/code/google
```

If the application is behind Cloudflare Tunnel or another reverse proxy and Google shows `redirect_uri_mismatch`, check the exact `redirect_uri` in Google error details. If the backend generates `http://api.wwwho.lol/login/oauth2/code/google`, either add that URI in Google Console as a temporary workaround or configure forwarded headers in the production proxy/backend setup.

## Local Development

Start PostgreSQL:

```bash
docker compose up -d postgres
```

Run backend:

```bash
./mvnw spring-boot:run
```

Default local API:

```text
http://localhost:8080
```

Local Google OAuth redirect URI:

```text
http://localhost:8080/login/oauth2/code/google
```

## Build and Test

Run tests:

```bash
./mvnw test
```

Build jar:

```bash
./mvnw clean package
```

Run jar:

```bash
java -jar target/task-flow-backend-0.0.1-SNAPSHOT.jar
```

## Docker

The Dockerfile uses a Maven build image and a lightweight Java runtime image.

Build image:

```bash
docker build -t task-flow-backend .
```

Run container example:

```bash
docker run -p 8080:8080 --env-file .env task-flow-backend
```

## Production Deployment

Current production flow:

```text
GitHub -> Ubuntu Server -> Docker Compose -> Cloudflare Tunnel -> public domains
```

Server deployment command:

```bash
cd ~/apps/task-flow
./deploy.sh
```

Important production routes:

```text
wwwho.lol           -> frontend
api.wwwho.lol       -> backend API
telegram.wwwho.lol  -> Telegram webhook endpoint
```

Telegram webhook URL:

```text
https://telegram.wwwho.lol/telegram/webhook
```

## Security Notes

- Passwords are stored as BCrypt hashes.
- JWT protects user task and Telegram endpoints.
- Users can only access their own tasks.
- Admin endpoints require `ADMIN` role.
- Telegram webhook is public but protected by a secret token header.
- Real `.env` files, JWT secrets, database passwords, Google client secrets, and Telegram tokens must not be committed.

## Tests

The backend includes integration and service tests for:

- Application context startup
- Auth flow
- Task ownership
- Admin access
- Google OAuth user mapping
- Flyway migration compatibility with test database

Run:

```bash
./mvnw test
```

## Current Status

Implemented:

- Local JWT auth
- Google OAuth2 login
- Task CRUD
- Task statuses
- Search/filter/sort/pagination
- Pinned tasks
- Telegram connection flow
- Telegram reminders
- Admin read-only endpoints
- Flyway migrations
- Dockerized backend
- PostgreSQL persistence
- Production deployment through Cloudflare Tunnel

Possible improvements:

- Add structured JSON logging
- Add refresh tokens
- Add email verification
- Add CI pipeline with tests and Docker build
