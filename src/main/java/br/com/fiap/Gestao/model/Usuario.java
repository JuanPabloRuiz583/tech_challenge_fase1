package br.com.fiap.Gestao.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String loginUsername;

    private String senha;

    private LocalDateTime dataUltimaAlteracao;

    @Enumerated(EnumType.STRING)
    private TipoUsuario tipoUsuario;

    @Embedded
    private Endereco endereco;
}
