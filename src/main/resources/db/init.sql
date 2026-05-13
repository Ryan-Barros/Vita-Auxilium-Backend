CREATE EXTENSION IF NOT EXISTS "pgcrypto";
CREATE EXTENSION IF NOT EXISTS "unaccent";
CREATE INDEX ON users (unaccent('unaccent', name) text_pattern_ops);