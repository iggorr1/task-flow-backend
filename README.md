# Task Flow — Backend API

Backend REST API for a personal task management application.

The API supports user registration, JWT authentication, task CRUD operations, task status management, filtering, sorting, pagination, and role-based access control.

---

## Live Demo

Frontend:

```text
https://wwwho.lol
```

Production API:

```text
https://api.wwwho.lol
```

Frontend repository:

```text
https://github.com/iggorr1/task-manager-frontend
```

---

## Tech Stack

- Java
- Spring Boot
- Spring Security
- JWT
- PostgreSQL
- Spring Data JPA / Hibernate
- Maven
- Docker
- Docker Compose
- Swagger / OpenAPI

---

## Features

- User registration
- User login
- JWT authentication
- Password hashing with BCrypt
- Create tasks
- Get current user's tasks
- Get task by id
- Update tasks
- Delete tasks
- Mark task as completed
- Task status management:
    - `TODO`
    - `IN_PROGRESS`
    - `DONE`
- Pagination
- Sorting
- Filtering by:
    - completed
    - title
    - status
- Global exception handling
- Role-based access control:
    - `USER`
    - `ADMIN`
- Dockerized backend
- PostgreSQL database
- Production deployment through Docker Compose and Cloudflare Tunnel

---

## API Endpoints

### Auth

| Method | Endpoint | Description |
|---|---|---|
| POST | `/users/register` | Register new user |
| POST | `/users/login` | Login and receive JWT token |

### Tasks

| Method | Endpoint | Description |
|---|---|---|
| POST | `/tasks` | Create task |
| GET | `/tasks` | Get current user's tasks |
| GET | `/tasks/{id}` | Get task by id |
| PUT | `/tasks/{id}` | Update task |
| DELETE | `/tasks/{id}` | Delete task |
| PATCH | `/tasks/{id}/complete` | Mark task as completed |
| PATCH | `/tasks/{id}/status` | Update task status |

### Admin

| Method | Endpoint | Description |
|---|---|---|
| GET | `/admin/test` | Test admin-only access |

---

## Query Parameters for `GET /tasks`

| Parameter | Example | Description |
|---|---|---|
| `page` | `page=0` | Page number |
| `size` | `size=10` | Page size |
| `sort` | `sort=createdAt,desc` | Sort field and direction |
| `completed` | `completed=true` | Filter by completion status |
| `title` | `title=test` | Filter by title contains, ignore case |
| `status` | `status=IN_PROGRESS` | Filter by task status |

Example:

```http
GET /tasks?page=0&size=10&status=IN_PROGRESS&title=test&sort=createdAt,desc
Authorization: Bearer <token>
```

---

## Task Status

Each task has a status:

```text
TODO
IN_PROGRESS
DONE
```

New tasks are created with status:

```text
TODO
```

When task status is changed to `DONE`, the `completed` field becomes `true`.

When task status is changed to `TODO` or `IN_PROGRESS`, the `completed` field becomes `false`.

---

## Authentication

Most task endpoints require JWT authentication.

After login, copy the token from the response and send it in the `Authorization` header:

```text
Authorization: Bearer <token>
```

Login response example:

```json
{
  "token": "jwt-token"
}
```

---

## Example Requests

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

### Create task

```http
POST /tasks
Authorization: Bearer <token>
Content-Type: application/json
```

```json
{
  "title": "Learn Spring Boot",
  "description": "Practice REST API"
}
```

### Get tasks

```http
GET /tasks?page=0&size=10&status=IN_PROGRESS&title=test&sort=createdAt,desc
Authorization: Bearer <token>
```

### Get task by id

```http
GET /tasks/1
Authorization: Bearer <token>
```

### Update task

```http
PUT /tasks/1
Authorization: Bearer <token>
Content-Type: application/json
```

```json
{
  "title": "Updated title",
  "description": "Updated description"
}
```

### Mark task as completed

```http
PATCH /tasks/1/complete
Authorization: Bearer <token>
```

### Update task status

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

### Delete task

```http
DELETE /tasks/1
Authorization: Bearer <token>
```

### Admin-only endpoint

```http
GET /admin/test
Authorization: Bearer <admin_token>
```

---

## Error Response Format

Basic error response:

```json
{
  "status": 404,
  "message": "Task not found",
  "timestamp": "2026-05-05T11:14:07.5835068"
}
```

Validation error response:

```json
{
  "status": 400,
  "message": "Validation failed",
  "timestamp": "2026-05-05T11:14:07.5835068",
  "errors": {
    "login": "must not be blank",
    "password": "must not be blank"
  }
}
```

---

## Swagger / OpenAPI

Swagger UI:

```text
http://localhost:8080/swagger-ui/index.html
```

OpenAPI JSON:

```text
http://localhost:8080/v3/api-docs
```

---

## Environment Variables

The application uses environment variables for database and JWT configuration.

Example:

```env
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/demo
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=your_password

JWT_SECRET=your_long_secret_key
JWT_EXPIRATION=86400000
```

Do not commit real secrets to GitHub.

---

## Running Locally

1. Clone the repository:

```bash
git clone https://github.com/iggorr1/demo.git
cd demo
```

2. Configure environment variables or `application.properties`.

3. Start PostgreSQL.

4. Run the application:

```bash
./mvnw spring-boot:run
```

On Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

5. Open Swagger UI:

```text
http://localhost:8080/swagger-ui/index.html
```

---

## Docker

Build backend image:

```bash
docker build -t task-flow-backend .
```

Run backend container:

```bash
docker run -p 8080:8080 task-flow-backend
```

In production, the backend runs through Docker Compose together with:

- PostgreSQL
- Frontend
- Cloudflare Tunnel

---

## Tests

The project includes integration tests for the main task flow:

- Register user
- Login and receive JWT
- Create task
- Update task status
- Filter tasks by status

Run tests:

```bash
./mvnw test
```

On Windows:

```powershell
.\mvnw.cmd test
```

---

## Deployment

The production version runs on an Ubuntu Server using Docker Compose.

Deployment flow:

```text
GitHub
↓
git pull on server
↓
Docker Compose rebuild
↓
Cloudflare Tunnel
↓
api.wwwho.lol
```

Server deploy command:

```bash
cd ~/apps/task-flow
./deploy.sh
```

---

## Current Status

Implemented:

- Authentication
- JWT security
- Task CRUD
- Task status workflow
- Filtering / sorting / pagination
- PostgreSQL persistence
- Docker deployment
- Production API domain
- Frontend integration

Planned improvements:

- Better backend error messages
- `/users/me` endpoint
- Task priority
- Due dates
- More tests