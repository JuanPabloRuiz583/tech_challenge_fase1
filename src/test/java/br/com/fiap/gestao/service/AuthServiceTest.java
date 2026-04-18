package br.com.fiap.gestao.service;

import br.com.fiap.gestao.exception.CredenciaisInvalidasException;
import br.com.fiap.gestao.model.Endereco;
import br.com.fiap.gestao.model.TipoUsuario;
import br.com.fiap.gestao.model.Usuario;
import br.com.fiap.gestao.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Tests")
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private Usuario usuario;
    private String email;
    private String senha;

    @BeforeEach
    void setUp() {
        email = "joao@example.com";
        senha = "senha123";

        Endereco endereco = new Endereco();
        endereco.setRua("Rua Teste");
        endereco.setNumero("123");
        endereco.setCidade("São Paulo");
        endereco.setEstado("SP");
        endereco.setCep("01310-100");

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("João Silva");
        usuario.setEmail(email);
        usuario.setLoginUsername("joao");
        usuario.setSenha("hashedPassword");
        usuario.setTipoUsuario(TipoUsuario.CLIENTE);
        usuario.setEndereco(endereco);
        usuario.setDataUltimaAlteracao(LocalDateTime.now());
    }

    // Tests para loadUserByUsername
    @Test
    @DisplayName("Should load user by username successfully")
    void testLoadUserByUsernameSuccess() {
        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuario));

        UserDetails result = authService.loadUserByUsername(email);

        assertNotNull(result);
        assertEquals(email, result.getUsername());
        verify(usuarioRepository, times(1)).findByEmail(email);
    }

    @Test
    @DisplayName("Should throw UsernameNotFoundException when user not found")
    void testLoadUserByUsernameNotFound() {
        when(usuarioRepository.findByEmail("nonexistent@example.com"))
                .thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> authService.loadUserByUsername("nonexistent@example.com"));
        verify(usuarioRepository, times(1)).findByEmail("nonexistent@example.com");
    }

    // Tests para autenticar
    @Test
    @DisplayName("Should authenticate user successfully")
    void testAutenticarSuccess() {
        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches(senha, usuario.getPassword())).thenReturn(true);

        Usuario result = authService.autenticar(email, senha);

        assertNotNull(result);
        assertEquals(usuario.getId(), result.getId());
        assertEquals(email, result.getEmail());
        verify(usuarioRepository, times(1)).findByEmail(email);
        verify(passwordEncoder, times(1)).matches(senha, usuario.getPassword());
    }

    @Test
    @DisplayName("Should throw CredenciaisInvalidasException when email not found")
    void testAutenticarEmailNotFound() {
        when(usuarioRepository.findByEmail("wrong@example.com"))
                .thenReturn(Optional.empty());

        assertThrows(CredenciaisInvalidasException.class,
                () -> authService.autenticar("wrong@example.com", senha));
        verify(usuarioRepository, times(1)).findByEmail("wrong@example.com");
        verify(passwordEncoder, never()).matches(any(), any());
    }

    @Test
    @DisplayName("Should throw CredenciaisInvalidasException when password is wrong")
    void testAutenticarWrongPassword() {
        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("wrongPassword", usuario.getPassword())).thenReturn(false);

        assertThrows(CredenciaisInvalidasException.class,
                () -> authService.autenticar(email, "wrongPassword"));
        verify(usuarioRepository, times(1)).findByEmail(email);
        verify(passwordEncoder, times(1)).matches("wrongPassword", usuario.getPassword());
    }

    @Test
    @DisplayName("Should return usuario with correct authorities")
    void testAutenticarReturnsCorrectAuthorities() {
        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches(senha, usuario.getPassword())).thenReturn(true);

        Usuario result = authService.autenticar(email, senha);

        assertNotNull(result.getAuthorities());
        assertTrue(result.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals(TipoUsuario.CLIENTE.toString())));
    }
}

