# Task Manager API

Backend REST API for managing personal tasks with authentication.

## Tech Stack

- Java
- Spring Boot
- Spring Security
- JWT
- PostgreSQL
- Spring Data JPA / Hibernate
- Swagger / OpenAPI

## Features

- User registration
- User login
- JWT authentication
- Create tasks
- Get own tasks
- Get task by id
- Update tasks
- Delete tasks
- Mark task as completed
- Task status: `TODO`, `IN_PROGRESS`, `DONE`
- Pagination
- Sorting
- Filtering by completed, title, and status
- Global exception handling
- Role-based access control: `USER`, `ADMIN`

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

## Query Parameters for GET /tasks

| Parameter | Example | Description |
|---|---|---|
| `page` | `page=0` | Page number |
| `size` | `size=10` | Page size |
| `sort` | `sort=createdAt,desc` | Sort field and direction |
| `completed` | `completed=true` | Filter by completion status |
| `title` | `title=test` | Filter by title contains, ignore case |
| `status` | `status=IN_PROGRESS` | Filter by task status |

## Task Status

Each task has a status:

```text
TODO
IN_PROGRESS
DONE
```

New tasks are created with status `TODO`.

When task status is changed to `DONE`, the `completed` field becomes `true`.
When task status is changed to `TODO` or `IN_PROGRESS`, the `completed` field becomes `false`.

## Error Response Format

```json
{
  "status": 404,
  "message": "Task not found",
  "timestamp": "2026-05-05T11:14:07.5835068"
}
```

Validation errors include field-level details:

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

## Swagger / OpenAPI

Swagger UI is available at:

```text
http://localhost:8080/swagger-ui/index.html
```

OpenAPI JSON is available at:

```text
http://localhost:8080/v3/api-docs
```

## Authentication

Most task endpoints require JWT authentication.

After login, copy the token from the response and send it in the Authorization header:

```text
Authorization: Bearer <token>
```

## Example Requests

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

### Get tasks with filtering and sorting

```http
GET /tasks?page=0&size=10&status=IN_PROGRESS&title=test&sort=createdAt,desc
Authorization: Bearer <token>
```

### Get task by id

```http
GET /tasks/1
Authorization: Bearer <token>
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

### Admin-only endpoint

```http
GET /admin/test
Authorization: Bearer <admin_token>
```

## Tests

Added integration test for main task flow:

- register user
- login and receive JWT
- create task
- update task status
- filter tasks by status

## Running locally

1. Clone the repository
2. Configure PostgreSQL in `application.properties`
3. Run the application
4. Open Swagger UI:

```text
http://localhost:8080/swagger-ui/index.html
```

## Docker

PostgreSQL can be started with Docker Compose:

```bash
docker compose up -d