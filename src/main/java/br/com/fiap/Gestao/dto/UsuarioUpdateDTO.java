package br.com.fiap.Gestao.dto;

import br.com.fiap.Gestao.model.TipoUsuario;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

public record UsuarioUpdateDTO(
        @NotBlank(message = "nome obrigatorio")
        @Size(min = 3, max = 120, message = "nome deve ter entre 3 e 120 caracteres")
        String nome,

        @NotBlank(message = "email obrigatorio")
        @Email(message = "email invalido")
        @Size(max = 150, message = "email deve ter no maximo 150 caracteres")
        String email,

        @NotBlank(message = "loginUsername obrigatorio")
        @Size(min = 3, max = 50, message = "loginUsername deve ter entre 3 e 50 caracteres")
        String loginUsername,

        @NotNull(message = "tipoUsuario obrigatorio")
        TipoUsuario tipoUsuario,

        @NotNull(message = "endereco obrigatorio")
        @Valid
        UsuarioRequestDTO.EnderecoDTO endereco
) {
    public record EnderecoDTO(
            @NotBlank(message = "rua obrigatoria")
            @Size(max = 120, message = "rua deve ter no maximo 120 caracteres")
            String rua,

            @NotBlank(message = "numero obrigatorio")
            @Size(max = 10, message = "numero deve ter no maximo 10 caracteres")
            String numero,

            @NotBlank(message = "cidade obrigatoria")
            @Size(max = 80, message = "cidade deve ter no maximo 80 caracteres")
            String cidade,

            @NotBlank(message = "estado obrigatorio")
            @Size(min = 2, max = 2, message = "estado deve ter 2 caracteres")
            String estado,

            @NotBlank(message = "cep obrigatorio")
            @Pattern(regexp = "^\\d{5}-?\\d{3}$", message = "cep deve estar no formato 12345-678 ou 12345678")
            String cep
    ) {}
    }
