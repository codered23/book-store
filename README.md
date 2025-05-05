# Book Store API 📚

A RESTful service for managing books, authors, categories, and user shopping carts. Implemented using Java, Spring Boot, MySQL, and Docker.

## 🚀 Technologies

- Java 17
- Spring Boot 3
- Spring Security (JWT)
- Spring Data JPA (Hibernate)
- MySQL
- Docker + Docker Compose
- TestContainers + JUnit + Mockito
- Swagger (OpenAPI 3)

## 🔐 Authentication

JWT-based authentication. Users can register, log in, and receive tokens for accessing protected resources.

## 📦 Core Features

- 📚 Books: CRUD, filtering, categories
- 🧑‍💼 Users: registration, login, profile
- 🛒 Shopping Cart: add/remove books, place orders
- 📂 Categories: category management
- 📖 Orders: user order history, status

## 🧪 Testing

- `Unit tests` with Mockito
- `Integration tests` with TestContainers for MySQL

## 📄 Documentation

Swagger available at:  
`/swagger-ui/index.html`

## 🐳 Docker

### Run the project:

```bash
docker-compose up --build
