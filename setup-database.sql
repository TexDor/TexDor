-- Create database if it doesn't exist
CREATE DATABASE IF NOT EXISTS texdor_db;

-- Use the database
USE texdor_db;

-- Show all tables (should be empty if first time)
SHOW TABLES;

-- Check if tables exist
SELECT TABLE_NAME 
FROM information_schema.TABLES 
WHERE TABLE_SCHEMA = 'texdor_db';

