CREATE TABLE IF NOT EXISTS shopping_carts (
                                              id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                              user_id BIGINT NOT NULL,
                                              is_deleted BOOLEAN DEFAULT false,
                                              FOREIGN KEY (user_id) REFERENCES users(id)
);
