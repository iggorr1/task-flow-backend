# Task Manager API

Backend REST API for managing personal tasks with authentication.

## Tech Stack

- Java
- Spring Boot
- Spring Security
- JWT
- PostgreSQL
- Spring Data JPA / Hibernate

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
- Pagination
- Sorting
- Filtering by completed and title
- Global exception handling


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

## Query Parameters for GET /tasks

| Parameter | Example | Description |
|---|---|---|
| `page` | `page=0` | Page number |
| `size` | `size=10` | Page size |
| `sort` | `sort=createdAt,desc` | Sort field and direction |
| `completed` | `completed=true` | Filter by completion status |
| `title` | `title=test` | Filter by title contains, ignore case |

## Error Response Format

```json
{
  "status": 404,
  "message": "Task not found",
  "timestamp": "2026-05-05T11:14:07.5835068"
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
GET /tasks?page=0&size=10&completed=false&title=test&sort=createdAt,desc
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