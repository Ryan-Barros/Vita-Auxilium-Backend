-- =============================================================================
-- CONFIGURAÇÕES DE POLÍTICAS NAS HYPERTABLES
-- =============================================================================

-- 1. Habilita compressão na hypertable e adiciona o campo de agrupamento/ordenação
ALTER TABLE device_data SET (
    timescaledb.compress,
    timescaledb.compress_segmentby = 'device_id',
    timescaledb.compress_orderby = 'date DESC'
    );

-- 2. Adiciona a política de compressão para dados mais antigos que 7 dias
SELECT add_compression_policy('device_data', INTERVAL '7 days');

-- 3. Adiciona a política de retenção para apagar dados mais antigos que 90 dias
SELECT add_retention_policy('device_data', INTERVAL '90 days');

-- 4. Exibe se as políticas foram de fato aplicadas
SELECT *
FROM timescaledb_information.jobs;