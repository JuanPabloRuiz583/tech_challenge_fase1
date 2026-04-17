package br.com.fiap.gestao.convertordto;

import br.com.fiap.gestao.dto.UsuarioRequestDTO;
import br.com.fiap.gestao.dto.UsuarioUpdateDTO;
import br.com.fiap.gestao.model.Endereco;
import br.com.fiap.gestao.model.TipoUsuario;
import br.com.fiap.gestao.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UsuarioMapper Tests")
class UsuarioMapperTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioMapper usuarioMapper;

    private UsuarioRequestDTO usuarioRequestDTO;
    private UsuarioUpdateDTO usuarioUpdateDTO;
    private UsuarioRequestDTO.EnderecoDTO enderecoDTO;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        enderecoDTO = new UsuarioRequestDTO.EnderecoDTO(
                "Rua Teste",
                "123",
                "São Paulo",
                "SP",
                "01310-100"
        );

        usuarioRequestDTO = new UsuarioRequestDTO(
                "João Silva",
                "joao@example.com",
                "joao",
                "senha123",
                TipoUsuario.CLIENTE,
                enderecoDTO
        );

        usuarioUpdateDTO = new UsuarioUpdateDTO(
                "João Silva Updated",
                "joao@example.com",
                "joao",
                TipoUsuario.CLIENTE,
                enderecoDTO
        );

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("João Silva");
        usuario.setEmail("joao@example.com");
        usuario.setLoginUsername("joao");
        usuario.setSenha("senhaEncriptada");
        usuario.setTipoUsuario(TipoUsuario.CLIENTE);

        Endereco endereco = new Endereco();
        endereco.setRua("Rua Teste");
        endereco.setNumero("123");
        endereco.setCidade("São Paulo");
        endereco.setEstado("SP");
        endereco.setCep("01310-100");
        usuario.setEndereco(endereco);
    }

    @Test
    @DisplayName("Should convert UsuarioRequestDTO to Entity")
    void testToEntity() {
        when(passwordEncoder.encode("senha123")).thenReturn("senhaEncriptada");

        Usuario result = usuarioMapper.toEntity(usuarioRequestDTO);

        assertNotNull(result);
        assertEquals("João Silva", result.getNome());
        assertEquals("joao@example.com", result.getEmail());
        assertEquals("joao", result.getLoginUsername());
        assertEquals("senhaEncriptada", result.getSenha());
        assertEquals(TipoUsuario.CLIENTE, result.getTipoUsuario());
        
        assertNotNull(result.getEndereco());
        assertEquals("Rua Teste", result.getEndereco().getRua());
        assertEquals("123", result.getEndereco().getNumero());
        assertEquals("São Paulo", result.getEndereco().getCidade());
        assertEquals("SP", result.getEndereco().getEstado());
        assertEquals("01310-100", result.getEndereco().getCep());
    }

    @Test
    @DisplayName("Should apply updates from UsuarioRequestDTO to existing entity")
    void testApplyUpdatesFromUsuarioRequestDTO() {
        when(passwordEncoder.encode("senha123")).thenReturn("novaSenhaEncriptada");

        usuarioMapper.applyUpdates(usuario, usuarioRequestDTO);

        assertEquals("João Silva", usuario.getNome());
        assertEquals("joao@example.com", usuario.getEmail());
        assertEquals("joao", usuario.getLoginUsername());
        assertEquals("novaSenhaEncriptada", usuario.getSenha());
        assertEquals(TipoUsuario.CLIENTE, usuario.getTipoUsuario());
        
        assertNotNull(usuario.getEndereco());
        assertEquals("Rua Teste", usuario.getEndereco().getRua());
        assertEquals("123", usuario.getEndereco().getNumero());
        assertEquals("São Paulo", usuario.getEndereco().getCidade());
        assertEquals("SP", usuario.getEndereco().getEstado());
        assertEquals("01310-100", usuario.getEndereco().getCep());
    }

    @Test
    @DisplayName("Should apply updates from UsuarioUpdateDTO to existing entity")
    void testApplyUpdatesFromUsuarioUpdateDTO() {
        usuarioMapper.applyUpdates(usuario, usuarioUpdateDTO);

        assertEquals("João Silva Updated", usuario.getNome());
        assertEquals("joao@example.com", usuario.getEmail());
        assertEquals("joao", usuario.getLoginUsername());
        assertEquals(TipoUsuario.CLIENTE, usuario.getTipoUsuario());
        
        assertNotNull(usuario.getEndereco());
        assertEquals("Rua Teste", usuario.getEndereco().getRua());
        assertEquals("123", usuario.getEndereco().getNumero());
        assertEquals("São Paulo", usuario.getEndereco().getCidade());
        assertEquals("SP", usuario.getEndereco().getEstado());
        assertEquals("01310-100", usuario.getEndereco().getCep());
    }

    @Test
    @DisplayName("Should convert Usuario to UsuarioUpdateDTO")
    void testToUpdateDTO() {
        UsuarioUpdateDTO result = usuarioMapper.toUpdateDTO(usuario);

        assertNotNull(result);
        assertEquals("João Silva", result.nome());
        assertEquals("joao@example.com", result.email());
        assertEquals("joao", result.loginUsername());
        assertEquals(TipoUsuario.CLIENTE, result.tipoUsuario());
        
        assertNotNull(result.endereco());
        assertEquals("Rua Teste", result.endereco().rua());
        assertEquals("123", result.endereco().numero());
        assertEquals("São Paulo", result.endereco().cidade());
        assertEquals("SP", result.endereco().estado());
        assertEquals("01310-100", result.endereco().cep());
    }

    @Test
    @DisplayName("Should handle different TipoUsuario")
    void testWithDonoRestaurante() {
        UsuarioRequestDTO dtoDonoRestaurante = new UsuarioRequestDTO(
                "Maria Silva",
                "maria@example.com",
                "maria",
                "senha456",
                TipoUsuario.DONO_RESTAURANTE,
                enderecoDTO
        );

        when(passwordEncoder.encode("senha456")).thenReturn("senhaEncriptadaMaria");

        Usuario result = usuarioMapper.toEntity(dtoDonoRestaurante);

        assertEquals(TipoUsuario.DONO_RESTAURANTE, result.getTipoUsuario());
        assertEquals("Maria Silva", result.getNome());
        assertEquals("senhaEncriptadaMaria", result.getSenha());
    }

    @Test
    @DisplayName("Should handle null endereco gracefully")
    void testToUpdateDTOWithNullEndereco() {
        usuario.setEndereco(null);

        assertThrows(NullPointerException.class, () -> {
            usuarioMapper.toUpdateDTO(usuario);
        });
    }
}
