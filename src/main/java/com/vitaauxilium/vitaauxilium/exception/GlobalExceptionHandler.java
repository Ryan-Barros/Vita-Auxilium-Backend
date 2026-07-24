package com.vitaauxilium.vitaauxilium.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    public ProblemDetail handleBadCredentials(BadCredentialsException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNAUTHORIZED,
                "Email ou senha inválidos"
        );

        problemDetail.setTitle("Falha na Autenticação");
        problemDetail.setProperty("code", "INVALID_CREDENTIALS");
        problemDetail.setProperty("timestamp", java.time.Instant.now());

        return problemDetail;
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ProblemDetail handleEntityNotFound(EntityNotFoundException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                ex.getMessage()
        );

        problemDetail.setTitle("Recurso não encontrado");
        problemDetail.setProperty("code", "NOT_FOUND");
        problemDetail.setProperty("timestamp", java.time.Instant.now());

        return problemDetail;
    }

    @ExceptionHandler(PlanLimitExceededException.class)
    public ProblemDetail handlePlanLimitExceeded(PlanLimitExceededException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNPROCESSABLE_CONTENT,
                ex.getMessage()
        );

        problemDetail.setTitle("Limite do Plano Atingido");
        problemDetail.setProperty("code", "PLAN_LIMIT_EXCEEDED");
        problemDetail.setProperty("timestamp", Instant.now());

        return problemDetail;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDenied(AccessDeniedException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.FORBIDDEN,
                ex.getMessage()
        );

        problemDetail.setTitle("Acesso negado");
        problemDetail.setProperty("code", "ACCESS_DENIED");
        problemDetail.setProperty("timestamp", java.time.Instant.now());
        return problemDetail;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleMessageNotReadable(HttpMessageNotReadableException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Corpo da requisição inválido ou perfil de usuário desconhecido"
        );
        problemDetail.setTitle("Requisição Malformada");
        problemDetail.setProperty("code", "MALFORMED_REQUEST");
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ProblemDetail handleDuplicateResourceException(DuplicateResourceException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                ex.getMessage()
        );
        problemDetail.setTitle("Recurso duplicado");
        problemDetail.setProperty("code", "DUPLICATE");
        problemDetail.setProperty("timestamp", java.time.Instant.now());
        return problemDetail;
    }
}
