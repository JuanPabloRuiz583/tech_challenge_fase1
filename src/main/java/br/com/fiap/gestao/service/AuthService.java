package br.com.fiap.gestao.service;

import br.com.fiap.gestao.exception.CredenciaisInvalidasException;
import br.com.fiap.gestao.model.Usuario;
import br.com.fiap.gestao.repository.UsuarioRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService implements UserDetailsService {
    @Autowired
    private UsuarioRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public @NonNull UserDetails loadUserByUsername(@NonNull String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario nao encontrado"));
    }

    public Usuario autenticar(String email, String senha) {
        Usuario usuario = userRepository.findByEmail(email)
                .orElseThrow(() -> new CredenciaisInvalidasException("Email ou senha invalidos"));

        if (!passwordEncoder.matches(senha, usuario.getSenha())) {
            throw new CredenciaisInvalidasException("Email ou senha invalidos");
        }

        return usuario;
    }
}
