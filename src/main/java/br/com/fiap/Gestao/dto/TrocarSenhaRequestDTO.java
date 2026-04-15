package br.com.fiap.Gestao.dto;

public record TrocarSenhaRequestDTO(
        String senhaAtual,
        String novaSenha,
        String confirmacaoNovaSenha
) {
}
