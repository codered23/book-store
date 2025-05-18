# Book Store API 📚

Welcome to the Book Store API! This project is a RESTful service designed for managing an online bookstore, including books, authors, categories, user authentication, shopping carts, and orders. It's implemented using Java and the Spring Boot ecosystem, aiming to provide a robust, scalable, and developer-friendly backend solution.

Ever wondered how an online bookstore backend works? That's what inspired this project! My goal was to build a complete and functional API with Java and Spring Boot. This system manages all the essentials: listing books, organizing categories, handling user accounts, processing shopping carts, and managing orders. I aimed for a clean, efficient, and easy-to-understand backend solution.

---

**🚀 LIVE DEMO & API DOCUMENTATION 🚀**

You can explore and interact with the live API using Swagger UI, available at:
**[http://13.61.180.203/swagger-ui/index.html](http://13.61.180.203/swagger-ui/index.html)**

**Please Note (Live Demo):** This is a public sandbox environment. Data you see or create might be modified by other users or periodically reset. For a stable and private testing experience you should use the local Docker Compose setup

**How to Test Secured (e.g., Admin) Endpoints via Swagger UI (Live Demo or Local):**

Many administrative endpoints require authentication. You can use the default admin credentials:
* **Default Admin Email:** `admin@example.com`
* **Default Admin Password:** `admin123`

Here's how to use them:
1.  **Navigate to Swagger UI** using the live demo link above or your local Swagger UI link.
2.  Find the **Authentication** section (usually `/api/auth`) and expand the `POST /api/auth/login` endpoint.
3.  Click "Try it out". In the request body, enter the default admin email and password:
    ```json
    {
      "email": "admin@example.com",
      "password": "admin123"
    }
    ```
4.  Click "Execute". In the response, you will receive a JWT token (e.g., in a field named `token` or `accessToken`). **Copy this token value.**
5.  At the top right of the Swagger UI page, click the **"Authorize"** button (it might look like a lock icon).
6.  A dialog titled "Available authorizations" will appear. In the "Value" field for the `bearerAuth (http, Bearer)` scheme, **paste the JWT token you copied**.
7.  Click "Authorize" in the dialog, then "Close".
8.  Now you are authenticated in Swagger UI! You can try executing admin-only endpoints (e.g., `POST /api/books` etc.). Swagger UI will automatically include your token for these requests.

---

## 🛠️ Technologies & Tools

This project is built with a modern and robust set of technologies:

* **Programming Language:** Java 17
* **Framework:** Spring Boot
    * Spring MVC (for RESTful APIs)
    * Spring Data JPA (with Hibernate as ORM)
    * Spring Security (JWT for token-based authentication & authorization)
    * Spring Validation
* **Database:** MySQL
* **Database Migrations:** Liquibase
* **API Documentation:** Swagger / OpenAPI 3
* **Testing Frameworks:**
    * JUnit 5
    * Mockito
    * Testcontainers (for MySQL in integration tests)
    * Spring Boot Test (with MockMvc)
* **Build Tool:** Maven
* **Containerization:** Docker & Docker Compose
* **Utilities:** Lombok, MapStruct (for DTO mapping)

---

## 🔐 Authentication (`/api/auth`)

Authentication is handled using JWT (JSON Web Tokens). The system supports:
* **User Registration:** `POST /api/auth/register` - Allows new users to sign up.
* **User Login:** `POST /api/auth/login` - Authenticates existing users and returns a JWT token.

---

## 📦 Core Features & Functionality

The API offers a range of features to manage an online bookstore:

### Books (`/api/books`)
* `POST /api/books`: Create a new book (Admin only).
* `GET /api/books`: Retrieve a paginated list of all books.
* `GET /api/books/{id}`: Retrieve details of a specific book by its ID.
* `PUT /api/books/{id}`: Update an existing book's information (Admin only).
* `DELETE /api/books/{id}`: Delete a book (Admin only).
* `GET /api/books/search`: Search for books based on parameters (by author, price).
* `POST /api/books/{bookId}/categories/{categoryId}`: Add a book to a category (Admin only).

### Categories (`/api/categories`)
* `POST /api/categories`: Create a new category (Admin only).
* `GET /api/categories`: Retrieve a list of all categories.
* `GET /api/categories/{id}`: Retrieve details of a specific category by its ID. (Admin only).
* `PUT /api/categories/{id}`: Update an existing category (Admin only).
* `DELETE /api/categories/{id}`: Delete a category (Admin only).
* `GET /api/categories/{id}/books`: Retrieve all books belonging to a specific category.

### Shopping Cart (`/api/cart`)
* `GET /api/cart`: View the current user's shopping cart.
* `POST /api/cart`: Add an item (book) to the shopping cart.
* `PUT /api/cart/cart-items/{id}`: Update the quantity of an item in the shopping cart.
* `DELETE /api/cart/cart-items/{id}`: Remove an item from the shopping cart.

### Orders (`/api/orders`)
* `POST /api/orders`: Place a new order using items from the shopping cart.
* `GET /api/orders`: Retrieve the order history for the current user.
* `GET /api/orders/{orderId}`: Retrieve details of a specific order by its ID.
* `PUT /api/orders/{id}`: Update the status of an order (Admin only).
* `GET /api/orders/{orderId}/items`: Retrieve all items for a specific order.
* `GET /api/orders/{orderId}/items/{itemId}`: Retrieve a specific item from an order.
  
---

### Running with Docker Compose (Recommended for Quick Start)

Want to get the project up and running quickly with its database? Docker Compose is your friend here! This project includes a `docker-compose.yml` file to make it super simple.

**Here’s what you’ll need first:**

* **Docker Desktop** (or Docker Engine with the Docker Compose CLI) installed and actually **running** on your machine.
* **Git**, so you can grab the project files.

**Let's Get Started:**

1.  **Grab the Code:**
    Open your favorite terminal (like Bash, PowerShell, Git Bash, or your IDE's built-in terminal) and clone the project:
    ```bash
    git clone [https://github.com/olehdev23/book-store.git](https://github.com/olehdev23/book-store.git)
    cd book-store 
    ```
    *(This just downloads the project folder and moves you inside it.)*

2. **Set Up Your Environment (`.env` file):**
    This project requires an `.env` file in the root directory (`book-store`) for environment-specific configurations (like database credentials, ports, and JWT secrets).

    * A template file named **`.env.example`** is included in the project repository. This file contains all the necessary variables with comments explaining them.
    * **Create your local configuration by copying this template to a new file named `.env` in the same root directory.**
        * On Linux/macOS or Git Bash: `cp .env.example .env`
        * On Windows Command Prompt/PowerShell: `copy .env.example .env`
    * Once you have your `.env` file, **open it with a text editor and replace all placeholder values** (like `your_strong_mysql_root_password` and `replace_this_with_your_very_long_and_random_jwt_secret`) with your actual settings.
    * **It's crucial to set a strong `MYSQLDB_ROOT_PASSWORD` and a long, random `JWT_SECRET` (at least 32 characters) for security and proper application functioning.** Refer to the comments within the `.env.example` file for detailed guidance on each variable.

3.  **Launch Everything with Docker Compose:**
    Make sure you're still in the `book-store` root folder in your terminal, then type:
    ```bash
    docker-compose up --build
    ```
    * The `--build` part rebuilds your app's Docker image if you've made changes. Good for the first run!
    * You'll see logs from both your app and the database.

4.  **Access Your Running Application:**
    Once everything starts up (look for messages like "Started BookStoreApplication..."):
    * Your Book Store API will be live at: **`http://localhost:8081`** (using the `SPRING_LOCAL_PORT` you set in your `.env` file).
    * Check out the API docs and try it out with Swagger UI: **`http://localhost:8081/swagger-ui/index.html`**.
    * If you need to connect to MySQL directly from your computer, use port `3307` (as per `MYSQLDB_LOCAL_PORT` in your `.env` file).

5.  **Stopping the Magic:**
    * Done for now? Go back to the terminal where `docker-compose up` is running and press `Ctrl+C`.
    * Then, to clean up properly (stop and remove the containers):
        ```bash
        docker-compose down
        ```
    * *(Careful: if you also want to remove the database data created by Docker, use `docker-compose down -v`.)*
