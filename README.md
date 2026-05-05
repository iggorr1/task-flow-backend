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