# TaskFlow — Backend API

Backend REST API for **TaskFlow**, a full-stack task management application with JWT authentication, PostgreSQL persistence, task workflow statuses, pinned tasks, and Telegram reminders.

The backend is built as a practical Java/Spring Boot pet project with a real production deployment flow: Docker, PostgreSQL, public API domain, and Telegram webhook integration.

---

## Live Links

```text
Frontend:       https://wwwho.lol
Backend API:    https://api.wwwho.lol
Telegram hook:  https://telegram.wwwho.lol/telegram/webhook
```

Swagger / OpenAPI, when enabled in the running backend:

```text
https://api.wwwho.lol/swagger-ui/index.html
```

---

## Tech Stack

- Java 17
- Spring Boot
- Spring Web
- Spring Security
- JWT authentication
- BCrypt password hashing
- Spring Data JPA / Hibernate
- PostgreSQL
- Bean Validation
- Maven
- Docker / Docker Compose
- Telegram Bot API
- Cloudflare Tunnel
- Swagger / OpenAPI

---

## Main Features

### Authentication

- User registration
- User login
- Google OAuth2 login
- JWT token generation
- Password hashing with BCrypt
- Protected routes with `Authorization: Bearer <token>`
- Role-based admin route example

### Task Management

- Create tasks
- Get current user's tasks
- Get task by ID
- Update task title/description
- Delete tasks
- Pin/unpin important tasks
- Task statuses:
  - `TODO`
  - `IN_PROGRESS`
  - `DONE`
- `completed` field is synchronized with task status
- Task ownership checks: users can access only their own tasks
- Pagination, filtering, search, and sorting

### Telegram Reminders

- Generate one-time Telegram connection links
- Connect a user's Telegram chat through `/start <token>`
- Validate Telegram webhook secret header
- Store Telegram connection per user
- Schedule task reminders with `reminderAt`
- Background scheduler checks due reminders every minute
- Send reminder messages through Telegram
- Track `reminderSent` to avoid duplicate messages

---

## Project Structure

```text
src/main/java/com/igor/taskflow
|-- config          # Security, OpenAPI configuration
|-- controller      # REST controllers
|-- dto             # Request/response DTOs
|-- entity          # JPA entities
|-- exception       # Custom exceptions and global handler
|-- repository      # Spring Data repositories
|-- security        # JWT filter, JWT service, UserDetailsService
`-- service         # Business logic, Telegram integration, scheduler
```

Important classes:

```text
SecurityConfig.java              # Security rules and CORS
JwtAuthenticationFilter.java     # Reads Bearer tokens
JwtService.java                  # Generates and validates JWT
TaskService.java                 # Task business logic
TelegramService.java             # Telegram linking flow
TelegramWebhookController.java   # Telegram webhook endpoint
TaskReminderScheduler.java       # Sends due reminders
```

---

## API Overview

### Auth

| Method | Endpoint | Auth | Description |
|---|---|---:|---|
| `POST` | `/users/register` | No | Register a new user |
| `POST` | `/users/login` | No | Login and receive JWT token |

### Tasks

| Method | Endpoint | Auth | Description |
|---|---|---:|---|
| `GET` | `/tasks` | Yes | Get current user's tasks |
| `GET` | `/tasks/{id}` | Yes | Get one task by ID |
| `POST` | `/tasks` | Yes | Create a task |
| `PUT` | `/tasks/{id}` | Yes | Update task title/description |
| `DELETE` | `/tasks/{id}` | Yes | Delete task |
| `PATCH` | `/tasks/{id}/complete` | Yes | Mark task as done |
| `PATCH` | `/tasks/{id}/status` | Yes | Update task status |
| `PATCH` | `/tasks/{id}/pin` | Yes | Toggle pinned state |
| `PATCH` | `/tasks/{id}/reminder` | Yes | Set reminder date/time |

### Telegram

| Method | Endpoint | Auth | Description |
|---|---|---:|---|
| `POST` | `/telegram/link` | Yes | Create Telegram connection link |
| `GET` | `/telegram/status` | Yes | Get current Telegram connection status |
| `DELETE` | `/telegram/disconnect` | Yes | Disconnect Telegram account |
| `POST` | `/telegram/webhook` | Secret header | Receive Telegram updates |

### Admin

| Method | Endpoint | Auth | Description |
|---|---|---:|---|
| `GET` | `/admin/test` | Admin | Test admin-only access |

---

## Query Parameters for `GET /tasks`

| Parameter | Example | Description |
|---|---|---|
| `page` | `page=0` | Page number |
| `size` | `size=10` | Page size |
| `sort` | `sort=createdAt,desc` | Sort field and direction |
| `completed` | `completed=false` | Filter by completion state |
| `title` | `title=meeting` | Search by title, case-insensitive |
| `status` | `status=TODO` | Filter by task status |

Example:

```http
GET /tasks?page=0&size=10&status=TODO&sort=createdAt,desc
Authorization: Bearer <token>
```

---

## Request Examples

### Register

```http
POST /users/register
Content-Type: application/json
```

```json
{
  "name": "Igor",
  "email": "igor@example.com",
  "login": "igor",
  "password": "password123"
}
```

### Login

```http
POST /users/login
Content-Type: application/json
```

```json
{
  "login": "igor",
  "password": "password123"
}
```

Response:

```json
{
  "token": "jwt-token"
}
```

### Create Task

```http
POST /tasks
Authorization: Bearer <token>
Content-Type: application/json
```

```json
{
  "title": "Learn Spring Boot",
  "description": "Practice REST API and JWT auth"
}
```

### Update Task Status

```http
PATCH /tasks/1/status
Authorization: Bearer <token>
Content-Type: application/json
```

```json
{
  "status": "IN_PROGRESS"
}
```

### Set Task Reminder

```http
PATCH /tasks/1/reminder
Authorization: Bearer <token>
Content-Type: application/json
```

```json
{
  "reminderAt": "2026-05-25T15:30:00.000Z"
}
```

### Create Telegram Link

```http
POST /telegram/link
Authorization: Bearer <token>
```

Response:

```json
{
  "link": "https://t.me/taskflow_reminders_ik_bot?start=<token>",
  "expiresAt": "2026-05-25T15:40:00.000Z"
}
```

---

## Environment Variables

Create `.env` or configure environment variables on the server:

```env
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/taskflow
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

