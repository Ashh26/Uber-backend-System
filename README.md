# 🚗 Cab booking Backend System (Spring Boot)

A production-grade backend system for an Uber-like ride-hailing platform, built using **Spring Boot** and modern backend engineering principles. This project models real-world workflows such as rider–driver matching, ride lifecycle management, dynamic pricing, wallet-based payments, ratings, and secure authentication.

This is **not a CRUD demo** — it is a workflow-driven, extensible backend designed with scalability, maintainability, and real business constraints in mind.

---

## 📌 Key Highlights

* JWT-based authentication with role-based authorization (RIDER, DRIVER, ADMIN)
* Complete ride lifecycle: request → accept → start → end → payment
* Strategy Pattern for pricing, driver matching, and payments
* Wallet & transaction ledger system with platform commission
* Rating & reputation system for riders and drivers
* Geo-spatial queries using PostGIS & JTS
* External distance calculation via OSRM
* Email notification system (single & bulk emails)
* Clean layered architecture with transactional integrity

---

## 🏗️ Architecture Overview

The application follows a **layered architecture** with strong separation of concerns:

* **Controller Layer** – API endpoints & request orchestration
* **Service Layer** – Core business workflows & validations
* **Strategy Layer** – Pluggable business logic (pricing, matching, payment)
* **Repository Layer** – JPA-based persistence
* **Security Layer** – JWT + Spring Security
* **Integration Layer** – OSRM, Email, Wallet, Payments

This structure ensures the system is **open for extension and closed for modification**.

---

## 🔐 Authentication & Security

* Secure signup & login using Spring Security
* Passwords stored using hashing
* Stateless authentication with **JWT access tokens**
* Refresh token support via HTTP-only cookies
* Role-based access control using `@Secured`

Supported roles:

* `ROLE_RIDER`
* `ROLE_DRIVER`
* `ROLE_ADMIN`

---

## 🚦 Ride Lifecycle Flow

1. Rider requests a ride
2. Fare calculated using pricing strategy
3. Driver matching strategy selects nearby drivers
4. Driver accepts the ride
5. OTP-based ride start validation
6. Ride ends
7. Payment processed & wallet updated
8. Rider and Driver can rate each other

Each transition is strictly validated to avoid invalid or fraudulent states.

---

## 💳 Payments & Wallet System

* Wallet created automatically for users
* Ledger-based wallet transactions (CREDIT / DEBIT)
* Platform commission applied transparently
* Supports multiple payment strategies:

  * Wallet payment
  * Cash payment

All financial operations are transaction-safe and auditable.

---

## 🧠 Strategy Pattern Usage

The system heavily uses the **Strategy Pattern** to allow future extensibility:

* **Driver Matching Strategy**

  * Nearest driver
  * Highest-rated driver

* **Ride Fare Strategy**

  * Default pricing
  * Surge pricing (extensible)

* **Payment Strategy**

  * Wallet payment
  * Cash payment

New strategies can be added without touching existing business logic.

---

## 🌍 Geo-Spatial Support

* Uses **PostGIS** for spatial queries
* Stores pickup/drop locations as `Geometry(Point, 4326)`
* Efficient driver proximity search using spatial indexes

---

## 📧 Email Integration

* Centralized `EmailService`
* Supports:

  * Single-recipient emails
  * Bulk emails
* Tested using Spring Boot integration tests

Use cases:

* Ride confirmations
* Payment receipts
* Account notifications

---

## 🧪 Testing

* `@SpringBootTest` for integration testing
* Email service tested for single and multiple recipients
* Transactional service logic ready for unit & integration tests

Planned improvements:

* Strategy unit tests
* Repository tests using Testcontainers
* Security flow tests

---

## 🗄️ Database & Tech Stack

**Backend**

* Java 17+
* Spring Boot
* Spring Security
* Spring Data JPA
* Hibernate

**Database**

* PostgreSQL
* PostGIS extension

**Other Tools**

* ModelMapper
* Lombok
* OSRM (Distance calculation)

---

## ⚙️ Setup & Run

### Prerequisites

* Java 17+
* PostgreSQL with PostGIS enabled
* Maven

### Steps

```bash
# Clone repository
git clone https://github.com/your-username/uber-backend-system.git

# Navigate to project
cd Cab-Booking-System

# Configure application.properties
# (DB, JWT secret, email credentials)

# Run application
mvn spring-boot:run
```

---

## 🚀 Future Enhancements

* Refresh token persistence & rotation
* Admin dashboard & analytics
* Redis caching for driver matching
* Event-driven notifications (Kafka/RabbitMQ)
* Distributed tracing & metrics
* Kubernetes deployment

---

## 📈 Project Positioning

This project demonstrates:

* Real-world backend system design
* Strong Spring Boot fundamentals
* Domain-driven workflows
* Scalable and extensible architecture

It is suitable for:

* Backend interviews
* System design discussions
* Resume & portfolio showcase

---

## 👤 Author

**Yasif**
Backend Engineer | Java | Spring Boot | System Design

---

⭐ If you find this project useful, feel free to star the repository!
