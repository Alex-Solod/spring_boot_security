# 📚 Online Book Store API

[![Java Version](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot Version](https://img.shields.io/badge/Spring%20Boot-3.x-green.svg)](https://spring.io/projects/spring-boot)

A robust and scalable RESTful API for an Online Book Store, built using the Spring Boot ecosystem. This project
simulates a real-world e-commerce backend where users can browse books, manage their shopping carts,
and administrators can manage the inventory, all protected by secure role-based authentication.

🎬 **[Watch the 3-Minute Project Demo on Loom](<https://www.loom.com/share/73b503c74ec04101bd3771b382d3038f>)**

---

## 🚀 Key Features

* **User Authentication & Authorization:** Secure registration and login powered by Spring Security and JWT (JSON Web Tokens).
* **Role-Based Access Control (RBAC):** Distinct permissions for `USER` (browse, manage cart, place orders) and `ADMIN` (full CRUD operations on books and categories).
* **Shopping Cart Management:** Dynamic cart updates, adding/removing items, and quantity adjustments.
* **Order Processing:** Seamless checkout flow that converts shopping cart items into formal orders with history tracking.
* **Automated Database Migrations:** Database schema evolution managed via Liquibase.
* **API Documentation:** Interactive and fully documented API endpoints using Swagger UI.

---

## 🛠️ Tech Stack & Tools

* **Backend Framework:** Spring Boot 3.x (Spring Web, Spring Security, Spring Data JPA)
* **Database:** MySQL (Production/Local), H2 (Isolated Integration Testing)
* **Migration Tool:** Liquibase
* **Data Mapping:** MapStruct (for efficient Entity-to-DTO conversions)
* **Security:** JWT (JSON Web Tokens)
* **Testing:** JUnit 5, MockMvc, AssertJ
* **API Docs:** Springdoc-openapi (Swagger UI)

---

## 📋 API Architecture & Endpoints

The application follows a clean layered architecture (Controller -> Service -> Repository). Below are the primary controllers:

* `AuthenticationController` (`/api/auth/*`) - Public endpoints for user `register` and `login`.
* `BookController` (`/api/books/*`) - Fetching books (Public/User) and modifying inventory (Admin only).
* `CategoryController` (`/api/categories/*`) - Book categorization and filtering.
* `ShoppingCartController` (`/api/cart/*`) - User-specific operations to manage current selections.
* `OrderController` (`/api/orders/*`) - Checkout processing and order history overview.

> 💡 **Interactive Docs:** Once the application is running, you can explore and test all endpoints visually via Swagger UI at: `http://localhost:8080/api/swagger-ui/index.html`

---

## 🔧 Setup & Installation

Follow these steps to run the project locally on your machine.

### Prerequisites
* Java Development Kit (JDK) 21
* Maven 3.x
* MySQL Server running locally or via Docker

### 1. Clone the Repository
```bash
git clone <https://github.com/Alex-Solod/spring_boot_security>
cd <project-folder-name>
