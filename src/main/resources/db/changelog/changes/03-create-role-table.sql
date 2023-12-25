CREATE TABLE IF NOT EXISTS role (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       role_name VARCHAR(255) NOT NULL UNIQUE
);