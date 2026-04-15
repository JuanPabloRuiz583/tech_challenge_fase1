package br.com.fiap.Gestao.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(
     @NotBlank String loginUsername,
     @NotBlank   String senha){
}
