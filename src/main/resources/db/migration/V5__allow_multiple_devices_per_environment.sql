-- =============================================================================
-- V5: PERMITIR MÚLTIPLOS DISPOSITIVOS POR AMBIENTE
-- =============================================================================

ALTER TABLE devices
    DROP CONSTRAINT IF EXISTS devices_environment_id_key;

CREATE INDEX IF NOT EXISTS idx_devices_environment_id ON devices (environment_id);
