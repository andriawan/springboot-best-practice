-- V6__assign_default_role_to_users.sql

-- This script ensures that every user has the default 'ROLE_USER'.
-- It's idempotent and will not create duplicate entries.

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE r.name = 'ROLE_USER'
  AND NOT EXISTS (
    SELECT 1
    FROM user_roles ur
    WHERE ur.user_id = u.id AND ur.role_id = r.id
  );
