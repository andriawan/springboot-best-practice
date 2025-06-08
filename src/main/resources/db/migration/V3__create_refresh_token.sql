-- V3__create_refresh_token.sql

-- Create the auth schema if it doesn't exist
CREATE SCHEMA IF NOT EXISTS auth;

-- Create the refresh_token table
CREATE TABLE IF NOT EXISTS auth.refresh_token (
    id SERIAL PRIMARY KEY,
    token TEXT NOT NULL UNIQUE,
    user_id INTEGER,
    expires_at TIMESTAMPTZ NOT NULL,
    blacklisted_at TIMESTAMPTZ,

    CONSTRAINT fk_user
        FOREIGN KEY (user_id)
        REFERENCES public.users(id)
        ON DELETE SET NULL
);

-- Index for quick lookup by token
CREATE INDEX IF NOT EXISTS idx_refresh_token_token
    ON auth.refresh_token (token);

-- Index to help with cleanup of expired tokens
CREATE INDEX IF NOT EXISTS idx_refresh_token_blacklisted_at
    ON auth.refresh_token (blacklisted_at);
