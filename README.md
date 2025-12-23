# Student Orders App

Spring Boot REST API for managing students and their orders.

## Running the Backend

You need:
- Java 17+
- Maven
- MySQL

1. Start MySQL (easiest with Docker):
```bash
docker run -p 3306:3306 --name student-orders-app -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=student_orders -d mysql
```

2. Build and run:
```bash
mvn clean install
mvn spring-boot:run
```

The app runs on `http://localhost:8080`

If you need to change database credentials, edit `src/main/resources/application.properties`

## Database

The app automatically creates tables and seeds some test data on startup.

If you want to do it manually:
```sql
CREATE DATABASE student_orders;
```
Then run the scripts in `src/main/resources/schema.sql` and `seed.sql`

Tables:
- students: id, name, grade, school, created_at
- orders: id, student_id, total, status, created_at

## Endpoints

- `POST /students` - Create a student
- `GET /students` - Get all students
- `POST /orders` - Create an order
- `GET /orders?studentId={id}` - Get orders for a student

## Tech Stack

- Spring Boot 4.0.0
- Spring Data JPA
- MySQL 8.0
- Lombok
- Apache Commons Text
- JUnit 5 & Mockito

## Security

Input sanitization is implemented to prevent XSS attacks. All user text inputs are sanitized before saving to the database. SQL injection is prevented by using JPA's parameterized queries.

## Shortcuts/Assumptions

- CORS is enabled for `http://localhost:4200` (dev only)
- Database credentials default to root/root
- No authentication - all endpoints are public
- No pagination on list endpoints
- Order status is just "pending" or "paid"
- Deleting a student deletes their orders (cascade)

## What Could Be Improved

- Add authentication (JWT)
- Pagination on GET endpoints
- API documentation (Swagger)
- Restrict CORS for production
- Integration tests
- Docker Compose setup
