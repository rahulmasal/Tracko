-- Tracko Database Initialization for Docker
-- This runs on first container startup

-- Create user (if not exists)
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'tracko_user') THEN
        CREATE USER tracko_user WITH PASSWORD 'tracko_pass_2024';
    END IF;
END
$$;

-- Create database (idempotent check)
SELECT 'CREATE DATABASE tracko_db OWNER tracko_user ENCODING ''UTF8'' LC_COLLATE ''en_US.UTF-8'' LC_CTYPE ''en_US.UTF-8'''
WHERE NOT EXISTS (SELECT 1 FROM pg_database WHERE datname = 'tracko_db')\gexec

-- Grant permissions
GRANT ALL PRIVILEGES ON DATABASE tracko_db TO tracko_user;
GRANT ALL ON SCHEMA public TO tracko_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO tracko_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO tracko_user;
GRANT ALL PRIVILEGES ON ALL FUNCTIONS IN SCHEMA public TO tracko_user;

ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO tracko_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO tracko_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON FUNCTIONS TO tracko_user;
