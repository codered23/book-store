CREATE TABLE IF NOT EXISTS users_roles
(
    user_id BIGINT,
    roles_id BIGINT,
    PRIMARY KEY (user_id, roles_id),
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (roles_id) REFERENCES roles (id)
);