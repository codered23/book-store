INSERT IGNORE INTO users_roles (user_id, role_id)
VALUES (9999, (SELECT id FROM roles WHERE role_name = 'ADMIN' LIMIT 1));