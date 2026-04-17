package br.com.fiap.gestao.repository;

import br.com.fiap.gestao.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByLoginUsername(String loginUsername);
    List<Usuario> findByNomeContainingIgnoreCase(String nome);
}
