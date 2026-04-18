package br.com.fiap.gestao.handler;

import br.com.fiap.gestao.dto.ProblemDetailDTO;
import br.com.fiap.gestao.exception.CredenciaisInvalidasException;
import br.com.fiap.gestao.exception.SenhaInvalidaException;
import br.com.fiap.gestao.exception.UsuarioDuplicadoException;
import br.com.fiap.gestao.exception.UsuarioNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    
    private static final String API_ERROR_TYPE = "https://api.gestao.com/erros";

    @ExceptionHandler(UsuarioNotFoundException.class)
    public ResponseEntity<ProblemDetailDTO> handleUsuarioNotFound(
            UsuarioNotFoundException ex,
            HttpServletRequest request
    ) {
        ProblemDetailDTO body = new ProblemDetailDTO(
                API_ERROR_TYPE + "/usuario-nao-encontrado",
                "Usuário não encontrado",
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                request.getRequestURI(),
                Instant.now(),
                null
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(UsuarioDuplicadoException.class)
    public ResponseEntity<ProblemDetailDTO> handleUsuarioDuplicado(
            UsuarioDuplicadoException ex,
            HttpServletRequest request
    ) {
        ProblemDetailDTO body = new ProblemDetailDTO(
                API_ERROR_TYPE + "/usuario-duplicado",
                "Usuário duplicado",
                HttpStatus.CONFLICT.value(),
                ex.getMessage(),
                request.getRequestURI(),
                Instant.now(),
                null
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(SenhaInvalidaException.class)
    public ResponseEntity<ProblemDetailDTO> handleSenhaInvalida(
            SenhaInvalidaException ex,
            HttpServletRequest request
    ) {
        ProblemDetailDTO body = new ProblemDetailDTO(
                API_ERROR_TYPE + "/senha-invalida",
                "Senha inválida",
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                request.getRequestURI(),
                Instant.now(),
                null
        );
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(CredenciaisInvalidasException.class)
    public ResponseEntity<ProblemDetailDTO> handleCredenciaisInvalidas(
            CredenciaisInvalidasException ex,
            HttpServletRequest request
    ) {
        ProblemDetailDTO body = new ProblemDetailDTO(
                API_ERROR_TYPE + "/credenciais-invalidas",
                "Credenciais inválidas",
                HttpStatus.UNAUTHORIZED.value(),
                ex.getMessage(),
                request.getRequestURI(),
                Instant.now(),
                null
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetailDTO> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        for (FieldError err : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(err.getField(), err.getDefaultMessage());
        }

        ProblemDetailDTO body = new ProblemDetailDTO(
                API_ERROR_TYPE + "/validacao-falhou",
                "Erro de validação",
                HttpStatus.BAD_REQUEST.value(),
                "Um ou mais campos contêm valores inválidos",
                request.getRequestURI(),
                Instant.now(),
                fieldErrors
        );
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetailDTO> handleGeneric(
            Exception ex,
            HttpServletRequest request
    ) {
        ProblemDetailDTO body = new ProblemDetailDTO(
                API_ERROR_TYPE + "/erro-interno",
                "Erro interno do servidor",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Ocorreu um erro inesperado. Por favor, tente novamente mais tarde.",
                request.getRequestURI(),
                Instant.now(),
                null
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
