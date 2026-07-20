package com.vitaauxilium.vitaauxilium.utils;

import com.vitaauxilium.vitaauxilium.repositories.EnvironmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
@RequiredArgsConstructor
public class CodeGenerator {
    private static final String ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int CODE_LENGTH = 8;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private final EnvironmentRepository environmentRepository;

    private String generateRandomCode() {
        StringBuilder sb = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            sb.append(ALPHABET.charAt(SECURE_RANDOM.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }

    public String generateUniqueCode(String prefix) {
        for (int i = 0; i < 10; i++) {
            String code = prefix + generateRandomCode();
            if (!environmentRepository.existsByEnvironmentCode(code)) {
                return code;
            }
        }
        throw new RuntimeException("Não foi possível gerar o código único");
    }
}