Do not commit real secrets or real Telegram bot tokens to Git.

---

## Local Development

### 1. Start PostgreSQL

Using Docker Compose:

```bash
docker compose up -d postgres
```

### 2. Configure environment variables

Example for local development:

```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/taskflow
export SPRING_DATASOURCE_USERNAME=taskflow_user
export SPRING_DATASOURCE_PASSWORD=change_me
export JWT_SECRET=replace-with-a-long-random-secret
export JWT_EXPIRATION=86400000
export FRONTEND_URL=http://localhost:5173
export GOOGLE_CLIENT_ID=replace-with-google-client-id
export GOOGLE_CLIENT_SECRET=replace-with-google-client-secret
export TELEGRAM_BOT_USERNAME=your_bot_username
export TELEGRAM_BOT_TOKEN=your_bot_token
export TELEGRAM_WEBHOOK_SECRET=local-dev-secret
```

Google OAuth redirect URIs:

```text
Local:      http://localhost:8080/login/oauth2/code/google
Production: https://api.wwwho.lol/login/oauth2/code/google
```

### 3. Run backend

```bash
./mvnw spring-boot:run
```

Default local API URL:

```text
http://localhost:8080
```

---

## Build

```bash
./mvnw clean package
```

Run the generated jar:

```bash
java -jar target/task-flow-backend-0.0.1-SNAPSHOT.jar
```

---

## Docker

Build image:

```bash
docker build -t task-flow-backend .
```

Run container example:

```bash
docker run -p 8080:8080 --env-file .env task-flow-backend
```

---

## Production Deployment Notes

The deployed setup uses:

```text
GitHub → Ubuntu Server → Docker Compose → Cloudflare Tunnel → public domains
```

Important production routes:

```text
wwwho.lol           -> frontend container
api.wwwho.lol       -> backend container
telegram.wwwho.lol  -> backend webhook endpoint
```

Telegram webhook URL:

```text
https://telegram.wwwho.lol/telegram/webhook
```

Set webhook example:

```bash
curl "https://api.telegram.org/bot$TELEGRAM_BOT_TOKEN/setWebhook?url=https://telegram.wwwho.lol/telegram/webhook&secret_token=$TELEGRAM_WEBHOOK_SECRET&drop_pending_updates=true"
```

---

## Security Notes

- Passwords are stored as BCrypt hashes.
- JWT is required for all user task and Telegram account endpoints.
- `/telegram/webhook` is public but protected by Telegram's secret token header.
- Users can only access their own tasks.
- Admin endpoints require `ADMIN` role.
- Real `.env` files, JWT secrets, database passwords, and bot tokens should never be committed.

---

## Current Status

Implemented:

- JWT auth
- Task CRUD
- Task statuses
- Search/filter/sort/pagination
- Pinned tasks
- Telegram connection flow
- Telegram reminders
- Dockerized backend
- PostgreSQL persistence
- Production deployment through Cloudflare Tunnel

Planned / possible improvements:

- Remove reminder endpoint: `DELETE /tasks/{id}/reminder`
- Better structured logging for scheduler and Telegram flow
- Integration tests for auth, task ownership, and reminders
- DTO cleanup and package naming cleanup before final portfolio polish
