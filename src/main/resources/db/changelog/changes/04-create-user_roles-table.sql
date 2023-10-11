CREATE TABLE IF NOT EXISTS user_roles
(
    user_id BIGINT,
    roles_id BIGINT,
    PRIMARY KEY (user_id, roles_id),
    FOREIGN KEY (user_id) REFERENCES User (id),
    FOREIGN KEY (roles_id) REFERENCES Role (id)
);