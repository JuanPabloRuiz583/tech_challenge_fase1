package br.com.fiap.gestao.dto;

import java.time.LocalDateTime;
import java.util.Map;

public record ApiErrorDTO(
        LocalDateTime timeError,
        String message,
        String error,
        String path,
        Map<String,String> fieldErrors

) {
}
