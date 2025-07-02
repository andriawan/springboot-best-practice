-- V5__standardize_and_add_roles.sql
INSERT INTO roles (name) VALUES
    ('ROLE_MODERATOR'),
    ('ROLE_ADMIN'),
    ('ROLE_USER'),
    ('ROLE_GUEST'),
    ('ROLE_API')
ON CONFLICT (name) DO NOTHING;
