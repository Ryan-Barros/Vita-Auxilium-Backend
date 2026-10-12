package com.vitaauxilium.vitaauxilium.utils;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;

@Converter
@Component
public class CpfConverter implements AttributeConverter<String, String> {

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int IV_LENGTH = 12;
    private static final int TAG_BITS = 128;

    private final SecretKey key;
    private final SecureRandom random = new SecureRandom();

    public CpfConverter(@Value("${crypto.secret}") String base64Secret) {
        byte[] raw = Base64.getDecoder().decode(base64Secret);
        if (raw.length != 32) {
            throw new IllegalStateException("crypto.secret deve ter 256 bits (32 bytes em Base64)");
        }
        this.key = new SecretKeySpec(raw, "AES");
    }

    @Override
    public String convertToDatabaseColumn(String cpf) {
        if (cpf == null) return null;
        try {
            byte[] iv = new byte[IV_LENGTH];
            random.nextBytes(iv);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(TAG_BITS, iv));
            byte[] encrypted = cipher.doFinal(cpf.getBytes(StandardCharsets.UTF_8));

            byte[] payload = ByteBuffer.allocate(IV_LENGTH + encrypted.length)
                    .put(iv).put(encrypted).array();
            return Base64.getEncoder().encodeToString(payload);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Erro ao cifrar CPF", e);
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        try {
            byte[] payload = Base64.getDecoder().decode(dbData);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key,
                    new GCMParameterSpec(TAG_BITS, payload, 0, IV_LENGTH));
            byte[] plain = cipher.doFinal(payload, IV_LENGTH, payload.length - IV_LENGTH);
            return new String(plain, StandardCharsets.UTF_8);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Erro ao decifrar CPF", e);
        }
    }
}