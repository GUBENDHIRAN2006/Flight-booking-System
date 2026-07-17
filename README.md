# ✈️ Flight Booking System

A full-stack flight reservation web application built with **Spring Boot**. Users can register, search flights, book or cancel seats, and view their booking history. Admins can manage flight inventory and view live booking/revenue statistics.

---

## 🚀 Features

**User**
- Register and log in (session-based authentication)
- Search flights by source, destination, date, price, and airline
- Book a seat on a flight (with automatic seat availability check)
- Cancel an existing booking (seat is automatically released)
- View personal booking history
- Update profile (name, email, password, passport, visa details)

**Admin**
- Add, update, and delete flights
- View booking statistics (total, confirmed, cancelled, total revenue)
- View flight statistics (average price, cheapest flights, flights per airline)
- View complete booking action history/audit log

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Backend | Java 21, Spring Boot 4.1.0 |
| Backend modules | Spring Web (MVC/REST), Spring Data JPA |
| Database | MySQL (H2 optional for local testing) |
| ORM | Hibernate |
| Boilerplate reduction | Lombok |
| Frontend | HTML, CSS, JavaScript (served as static resources) |
| Build tool | Maven |

---

## 🏗️ Architecture

```
Browser (HTML/CSS/JS)
        │  HTTP + JSON (session cookie)
        ▼
Controller Layer      →  @RestController (Auth, Flist, Booking, BookHistory)
        ▼
Service Layer          →  @Service (business logic + @Transactional)
        ▼
Repository Layer       →  Spring Data JPA (extends JpaRepository)
        ▼
MySQL Database          →  flight_book_db
```

A `DataLoader` (`CommandLineRunner`) automatically seeds a default admin account and ~50 sample flights the first time the app runs.

---

## 📁 Project Structure

```
booking/
├── src/main/java/com/flight/booking/
│   ├── BookingApplication.java        # Main entry point
│   ├── controller/                    # REST controllers
│   │   ├── AuthController.java
│   │   ├── FlistController.java
│   │   ├── BookingController.java
│   │   └── BookHistory.java
│   ├── service/                       # Business logic
│   │   ├── UserService.java
│   │   ├── FlistService.java
│   │   ├── BookingService.java
│   │   ├── BookHistoryService.java
│   │   └── DataLoader.java
│   ├── repository/                    # Spring Data JPA repositories
│   │   ├── UserRepository.java
│   │   ├── FlistRepository.java
│   │   ├── BookingRepository.java
│   │   └── BookingHistoryRepository.java
│   ├── entity/                        # JPA entities
│   │   ├── User.java
│   │   ├── Flist.java
│   │   ├── Booking.java
│   │   └── BookHistory.java
│   └── dto/
│       ├── request/                   # LoginReqDTO, RegisterReqDTO, UserProfileUpdateDTO
│       └── response/                  # ApiResponse
├── src/main/resources/
│   ├── application.properties
│   └── static/                        # Frontend pages
│       ├── index.html
│       ├── login.html
│       ├── register.html
│       ├── home.html
│       ├── flight-booking.html
│       ├── booking.html
│       ├── payment.html
│       ├── profile.html
│       ├── history.html
│       └── admin.html
└── pom.xml
```

---

## ⚙️ Setup & Installation

### Prerequisites
- Java 21+
- Maven 3.8+
- MySQL 8+ (running locally)

### 1. Clone the repository
```bash
git clone <your-repo-url>
cd booking
```

### 2. Configure the database
Create a MySQL user/database matching `src/main/resources/application.properties`, or update it to match your own credentials:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/flight_book_db?createDatabaseIfNotExist=true
spring.datasource.username=flight
spring.datasource.password=flight
spring.jpa.hibernate.ddl-auto=update
```
The database and tables are created automatically on first run — no manual SQL scripts needed.

### 3. Run the application
```bash
./mvnw spring-boot:run
```
The app starts on **http://localhost:8080**.

### 4. Default admin login
```
Email: admin@gmail.com
Password: admin@123
```

---

## 📡 API Endpoints

### Auth — `/api/auth`
| Method | Endpoint | Description |
|---|---|---|
| POST | `/register` | Register a new user |
| POST | `/login` | Log in and start a session |
| POST | `/logout` | End the session |
| GET | `/me` | Get the currently logged-in user |
| PUT | `/profile` | Update user profile |

### Flights — `/api/flights`
| Method | Endpoint | Description |
|---|---|---|
| GET | `/` | Search/list flights (filters: source, destination, date, maxPrice, airline) |
| GET | `/{id}` | Get a single flight by ID |
| POST | `/` | Create a new flight (admin) |
| PUT | `/{id}` | Update a flight (admin) |
| DELETE | `/{id}` | Delete a flight (admin) |
| GET | `/stats` | Flight statistics (avg price, cheapest, per-airline counts) |

### Bookings — `/api/bookings`
| Method | Endpoint | Description |
|---|---|---|
| POST | `/` | Book a flight |
| DELETE | `/{id}` | Cancel a booking |
| GET | `/` | Get all bookings |
| GET | `/user/{userId}` | Get bookings for a specific user |
| GET | `/stats` | Booking statistics (total, confirmed, cancelled, revenue) |

### Booking History — `/api/history`
| Method | Endpoint | Description |
|---|---|---|
| GET | `/` | Get full booking action history |
| GET | `/user/{email}` | Get booking history for a specific user |

---

## 🗄️ Database Schema (Auto-generated)

| Table | Key Columns |
|---|---|
| `users` | id, name, email (unique), password, role, passport, visa |
| `flights` | id, flight_number, airline, source, destination, departure_time, arrival_time, price, available_seats, total_seats, flight_type |
| `bookings` | id, user_id (FK), flight_id (FK), passenger_name, passenger_email, seat_number, booking_date, status |
| `booking_history` | id, booking_id, user_email, flight_number, action, action_date, details |

---

## 🔮 Possible Future Improvements
- Hash passwords with BCrypt and add Spring Security / JWT-based authentication
- Add role-based access control (`@PreAuthorize`) to protect admin-only endpoints
- Replace field injection (`@Autowired`) with constructor injection
- Add request validation (`@Valid`, `@NotBlank`) on DTOs
- Add pagination to flight search and booking lists
- Centralize error handling with `@RestControllerAdvice`

---

## 📄 License
This project was built for academic/learning purposes.
