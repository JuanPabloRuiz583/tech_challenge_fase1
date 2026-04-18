package br.com.fiap.gestao.exception;

public class UsernameNotFoundException extends RuntimeException {
    public UsernameNotFoundException(String message) {
        super(message);
    }
}
