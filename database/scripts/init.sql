-- Tracko Database Initialization Script
-- Run this as PostgreSQL superuser to create the database and user

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'tracko_user') THEN
        CREATE USER tracko_user WITH PASSWORD 'tracko_pass_2024';
    END IF;
END
$$;

-- Create database (must be run outside transaction block, so executed separately)
-- CREATE DATABASE tracko_db WITH OWNER tracko_user ENCODING 'UTF8' LC_COLLATE 'en_US.UTF-8' LC_CTYPE 'en_US.UTF-8' TEMPLATE template0;

-- Grant permissions
GRANT ALL PRIVILEGES ON DATABASE tracko_db TO tracko_user;

-- Connect to tracko_db before running migrations
-- \c tracko_db

-- Grant schema permissions
GRANT ALL ON SCHEMA public TO tracko_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO tracko_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO tracko_user;
GRANT ALL PRIVILEGES ON ALL FUNCTIONS IN SCHEMA public TO tracko_user;

-- Default permissions for future tables
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO tracko_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO tracko_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON FUNCTIONS TO tracko_user;
