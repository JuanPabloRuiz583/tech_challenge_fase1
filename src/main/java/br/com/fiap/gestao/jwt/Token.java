package br.com.fiap.gestao.jwt;

public record Token(
        String token,
        String email
) {}
