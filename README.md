# Student Orders Application

A Spring Boot REST API for managing students and their orders.

## How to Run the Backend

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- MySQL 8.0+

### Steps
1. **Configure database credentials** (if different from defaults)

   Edit `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/student_orders
   spring.datasource.username=root
   spring.datasource.password=root
   ```

2. **Build and run**
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

3. **Access the API**

   The server runs on `http://localhost:8080`

## How to Create/Import the MySQL Database

### Option 1: Using Docker (Recommended)
Run MySQL in a Docker container:
```bash
docker run -p 3306:3306 --name student-orders-app -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=student_orders -d mysql
```

The application automatically creates tables and seeds data on startup.

### Option 2: Local MySQL Installation
If you have MySQL installed locally, the application will automatically create and seed the database on startup.

### Manual Setup (Optional)
If you prefer manual setup:

1. **Create the database**
   ```sql
   CREATE DATABASE student_orders;
   ```

2. **Run schema and seed scripts**
   ```bash
   mysql -u root -p student_orders < src/main/resources/schema.sql
   mysql -u root -p student_orders < src/main/resources/seed.sql
   ```

### Database Schema
- **students**: `id`, `name`, `grade`, `school`, `created_at`
- **orders**: `id`, `student_id`, `total`, `status`, `created_at` (with foreign key constraint and indexes)

## API Endpoints

| Method | Endpoint                 | Description              |
|--------|--------------------------|--------------------------|
| POST   | `/students`              | Create a new student     |
| GET    | `/students`              | Get all students         |
| POST   | `/orders`                | Create a new order       |
| GET    | `/orders?studentId={id}` | Get orders for a student |

## Tools & Frameworks Used

- **Spring Boot 4.0.0** - Application framework
- **Spring Data JPA** - Database ORM
- **MySQL 8.0** - Relational database
- **Lombok** - Boilerplate code reduction
- **Jakarta Validation** - Request validation
- **JUnit 5 & Mockito** - Unit testing
- **Maven** - Build tool

## Assumptions & Shortcuts

1. **CORS enabled for all origins** - Configured for local development (`http://localhost:4200`)
2. **Database credentials** - Defaults to `root/root`, configurable via properties
3. **No authentication/authorization** - Endpoints are publicly accessible
4. **No pagination** - `GET /students` returns all records
5. **Simple order status** - Only `pending` and `paid` statuses
6. **No soft deletes** - Student deletion cascades to orders
7. **Fixed decimal precision** - Order totals limited to `DECIMAL(10,2)`

## Areas for Improvement

### High Priority
- **Authentication & Authorization** - Implement JWT or OAuth2 for secure access
- **Pagination & Filtering** - Add pagination to student/order lists with search/filter capabilities
- **Input Sanitization** - Add XSS protection and input sanitization
- **Production CORS** - Restrict CORS to specific allowed origins

### Medium Priority
- **Order Management** - Add endpoints for updating and deleting orders
- **Student Details** - Include order count/total in student response
- **Caching** - Implement Redis for frequently accessed data
- **API Documentation** - Add Swagger/OpenAPI documentation

### Low Priority
- **Monitoring & Metrics** - Add Spring Actuator with custom metrics
- **Integration Tests** - Add full API integration tests
- **Docker Support** - Containerize application with docker-compose
- **Audit Logging** - Track who created/modified records
