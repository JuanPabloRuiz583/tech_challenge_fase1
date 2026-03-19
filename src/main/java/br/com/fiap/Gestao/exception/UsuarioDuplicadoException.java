package br.com.fiap.Gestao.exception;

public class UsuarioDuplicadoException extends RuntimeException {
    public UsuarioDuplicadoException(String message) {
        super(message);
    }
}
