-- =============================================================================
-- V4: PADRONIZAÇÃO DE CATEGORIAS, LEMBRETES E EVOLUÇÃO DA TABELA HUMOR
-- =============================================================================

INSERT INTO categories (id_categoria, name_categoria) VALUES
    ('01960000-0000-7000-8000-000000000001', 'MEDICATION'),
    ('01960000-0000-7000-8000-000000000002', 'MEAL'),
    ('01960000-0000-7000-8000-000000000003', 'EXERCISE'),
    ('01960000-0000-7000-8000-000000000004', 'APPOINTMENT'),
    ('01960000-0000-7000-8000-000000000005', 'HYGIENE'),
    ('01960000-0000-7000-8000-000000000006', 'ROUTINE'),
    ('01960000-0000-7000-8000-000000000007', 'HEALTH')
ON CONFLICT (name_categoria) DO NOTHING;

ALTER TABLE reminders
    ADD COLUMN IF NOT EXISTS is_completed boolean NOT NULL DEFAULT false;

ALTER TABLE reminders_aud
    ADD COLUMN IF NOT EXISTS is_completed boolean;

ALTER TABLE humor
    ADD COLUMN IF NOT EXISTS environment_id uuid REFERENCES environments (environment_id),
    ADD COLUMN IF NOT EXISTS user_id uuid REFERENCES users (user_id);

ALTER TABLE humor
    DROP CONSTRAINT IF EXISTS humor_activity_activity_id_key;

ALTER TABLE humor
    ALTER COLUMN humor_intensity SET DEFAULT 3;

CREATE INDEX IF NOT EXISTS idx_humor_environment ON humor (environment_id);
