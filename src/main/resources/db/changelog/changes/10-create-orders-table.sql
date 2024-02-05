CREATE TABLE IF NOT EXISTS orders (
                                      id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                      user_id BIGINT NOT NULL,
                                      status TINYINT NOT NULL,
                                      total DECIMAL(19, 2) NOT NULL,
                                      order_date VARCHAR(255) NOT NULL,
                                      shipping_address VARCHAR(255) NOT NULL,
                                      is_deleted BOOLEAN DEFAULT false,
                                      FOREIGN KEY (user_id) REFERENCES users(id)
);
