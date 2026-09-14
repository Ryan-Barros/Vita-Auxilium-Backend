package com.vitaauxilium.vitaauxilium.utils;

public enum EmailTemplate {
    WELCOME("Bem-vindo ao VitaAuxilium!", 
            "Bem-vindo(a), %s!\n\n" +
            "Sua conta no VitaAuxilium foi criada com sucesso.\n" +
            "Agora você pode acessar nossa plataforma com suas credenciais.\n\n" +
            "Equipe VitaAuxilium");

    private final String subject;
    private final String bodyTemplate;

    EmailTemplate(String subject, String bodyTemplate) {
        this.subject = subject;
        this.bodyTemplate = bodyTemplate;
    }

    public String getSubject() {
        return subject;
    }

    public String formatBody(Object... args) {
        return String.format(bodyTemplate, args);
    }
}
