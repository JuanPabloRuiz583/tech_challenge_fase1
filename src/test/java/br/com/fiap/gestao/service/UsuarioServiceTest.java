package br.com.fiap.gestao.service;

import br.com.fiap.gestao.convertordto.UsuarioMapper;
import br.com.fiap.gestao.dto.TrocarSenhaRequestDTO;
import br.com.fiap.gestao.dto.UsuarioRequestDTO;
import br.com.fiap.gestao.dto.UsuarioUpdateDTO;
import br.com.fiap.gestao.exception.SenhaInvalidaException;
import br.com.fiap.gestao.exception.UsuarioDuplicadoException;
import br.com.fiap.gestao.exception.UsuarioNotFoundException;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UsuarioService Tests")
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private UsuarioMapper usuarioMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuario;
    private UsuarioRequestDTO usuarioRequestDTO;
    private UsuarioUpdateDTO usuarioUpdateDTO;
    private Endereco endereco;

    @BeforeEach
    void setUp() {
        endereco = new Endereco();
        endereco.setRua("Rua Teste");
        endereco.setNumero("123");
        endereco.setCidade("São Paulo");
        endereco.setEstado("SP");
        endereco.setCep("01310-100");

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("João Silva");
        usuario.setEmail("joao@example.com");
        usuario.setLoginUsername("joao");
        usuario.setSenha("senha123");
        usuario.setTipoUsuario(TipoUsuario.CLIENTE);
        usuario.setEndereco(endereco);
        usuario.setDataUltimaAlteracao(LocalDateTime.now());

        usuarioRequestDTO = new UsuarioRequestDTO(
                "João Silva",
                "joao@example.com",
                "joao",
                "senha123",
                TipoUsuario.CLIENTE,
                new UsuarioRequestDTO.EnderecoDTO(
                        "Rua Teste",
                        "123",
                        "São Paulo",
                        "SP",
                        "01310-100"
                )
        );

        usuarioUpdateDTO = new UsuarioUpdateDTO(
                "João Silva Updated",
                "joao@example.com",
                "joao",
                TipoUsuario.CLIENTE,
                new UsuarioRequestDTO.EnderecoDTO(
                        "Rua Teste",
                        "123",
                        "São Paulo",
                        "SP",
                        "01310-100"
                )
        );
    }

    // Tests para getAll
    @Test
    @DisplayName("Should get all users with default pagination")
    void testGetAllWithDefaultPagination() {
        when(usuarioRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(usuario)));

        List<Usuario> result = usuarioService.getAll(1, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(usuario.getId(), result.get(0).getId());
        verify(usuarioRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("Should adjust invalid size to 10")
    void testGetAllWithInvalidSize() {
        when(usuarioRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(usuario)));

        usuarioService.getAll(1, 0);

        verify(usuarioRepository).findAll(any(Pageable.class));
    }

    // Tests para getById
    @Test
    @DisplayName("Should get user by id successfully")
    void testGetByIdSuccess() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        Usuario result = usuarioService.getById(1L);

        assertNotNull(result);
        assertEquals(usuario.getId(), result.getId());
        assertEquals(usuario.getEmail(), result.getEmail());
        verify(usuarioRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw UsuarioNotFoundException when user not found")
    void testGetByIdNotFound() {
        when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(UsuarioNotFoundException.class, () -> usuarioService.getById(999L));
        verify(usuarioRepository, times(1)).findById(999L);
    }

    // Tests para create
    @Test
    @DisplayName("Should create user successfully")
    void testCreateSuccess() {
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(usuarioRepository.findByLoginUsername(anyString())).thenReturn(Optional.empty());
        when(usuarioMapper.toEntity(usuarioRequestDTO)).thenReturn(usuario);

        usuarioService.create(usuarioRequestDTO);

        verify(usuarioRepository, times(1)).findByEmail(usuarioRequestDTO.email());
        verify(usuarioRepository, times(1)).findByLoginUsername(usuarioRequestDTO.loginUsername());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Should throw UsuarioDuplicadoException when email already exists")
    void testCreateWithDuplicateEmail() {
        when(usuarioRepository.findByEmail(usuarioRequestDTO.email()))
                .thenReturn(Optional.of(usuario));

        assertThrows(UsuarioDuplicadoException.class, () -> usuarioService.create(usuarioRequestDTO));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Should throw UsuarioDuplicadoException when loginUsername already exists")
    void testCreateWithDuplicateLoginUsername() {
        when(usuarioRepository.findByEmail(usuarioRequestDTO.email())).thenReturn(Optional.empty());
        when(usuarioRepository.findByLoginUsername(usuarioRequestDTO.loginUsername()))
                .thenReturn(Optional.of(usuario));

        assertThrows(UsuarioDuplicadoException.class, () -> usuarioService.create(usuarioRequestDTO));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    // Tests para update
    @Test
    @DisplayName("Should update user successfully")
    void testUpdateSuccess() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(usuarioRepository.findByLoginUsername(anyString())).thenReturn(Optional.empty());
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        Usuario result = usuarioService.update(1L, usuarioUpdateDTO);

        assertNotNull(result);
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Should throw UsuarioNotFoundException when updating non-existent user")
    void testUpdateUserNotFound() {
        when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(UsuarioNotFoundException.class, () -> usuarioService.update(999L, usuarioUpdateDTO));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Should throw UsuarioDuplicadoException when updating with duplicate email")
    void testUpdateWithDuplicateEmail() {
        Usuario otherUser = new Usuario();
        otherUser.setId(2L);
        otherUser.setEmail(usuarioUpdateDTO.email());

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.findByEmail(usuarioUpdateDTO.email())).thenReturn(Optional.of(otherUser));

        assertThrows(UsuarioDuplicadoException.class, () -> usuarioService.update(1L, usuarioUpdateDTO));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    // Tests para trocarSenha
    @Test
    @DisplayName("Should change password successfully")
    void testTrocarSenhaSuccess() {
        String senhaAtual = "senhaAnterior";
        String novaSenha = "novaSenha123";

        TrocarSenhaRequestDTO dto = new TrocarSenhaRequestDTO(
                senhaAtual,
                novaSenha,
                novaSenha
        );

        Usuario usuarioComSenhaEncriptada = new Usuario();
        usuarioComSenhaEncriptada.setId(1L);
        usuarioComSenhaEncriptada.setSenha("hashedPassword");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioComSenhaEncriptada));
        when(passwordEncoder.matches(senhaAtual, usuarioComSenhaEncriptada.getSenha())).thenReturn(true);
        when(passwordEncoder.encode(novaSenha)).thenReturn("hashedNewPassword");

        usuarioService.trocarSenha(1L, dto);

        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Should throw UsuarioNotFoundException when changing password for non-existent user")
    void testTrocarSenhaUserNotFound() {
        TrocarSenhaRequestDTO dto = new TrocarSenhaRequestDTO("senhaAtual", "novaSenha", "novaSenha");

        when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(UsuarioNotFoundException.class, () -> usuarioService.trocarSenha(999L, dto));
    }

    @Test
    @DisplayName("Should throw SenhaInvalidaException when current password is wrong")
    void testTrocarSenhaWithWrongCurrentPassword() {
        TrocarSenhaRequestDTO dto = new TrocarSenhaRequestDTO("senhaErrada", "novaSenha", "novaSenha");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("senhaErrada", usuario.getSenha())).thenReturn(false);

        assertThrows(SenhaInvalidaException.class, () -> usuarioService.trocarSenha(1L, dto));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Should throw SenhaInvalidaException when new passwords don't match")
    void testTrocarSenhaPasswordsMismatch() {
        TrocarSenhaRequestDTO dto = new TrocarSenhaRequestDTO("senhaAtual", "novaSenha1", "novaSenha2");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("senhaAtual", usuario.getSenha())).thenReturn(true);

        assertThrows(SenhaInvalidaException.class, () -> usuarioService.trocarSenha(1L, dto));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    // Tests para delete
    @Test
    @DisplayName("Should delete user successfully")
    void testDeleteSuccess() {
        when(usuarioRepository.existsById(1L)).thenReturn(true);

        usuarioService.delete(1L);

        verify(usuarioRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw UsuarioNotFoundException when deleting non-existent user")
    void testDeleteUserNotFound() {
        when(usuarioRepository.existsById(999L)).thenReturn(false);

        assertThrows(UsuarioNotFoundException.class, () -> usuarioService.delete(999L));
        verify(usuarioRepository, never()).deleteById(any());
    }

    // Tests para searchByNome
    @Test
    @DisplayName("Should search users by name successfully")
    void testSearchByNomeSuccess() {
        when(usuarioRepository.findByNomeContainingIgnoreCase("João"))
                .thenReturn(List.of(usuario));
        when(usuarioMapper.toUpdateDTO(usuario)).thenReturn(usuarioUpdateDTO);

        List<UsuarioUpdateDTO> result = usuarioService.searchByNome("João");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(usuarioRepository, times(1)).findByNomeContainingIgnoreCase("João");
    }

    @Test
    @DisplayName("Should return empty list when search name is empty")
    void testSearchByNomeEmpty() {
        List<UsuarioUpdateDTO> result = usuarioService.searchByNome("");

        assertNotNull(result);
        assertEquals(0, result.size());
        verify(usuarioRepository, never()).findByNomeContainingIgnoreCase(anyString());
    }

    @Test
    @DisplayName("Should return empty list when search name is null")
    void testSearchByNomeNull() {
        List<UsuarioUpdateDTO> result = usuarioService.searchByNome(null);

        assertNotNull(result);
        assertEquals(0, result.size());
        verify(usuarioRepository, never()).findByNomeContainingIgnoreCase(anyString());
    }

    @Test
    @DisplayName("Should trim search name and search by it")
    void testSearchByNomeWithWhitespace() {
        when(usuarioRepository.findByNomeContainingIgnoreCase("João"))
                .thenReturn(List.of(usuario));
        when(usuarioMapper.toUpdateDTO(usuario)).thenReturn(usuarioUpdateDTO);

        List<UsuarioUpdateDTO> result = usuarioService.searchByNome("  João  ");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(usuarioRepository, times(1)).findByNomeContainingIgnoreCase("João");
    }
}


