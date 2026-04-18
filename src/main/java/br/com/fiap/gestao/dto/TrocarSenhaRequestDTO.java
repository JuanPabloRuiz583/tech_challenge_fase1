package br.com.fiap.gestao.dto;

public record TrocarSenhaRequestDTO(
        String senhaAtual,
        String novaSenha,
        String confirmacaoNovaSenha
) {
}
