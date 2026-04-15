package br.com.fiap.Gestao.handler;

import br.com.fiap.Gestao.dto.ApiErrorDTO;
import br.com.fiap.Gestao.exception.SenhaInvalidaException;
import br.com.fiap.Gestao.exception.UsuarioDuplicadoException;
import br.com.fiap.Gestao.exception.UsuarioNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(UsuarioNotFoundException.class)
    public ResponseEntity<ApiErrorDTO> handleUsuarioNotFound(
            UsuarioNotFoundException ex,
            HttpServletRequest request
    ) {
        ApiErrorDTO body = new ApiErrorDTO(
                LocalDateTime.now(),
                ex.getMessage(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                request.getRequestURI(),
                null
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(UsuarioDuplicadoException.class)
    public ResponseEntity<ApiErrorDTO> handleUsuarioDuplicado(
            UsuarioDuplicadoException ex,
            HttpServletRequest request
    ) {
        ApiErrorDTO body = new ApiErrorDTO(
                LocalDateTime.now(),
                ex.getMessage(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                request.getRequestURI(),
                null
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(SenhaInvalidaException.class)
    public ResponseEntity<ApiErrorDTO> handleSenhaInvalida(
            SenhaInvalidaException ex,
            HttpServletRequest request
    ) {
        ApiErrorDTO body = new ApiErrorDTO(
                LocalDateTime.now(),
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                request.getRequestURI(),
                null
        );
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorDTO> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        for (FieldError err : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(err.getField(), err.getDefaultMessage());
        }

        ApiErrorDTO body = new ApiErrorDTO(
                LocalDateTime.now(),
                "Erro de validacao",
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                request.getRequestURI(),
                fieldErrors
        );
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorDTO> handleGeneric(
            Exception ex,
            HttpServletRequest request
    ) {
        ApiErrorDTO body = new ApiErrorDTO(
                LocalDateTime.now(),
                "Ocorreu um erro inesperado",
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                request.getRequestURI(),
                null
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
