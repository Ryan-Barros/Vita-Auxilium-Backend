package com.vitaauxilium.vitaauxilium.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void enviarEmailTexto(String para, String assunto, String mensagem) {
        SimpleMailMessage email = new SimpleMailMessage();

        email.setFrom("VitaAuxilium <vitaauxilium.sys@gmail.com>");
        email.setTo(para);
        email.setSubject(assunto);
        email.setText(mensagem);

        mailSender.send(email);
    }
}
