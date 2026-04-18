package br.com.fiap.gestao.handler;

import br.com.fiap.gestao.dto.ProblemDetailDTO;
import br.com.fiap.gestao.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("GlobalExceptionHandler Tests")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/v1/test");
    }

    @Test
    @DisplayName("Should handle UsuarioNotFoundException with 404 status")
    void testHandleUsuarioNotFound() {
        // Given
        UsuarioNotFoundException exception = new UsuarioNotFoundException("Usuário não encontrado");

        // When
        ResponseEntity<ProblemDetailDTO> response = handler.handleUsuarioNotFound(exception, request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        ProblemDetailDTO body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.type()).contains("/usuario-nao-encontrado");
        assertThat(body.title()).isEqualTo("Usuário não encontrado");
        assertThat(body.status()).isEqualTo(404);
        assertThat(body.detail()).isEqualTo("Usuário não encontrado");
        assertThat(body.instance()).isEqualTo("/api/v1/test");
        assertThat(body.timestamp()).isBeforeOrEqualTo(Instant.now());
    }

    @Test
    @DisplayName("Should handle UsuarioDuplicadoException with 409 status")
    void testHandleUsuarioDuplicado() {
        // Given
        UsuarioDuplicadoException exception = new UsuarioDuplicadoException("Usuário já existe");

        // When
        ResponseEntity<ProblemDetailDTO> response = handler.handleUsuarioDuplicado(exception, request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        ProblemDetailDTO body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.type()).contains("/usuario-duplicado");
        assertThat(body.title()).isEqualTo("Usuário duplicado");
        assertThat(body.status()).isEqualTo(409);
        assertThat(body.detail()).isEqualTo("Usuário já existe");
    }

    @Test
    @DisplayName("Should handle SenhaInvalidaException with 400 status")
    void testHandleSenhaInvalida() {
        // Given
        SenhaInvalidaException exception = new SenhaInvalidaException("Senha muito fraca");

        // When
        ResponseEntity<ProblemDetailDTO> response = handler.handleSenhaInvalida(exception, request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ProblemDetailDTO body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.type()).contains("/senha-invalida");
        assertThat(body.title()).isEqualTo("Senha inválida");
        assertThat(body.status()).isEqualTo(400);
        assertThat(body.detail()).isEqualTo("Senha muito fraca");
    }

    @Test
    @DisplayName("Should handle CredenciaisInvalidasException with 401 status")
    void testHandleCredenciaisInvalidas() {
        // Given
        CredenciaisInvalidasException exception = new CredenciaisInvalidasException("Email ou senha incorretos");

        // When
        ResponseEntity<ProblemDetailDTO> response = handler.handleCredenciaisInvalidas(exception, request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        ProblemDetailDTO body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.type()).contains("/credenciais-invalidas");
        assertThat(body.title()).isEqualTo("Credenciais inválidas");
        assertThat(body.status()).isEqualTo(401);
        assertThat(body.detail()).isEqualTo("Email ou senha incorretos");
    }

    @Test
    @DisplayName("Should handle MethodArgumentNotValidException with validation errors")
    void testHandleValidation() {
        // Given
        MethodParameter methodParameter = mock(MethodParameter.class);
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "test");
        bindingResult.addError(new FieldError("test", "email", "Email é obrigatório"));
        bindingResult.addError(new FieldError("test", "nome", "Nome deve ter pelo menos 2 caracteres"));
        
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(methodParameter, bindingResult);

        // When
        ResponseEntity<ProblemDetailDTO> response = handler.handleValidation(exception, request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ProblemDetailDTO body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.type()).contains("/validacao-falhou");
        assertThat(body.title()).isEqualTo("Erro de validação");
        assertThat(body.status()).isEqualTo(400);
        assertThat(body.detail()).isEqualTo("Um ou mais campos contêm valores inválidos");
        assertThat(body.fieldErrors()).isNotNull();
        assertThat(body.fieldErrors()).containsKey("email");
        assertThat(body.fieldErrors()).containsKey("nome");
    }

    @Test
    @DisplayName("Should handle generic Exception with 500 status")
    void testHandleGeneric() {
        // Given
        Exception exception = new RuntimeException("Erro inesperado");

        // When
        ResponseEntity<ProblemDetailDTO> response = handler.handleGeneric(exception, request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        ProblemDetailDTO body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.type()).contains("/erro-interno");
        assertThat(body.title()).isEqualTo("Erro interno do servidor");
        assertThat(body.status()).isEqualTo(500);
        assertThat(body.detail()).isEqualTo("Ocorreu um erro inesperado. Por favor, tente novamente mais tarde.");
        assertThat(body.instance()).isEqualTo("/api/v1/test");
    }
}
