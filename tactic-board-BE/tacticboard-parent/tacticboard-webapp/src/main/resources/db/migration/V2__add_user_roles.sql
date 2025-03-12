-- Insert default roles
INSERT INTO roles (name) VALUES ('ROLE_USER');
INSERT INTO roles (name) VALUES ('ROLE_COACH');
INSERT INTO roles (name) VALUES ('ROLE_ADMIN');

-- Create admin user (password: admin123)
INSERT INTO users (username, password, email, first_name, last_name, active)
VALUES ('admin', '$2a$10$yfIHMg3xrN0aCvmksXf6aOUKlKqUMtU2XPEpQH2M86T.77UvJmGHG', 'admin@tacticboard.com', 'Admin', 'User', true);

-- Assign admin role to admin user
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.username = 'admin' AND r.name = 'ROLE_ADMIN';