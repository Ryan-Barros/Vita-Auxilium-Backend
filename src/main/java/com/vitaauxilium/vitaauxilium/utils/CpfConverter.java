package com.vitaauxilium.vitaauxilium.utils;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Converter
public class CpfConverter implements AttributeConverter<String, String> {

    @Value("${crypto.secret}")
    private String secret;

    @Override
    public String convertToDatabaseColumn(String cpf) {
        if (cpf == null) return null;
        try {
            SecretKeySpec key = new SecretKeySpec(secret.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return Base64.getEncoder().encodeToString(cipher.doFinal(cpf.getBytes()));
        } catch (Exception e) {
            throw new RuntimeException("Erro ao encriptar CPF", e);
        }
    }

    @Override
    public String convertToEntityAttribute(String cpfEncriptado) {
        if (cpfEncriptado == null) return null;
        try {
            SecretKeySpec key = new SecretKeySpec(secret.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            return new String(cipher.doFinal(Base64.getDecoder().decode(cpfEncriptado)));
        } catch (Exception e) {
            throw new RuntimeException("Erro ao desencriptar CPF", e);
        }
    }
}