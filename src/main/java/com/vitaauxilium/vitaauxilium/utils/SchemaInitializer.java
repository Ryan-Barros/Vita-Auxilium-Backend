package com.vitaauxilium.vitaauxilium.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@Order(Integer.MAX_VALUE)
public class SchemaInitializer implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(@NonNull ApplicationArguments args) {
        log.info("Iniciando a execução dos scripts de Schema customizados...");

        try {
            String createExtension = """
                    CREATE EXTENSION IF NOT EXISTS unaccent
                    """;
            jdbcTemplate.execute(createExtension);
            log.info("Extensão criada com sucesso!");
        } catch (Exception e) {
            log.error("Erro ao criar a extensão");
            throw e;
        }

        try {
            String createFunction = """
                    CREATE OR REPLACE FUNCTION immutable_unaccent(text)
                    RETURNS text AS $$
                        SELECT public.unaccent($1)
                    $$ LANGUAGE sql IMMUTABLE PARALLEL SAFE STRICT;
                    """;
            jdbcTemplate.execute(createFunction);
            log.info("Função 'immutable_unaccent' criada ou atualizada com sucesso.");
        } catch (Exception e) {
            log.error("Erro ao criar a função 'immutable_unaccent'. Verifique se a extensão 'unaccent' está ativa no banco.", e);
            throw e;
        }

        try {
            String createIndex = """
                     CREATE INDEX IF NOT EXISTS idx_users_name_unaccent
                     ON users (immutable_unaccent(user_name) text_pattern_ops);
                    """;
            jdbcTemplate.execute(createIndex);
            log.info("Índice 'idx_users_name_unaccent' verificado/criado com sucesso.");
        } catch (Exception e) {
            log.error("Erro ao criar o índice. A tabela 'users' e a coluna 'user_name' realmente existem?", e);
            throw e;
        }
    }
}
