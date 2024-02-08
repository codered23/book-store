CREATE TABLE IF NOT EXISTS users (
                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                      email VARCHAR(255) NOT NULL UNIQUE,
                      password VARCHAR(255) NOT NULL,
                      first_name VARCHAR(255) NOT NULL,
                      last_name VARCHAR(255) NOT NULL,
                      shipping_address VARCHAR(255),
                      is_deleted BOOLEAN NOT NULL DEFAULT FALSE
);
