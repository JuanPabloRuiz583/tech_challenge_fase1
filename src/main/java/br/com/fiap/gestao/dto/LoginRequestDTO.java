package br.com.fiap.gestao.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(
     @NotBlank String loginUsername,
     @NotBlank   String senha){
}
