# MiniBank Backend

A simple banking backend built with Spring Boot, Spring Security, JPA, PostgreSQL, and JWT authentication.

This project provides a REST API for:

- user registration and login
- JWT-based authentication
- creating bank accounts in different currencies
- transferring money between accounts
- viewing paginated account and transaction data

## Tech Stack

- Java 21
- Spring Boot
- Spring Security
- Spring Data JPA
- PostgreSQL
- JWT (`jjwt`)
- Gradle
- H2 for tests

## Project Structure

```text
src/main/java/com/example/minibankbackend
├── account
├── common
├── transaction
└── user
```

Main areas:

- `user` - registration, login, current user info
- `account` - account creation and account listing
- `transaction` - transfers and transaction history
- `common` - security, exception handling, shared response models

## Requirements

- Java 21
- PostgreSQL running locally or remotely

## Configuration

The app uses environment variables with defaults from `application.properties`.

| Variable | Default | Description |
|---|---|---|
| `DB_URL` | `jdbc:postgresql://localhost:5433/minibank` | PostgreSQL connection URL |
| `DB_USERNAME` | `postgres` | Database username |
| `DB_PASSWORD` | `postgres` | Database password |
| `SERVER_PORT` | `8080` | Application port |
| `JWT_SECRET` | `change_this_secret_key_to_long_random_string_123456789` | JWT signing secret |
| `JWT_EXPIRATION_MINUTES` | `120` | Token lifetime in minutes |
| `SHOW_SQL` | `true` | Show SQL in logs |
| `SECURITY_LOG_LEVEL` | `INFO` | Spring Security log level |
| `HIBERNATE_LOG_LEVEL` | `INFO` | Hibernate SQL log level |

## Run Locally

1. Create a PostgreSQL database named `minibank`.
2. Make sure PostgreSQL is available at the configured host and port.
3. Export environment variables if needed.
4. Start the application:

```bash
./gradlew bootRun
```

The API will be available at:

```text
http://localhost:8080
```

## Running Tests

```bash
./gradlew test
```

## Authentication

Only `/api/auth/**` endpoints are public.

All other endpoints require a Bearer token:

```http
Authorization: Bearer <jwt-token>
```

## API Overview

### Auth

#### Register

`POST /api/auth/register`

Request:

```json
{
  "email": "user@example.com",
  "password": "secret123"
}
```

Response:

- `201 Created`

#### Login

`POST /api/auth/login`

Request:

```json
{
  "email": "user@example.com",
  "password": "secret123"
}
```

Response:

```json
{
  "token": "your-jwt-token"
}
```

### Users

#### Get Current User

`GET /api/users/me`

Requires authentication.

### Accounts

#### Create Account

`POST /api/accounts`

Requires authentication.

Supported currencies:

- `KZT`
- `USD`

Request:

```json
{
  "currency": "KZT"
}
```

Example response:

```json
{
  "id": 1,
  "currency": "KZT",
  "balance": 0
}
```

#### List My Accounts

`GET /api/accounts?page=0&size=10`

Requires authentication.

Example response:

```json
{
  "items": [
    {
      "id": 1,
      "currency": "KZT",
      "balance": 15000
    }
  ],
  "page": 0,
  "size": 10,
  "totalItems": 1,
  "totalPages": 1
}
```

### Transfers

#### Transfer Money

`POST /api/transfers`

Requires authentication.

Rules:

- source and destination accounts must be different
- the authenticated user must own the source account
- both accounts must use the same currency
- the source account must have enough balance

Request:

```json
{
  "fromAccountId": 1,
  "toAccountId": 2,
  "amount": 500.00,
  "description": "Monthly transfer"
}
```

Response:

- `200 OK`

### Transactions

#### List Transactions

`GET /api/transactions?accountId=1&page=0&size=10`

Optional filters:

- `from` - ISO-8601 instant
- `to` - ISO-8601 instant

Example:

```text
/api/transactions?accountId=1&from=2026-03-01T00:00:00Z&to=2026-03-31T23:59:59Z&page=0&size=10
```

Example response:

```json
{
  "items": [
    {
      "id": 10,
      "accountId": 1,
      "type": "TRANSFER_OUT",
      "status": "SUCCESS",
      "amount": 500.00,
      "description": "Monthly transfer",
      "createdAt": "2026-03-18T10:15:30Z"
    }
  ],
  "page": 0,
  "size": 10,
  "totalItems": 1,
  "totalPages": 1
}
```

## Error Response Format

When a request fails, the API returns a structured error response:

```json
{
  "timestamp": "2026-03-18T10:15:30Z",
  "path": "/api/transfers",
  "errorCode": "INSUFFICIENT_FUNDS",
  "message": "Not enough balance",
  "details": []
}
```

Possible error types include:

- `VALIDATION_ERROR`
- `NOT_FOUND`
- business errors such as `INSUFFICIENT_FUNDS`, `CURRENCY_MISMATCH`, and `SAME_ACCOUNT`
- `INTERNAL_ERROR`

## Example cURL Flow

### Register

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","password":"secret123"}'
```

### Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","password":"secret123"}'
```

### Create Account

```bash
curl -X POST http://localhost:8080/api/accounts \
  -H "Authorization: Bearer <jwt-token>" \
  -H "Content-Type: application/json" \
  -d '{"currency":"KZT"}'
```

### Get My Accounts

```bash
curl "http://localhost:8080/api/accounts?page=0&size=10" \
  -H "Authorization: Bearer <jwt-token>"
```

### Transfer Money

```bash
curl -X POST http://localhost:8080/api/transfers \
  -H "Authorization: Bearer <jwt-token>" \
  -H "Content-Type: application/json" \
  -d '{"fromAccountId":1,"toAccountId":2,"amount":500.00,"description":"Monthly transfer"}'
```

## Notes

- CORS is configured for `http://localhost:5173`
- Hibernate schema update is enabled with `spring.jpa.hibernate.ddl-auto=update`
- The default JWT secret is only suitable for local development and should be changed in production
