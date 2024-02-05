CREATE TABLE IF NOT EXISTS order_items (
                                           id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                           order_id BIGINT NOT NULL,
                                           book_id BIGINT NOT NULL,
                                           quantity INT NOT NULL,
                                           price DECIMAL(19, 2) NOT NULL,
                                           is_deleted BOOLEAN DEFAULT false,
                                           FOREIGN KEY (order_id) REFERENCES orders(id),
                                           FOREIGN KEY (book_id) REFERENCES books(id)
);
