CREATE TABLE IF NOT EXISTS categories (
                            id BIGINT PRIMARY KEY AUTO_INCREMENT,
                            name VARCHAR(255),
                            description VARCHAR(1000),
                            is_deleted BOOLEAN DEFAULT false
);
