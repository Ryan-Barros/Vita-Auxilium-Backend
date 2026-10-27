package com.vitaauxilium.vitaauxilium.exception;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

/**
 * GlobalExceptionHandler é um @RestControllerAdvice puro (sem estado), então
 * pode ser testado sem subir o contexto do Spring — basta instanciar e chamar
 * os métodos diretamente. Isso é rápido e cobre exatamente o que a análise de
 * segurança apontou como incompleto: a lista de exceções tratadas hoje é curta
 * e não inclui MethodArgumentNotValidException, DataIntegrityViolationException
 * ou um handler genérico — o que estes testes deixam explícito por omissão.
 */
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("BadCredentialsException deve virar 401 com código INVALID_CREDENTIALS")
    void deveTratarBadCredentials() {
        ProblemDetail problemDetail = handler.handleBadCredentials(new BadCredentialsException("Credenciais inválidas"));

        assertEquals(HttpStatus.UNAUTHORIZED.value(), problemDetail.getStatus());
        assertEquals("INVALID_CREDENTIALS", problemDetail.getProperties().get("code"));
        assertEquals("Email ou senha inválidos", problemDetail.getDetail());
    }

    @Test
    @DisplayName("EntityNotFoundException deve virar 404 preservando a mensagem original")
    void deveTratarEntityNotFound() {
        ProblemDetail problemDetail = handler.handleEntityNotFound(new EntityNotFoundException("Ambiente não encontrado!"));

        assertEquals(HttpStatus.NOT_FOUND.value(), problemDetail.getStatus());
        assertEquals("Ambiente não encontrado!", problemDetail.getDetail());
        assertEquals("NOT_FOUND", problemDetail.getProperties().get("code"));
    }

    @Test
    @DisplayName("PlanLimitExceededException deve virar 422 (UNPROCESSABLE_CONTENT)")
    void deveTratarPlanLimitExceeded() {
        ProblemDetail problemDetail = handler.handlePlanLimitExceeded(
                new PlanLimitExceededException("Limite do plano FREE atingido"));

        assertEquals(HttpStatus.UNPROCESSABLE_CONTENT.value(), problemDetail.getStatus());
        assertEquals("PLAN_LIMIT_EXCEEDED", problemDetail.getProperties().get("code"));
    }

    @Test
    @DisplayName("AccessDeniedException (própria do domínio) deve virar 403")
    void deveTratarAccessDenied() {
        ProblemDetail problemDetail = handler.handleAccessDenied(
                new AccessDeniedException("Precisa ser um familiar para criar um ambiente!"));

        assertEquals(HttpStatus.FORBIDDEN.value(), problemDetail.getStatus());
        assertEquals("ACCESS_DENIED", problemDetail.getProperties().get("code"));
    }

    @Test
    @DisplayName("HttpMessageNotReadableException deve virar 400 com mensagem genérica (não ecoa detalhes internos)")
    void deveTratarMessageNotReadable() {
        ProblemDetail problemDetail = handler.handleMessageNotReadable(
                new HttpMessageNotReadableException(
                        "JSON parse error: unexpected token",
                        mock(HttpInputMessage.class)));

        assertEquals(HttpStatus.BAD_REQUEST.value(), problemDetail.getStatus());
        assertEquals("MALFORMED_REQUEST", problemDetail.getProperties().get("code"));
        // A mensagem exposta ao cliente deve ser genérica, não o erro de parsing bruto
        assertEquals("Corpo da requisição inválido ou perfil de usuário desconhecido", problemDetail.getDetail());
    }

    @Test
    @DisplayName("DuplicateResourceException deve virar 409 (CONFLICT)")
    void deveTratarDuplicateResource() {
        ProblemDetail problemDetail = handler.handleDuplicateResourceException(
                new DuplicateResourceException("Email já cadastrado"));

        assertEquals(HttpStatus.CONFLICT.value(), problemDetail.getStatus());
        assertEquals("DUPLICATE", problemDetail.getProperties().get("code"));
    }

    @Test
    @DisplayName("PropertyReferenceException deve virar 400 com código INVALID_SORT_PROPERTY")
    void deveTratarPropertyReferenceException() {
        org.springframework.data.core.PropertyReferenceException ex =
                new org.springframework.data.core.PropertyReferenceException(
                        "invalido",
                        org.springframework.data.core.TypeInformation.of(com.vitaauxilium.vitaauxilium.models.Activity.class),
                        java.util.List.of());

        ProblemDetail pd = handler.handlePropertyReferenceException(ex);

        assertEquals(HttpStatus.BAD_REQUEST.value(), pd.getStatus());
        assertEquals("INVALID_SORT_PROPERTY", pd.getProperties().get("code"));
        assertEquals("Propriedade de ordenação inválida: invalido", pd.getDetail());
    }
}
