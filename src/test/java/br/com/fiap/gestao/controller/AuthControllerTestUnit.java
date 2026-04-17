package br.com.fiap.gestao.controller;

import br.com.fiap.gestao.exception.CredenciaisInvalidasException;
import br.com.fiap.gestao.handler.GlobalExceptionHandler;
import br.com.fiap.gestao.jwt.Credentials;
import br.com.fiap.gestao.jwt.Token;
import br.com.fiap.gestao.model.TipoUsuario;
import br.com.fiap.gestao.model.Usuario;
import br.com.fiap.gestao.service.AuthService;
import br.com.fiap.gestao.service.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("AuthController Unit Tests")
class AuthControllerTestUnit {

    private MockMvc mockMvc;
    private AuthService authService;
    private TokenService tokenService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        authService = mock(AuthService.class);
        tokenService = mock(TokenService.class);
        objectMapper = new ObjectMapper();
        
        AuthController controller = new AuthController();
        controller.authService = authService;
        controller.tokenService = tokenService;
        
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("Should authenticate user successfully and return token")
    void testLoginSuccess() throws Exception {
        // Given
        Credentials credentials = new Credentials("user@example.com", "password123");
        Usuario user = new Usuario();
        user.setId(1L);
        user.setEmail("user@example.com");
        user.setLoginUsername("user");
        user.setTipoUsuario(TipoUsuario.CLIENTE);

        Token expectedToken = new Token("jwt-token-123", "Bearer");

        when(authService.autenticar(credentials.email(), credentials.password())).thenReturn(user);
        when(tokenService.createToken(user)).thenReturn(expectedToken);

        // When & Then
        mockMvc.perform(post("/api/v1/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(credentials)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token-123"))
                .andExpect(jsonPath("$.type").value("Bearer"));

        verify(authService, times(1)).autenticar(credentials.email(), credentials.password());
        verify(tokenService, times(1)).createToken(user);
    }

    @Test
    @DisplayName("Should return 401 when credentials are invalid")
    void testLoginWithInvalidCredentials() throws Exception {
        // Given
        Credentials credentials = new Credentials("user@example.com", "wrongpassword");
        
        when(authService.autenticar(credentials.email(), credentials.password()))
                .thenThrow(new CredenciaisInvalidasException("Credenciais inválidas"));

        // When & Then
        mockMvc.perform(post("/api/v1/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(credentials)))
                .andExpect(status().isUnauthorized());

        verify(authService, times(1)).autenticar(credentials.email(), credentials.password());
        verify(tokenService, never()).createToken(any(Usuario.class));
    }

    @Test
    @DisplayName("Should return 400 when credentials are missing email")
    void testLoginWithMissingEmail() throws Exception {
        // Given
        Credentials credentials = new Credentials("", "password123");

        // When & Then
        mockMvc.perform(post("/api/v1/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(credentials)))
                .andExpect(status().isBadRequest());

        verify(authService, never()).autenticar(anyString(), anyString());
    }

    @Test
    @DisplayName("Should return 400 when credentials are missing password")
    void testLoginWithMissingPassword() throws Exception {
        // Given
        Credentials credentials = new Credentials("user@example.com", "");

        // When & Then
        mockMvc.perform(post("/api/v1/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(credentials)))
                .andExpect(status().isBadRequest());

        verify(authService, never()).autenticar(anyString(), anyString());
    }

    @Test
    @DisplayName("Should return 400 when request body is malformed")
    void testLoginWithMalformedBody() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/v1/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{invalid-json}"))
                .andExpect(status().isBadRequest());

        verify(authService, never()).autenticar(anyString(), anyString());
    }
}


