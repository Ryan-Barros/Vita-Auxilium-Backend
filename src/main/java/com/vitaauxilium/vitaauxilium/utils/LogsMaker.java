package com.vitaauxilium.vitaauxilium.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;

public class LogsMaker {

    private static final ObjectMapper mapper = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .configure(SerializationFeature.INDENT_OUTPUT, true)
            .build();

    public static void logInfo(Logger logger, String mensagem, Object objeto) {
        if (logger != null && logger.isInfoEnabled()) {
            try {
                String json = mapper.writeValueAsString(objeto);
                logger.info("{}:\n{}", mensagem, json);
            } catch (Exception e) {
                logger.error("Erro ao formatar objeto para log JSON", e);
            }
        }
    }
}
