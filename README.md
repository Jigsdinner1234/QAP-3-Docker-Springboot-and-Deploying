# Golf Club API

A Spring Boot REST API for golf club members, tournaments, and tournament registration. It uses Spring Data JPA (repository pattern) and a service layer to keep persistence and HTTP concerns separate. Member-to-tournament participation is a many-to-many relationship.

## Requirements

- Java 17 or Docker with Docker Compose
- Maven 3.9+ for running outside Docker

## Run locally

```powershell
mvn spring-boot:run
```

By default, the application uses an in-memory H2 database. The database is recreated when the application stops. To use another database, set `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`, and `SPRING_DATASOURCE_DRIVER`.

## Run in Docker

From the project directory:

```powershell
docker compose up --build
```

The API is available at `http://localhost:8080`; PostgreSQL data persists in the `postgres-data` volume. Stop the services with `Ctrl+C`, then run `docker compose down` to remove containers (the named data volume remains). For a clean local reset, remove that volume explicitly with `docker compose down --volumes`.

## API endpoints

Dates use ISO format (`YYYY-MM-DD`). Membership types are `ANNUAL`, `MONTHLY`, and `LIFETIME`. Lists support combined search criteria and return all records when no query parameters are provided.

| Method | Endpoint | Purpose |
|---|---|---|
| `POST` | `/api/members` | Add a member |
| `GET` | `/api/members` | Retrieve all members |
| `GET` | `/api/members/{id}` | Retrieve a member |
| `GET` | `/api/members?name=alex` | Search name (case-insensitive substring) |
| `GET` | `/api/members?membershipType=ANNUAL` | Search membership type |
| `GET` | `/api/members?phoneNumber=555-0100` | Search exact phone number |
| `GET` | `/api/members?tournamentStartDate=2026-08-15` | Members registered for a tournament starting on date |
| `POST` | `/api/tournaments` | Add a tournament |
| `GET` | `/api/tournaments` | Retrieve all tournaments |
| `GET` | `/api/tournaments/{id}` | Retrieve a tournament |
| `GET` | `/api/tournaments?startDate=2026-08-15` | Search exact start date |
| `GET` | `/api/tournaments?location=halifax` | Search location (case-insensitive substring) |
| `POST` | `/api/tournaments/{tournamentId}/members/{memberId}` | Register a member for a tournament |

Example member request:

```json
{
  "name": "Alex Morgan",
  "address": "12 Course Road, Halifax, NS",
  "email": "alex@example.com",
  "phoneNumber": "555-0100",
  "membershipStartDate": "2026-07-21",
  "membershipType": "ANNUAL"
}
```

Example tournament request:

```json
{
  "startDate": "2026-08-15",
  "endDate": "2026-08-16",
  "location": "Halifax Golf Club",
  "entryFee": 75.00,
  "cashPrizeAmount": 1000.00
}
```

Invalid input returns HTTP 400, duplicate member emails return HTTP 409, and missing IDs return HTTP 404. See `postman/Golf-Club-API.postman_collection.json` for executable request examples. The collection generates a fresh member email and saves created member/tournament IDs for later registration requests.

## RDS connection

The application accepts standard Spring datasource environment variables and does not embed RDS credentials. For an RDS PostgreSQL database, set:

```text
SPRING_DATASOURCE_URL=jdbc:postgresql://<rds-endpoint>:5432/<database>
SPRING_DATASOURCE_USERNAME=<database-user>
SPRING_DATASOURCE_PASSWORD=<database-password>
SPRING_DATASOURCE_DRIVER=org.postgresql.Driver
SPRING_JPA_DDL_AUTO=update
```

For a deployed application, configure these as deployment environment secrets rather than committing them. The RDS security group must allow inbound PostgreSQL traffic (port 5432) from the application's security group; the database must be reachable from where the API runs. This project has not been connected to a live RDS instance or deployed to AWS. No AWS credentials or endpoint were available in this workspace, so connection and deployment screenshots still need to be captured after configuring your account.

## Testing and screenshots

Run tests with `mvn test`. The Postman collection covers member/tournament creation and retrieval, all documented searches, and tournament registration. Import the collection in Postman, run the requests against `http://localhost:8080`, and capture the required screenshots yourself. I could not generate verified Postman or Docker runtime screenshots here because Maven and Docker are not installed in the current environment.
