package br.com.fiap.Gestao.jwt;

public record Token(
        String token,
        String email
) {}
