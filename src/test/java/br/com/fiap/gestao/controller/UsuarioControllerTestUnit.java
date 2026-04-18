package br.com.fiap.gestao.controller;

import br.com.fiap.gestao.dto.TrocarSenhaRequestDTO;
import br.com.fiap.gestao.dto.UsuarioRequestDTO;
import br.com.fiap.gestao.dto.UsuarioUpdateDTO;
import br.com.fiap.gestao.exception.UsuarioNotFoundException;
import br.com.fiap.gestao.model.Endereco;
import br.com.fiap.gestao.model.TipoUsuario;
import br.com.fiap.gestao.model.Usuario;
import br.com.fiap.gestao.service.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("UsuarioController Unit Tests")
class UsuarioControllerTestUnit {

    private MockMvc mockMvc;
    private UsuarioService usuarioService;
    private ObjectMapper objectMapper;

    private Usuario usuario;
    private UsuarioRequestDTO usuarioRequestDTO;
    private UsuarioUpdateDTO usuarioUpdateDTO;
    private UsuarioRequestDTO.EnderecoDTO enderecoDTO;

    @BeforeEach
    void setUp() {
        usuarioService = mock(UsuarioService.class);
        objectMapper = new ObjectMapper();
        
        UsuarioController controller = new UsuarioController(usuarioService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        
        enderecoDTO = new UsuarioRequestDTO.EnderecoDTO(
                "Rua Teste",
                "123",
                "São Paulo",
                "SP",
                "01310-100"
        );

        Endereco endereco = new Endereco();
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
        usuario.setSenha("senhaEncriptada");
        usuario.setTipoUsuario(TipoUsuario.CLIENTE);
        usuario.setEndereco(endereco);
        usuario.setDataUltimaAlteracao(LocalDateTime.now());

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
    }

    @Test
    @DisplayName("Should get all usuarios with default pagination")
    void testGetAllUsuariosDefault() throws Exception {
        when(usuarioService.getAll(1, 10))
                .thenReturn(List.of(usuario));

        mockMvc.perform(get("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].email").value("joao@example.com"));

        verify(usuarioService, times(1)).getAll(1, 10);
    }

    @Test
    @DisplayName("Should get usuario by id successfully")
    void testGetByIdSuccess() throws Exception {
        when(usuarioService.getById(1L))
                .thenReturn(usuario);

        mockMvc.perform(get("/api/v1/usuarios/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("joao@example.com"))
                .andExpect(jsonPath("$.nome").value("João Silva"));

        verify(usuarioService, times(1)).getById(1L);
    }

    @Test
    @DisplayName("Should return 404 when usuario not found")
    void testGetByIdNotFound() throws Exception {
        when(usuarioService.getById(999L))
                .thenThrow(new UsuarioNotFoundException("Usuario nao encontrado"));

        mockMvc.perform(get("/api/v1/usuarios/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(usuarioService, times(1)).getById(999L);
    }

    @Test
    @DisplayName("Should create usuario successfully")
    void testCreateSuccess() throws Exception {
        doNothing().when(usuarioService).create(isA(UsuarioRequestDTO.class));

        mockMvc.perform(post("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuarioRequestDTO)))
                .andExpect(status().isCreated());

        verify(usuarioService, times(1)).create(isA(UsuarioRequestDTO.class));
    }

    @Test
    @DisplayName("Should update usuario successfully")
    void testUpdateSuccess() throws Exception {
        when(usuarioService.update(eq(1L), isA(UsuarioUpdateDTO.class)))
                .thenReturn(usuario);

        mockMvc.perform(put("/api/v1/usuarios/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuarioUpdateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("joao@example.com"));

        verify(usuarioService, times(1)).update(eq(1L), isA(UsuarioUpdateDTO.class));
    }

    @Test
    @DisplayName("Should change password successfully")
    void testTrocarSenhaSuccess() throws Exception {
        TrocarSenhaRequestDTO dto = new TrocarSenhaRequestDTO(
                "senhaAtual",
                "novaSenha123",
                "novaSenha123"
        );

        doNothing().when(usuarioService).trocarSenha(eq(1L), isA(TrocarSenhaRequestDTO.class));

        mockMvc.perform(patch("/api/v1/usuarios/1/senha")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNoContent());

        verify(usuarioService, times(1)).trocarSenha(eq(1L), isA(TrocarSenhaRequestDTO.class));
    }

    @Test
    @DisplayName("Should delete usuario successfully")
    void testDeleteSuccess() throws Exception {
        doNothing().when(usuarioService).delete(1L);

        mockMvc.perform(delete("/api/v1/usuarios/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(usuarioService, times(1)).delete(1L);
    }

    @Test
    @DisplayName("Should search usuarios by nome")
    void testSearchByNomeSuccess() throws Exception {
        UsuarioUpdateDTO resultado = new UsuarioUpdateDTO(
                "João Silva",
                "joao@example.com",
                "joao",
                TipoUsuario.CLIENTE,
                enderecoDTO
        );

        when(usuarioService.searchByNome("João"))
                .thenReturn(List.of(resultado));

        mockMvc.perform(get("/api/v1/usuarios/search/nome?nome=João")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("João Silva"));

        verify(usuarioService, times(1)).searchByNome("João");
    }
}








