CREATE TABLE IF NOT EXISTS cart_items (
                                          id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                          shopping_cart_id BIGINT NOT NULL,
                                          book_id BIGINT NOT NULL,
                                          quantity INT NOT NULL,
                                          is_deleted BOOLEAN DEFAULT false,
                                          FOREIGN KEY (shopping_cart_id) REFERENCES shopping_carts(id),
                                          FOREIGN KEY (book_id) REFERENCES books(id)
);

