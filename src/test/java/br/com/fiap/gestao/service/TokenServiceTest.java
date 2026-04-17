package br.com.fiap.gestao.service;

import br.com.fiap.gestao.jwt.Token;
import br.com.fiap.gestao.model.Endereco;
import br.com.fiap.gestao.model.TipoUsuario;
import br.com.fiap.gestao.model.Usuario;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TokenService Tests")
class TokenServiceTest {

    private TokenService tokenService;
    private Usuario usuario;
    private String secret;

    @BeforeEach
    void setUp() {
        tokenService = new TokenService();
        secret = "secret";

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
    }

    // Tests para createToken
    @Test
    @DisplayName("Should create token successfully")
    void testCreateTokenSuccess() {
        Token token = tokenService.createToken(usuario);

        assertNotNull(token);
        assertNotNull(token.token());
        assertNotNull(token.email());
        assertEquals(usuario.getEmail(), token.email());
    }

    @Test
    @DisplayName("Should create token with correct subject (user id)")
    void testCreateTokenHasCorrectSubject() {
        Token token = tokenService.createToken(usuario);

        Algorithm algorithm = Algorithm.HMAC256(secret);
        var decoded = JWT.require(algorithm).build().verify(token.token());
        
        assertEquals(usuario.getId().toString(), decoded.getSubject());
    }

    @Test
    @DisplayName("Should create token with email claim")
    void testCreateTokenHasEmailClaim() {
        Token token = tokenService.createToken(usuario);

        Algorithm algorithm = Algorithm.HMAC256(secret);
        var decoded = JWT.require(algorithm).build().verify(token.token());
        
        assertEquals(usuario.getEmail(), decoded.getClaim("email").asString());
    }

    @Test
    @DisplayName("Should create token with role claim")
    void testCreateTokenHasRoleClaim() {
        Token token = tokenService.createToken(usuario);

        Algorithm algorithm = Algorithm.HMAC256(secret);
        var decoded = JWT.require(algorithm).build().verify(token.token());
        
        assertEquals(TipoUsuario.CLIENTE.toString(), decoded.getClaim("role").asString());
    }

    @Test
    @DisplayName("Should create token with expiration")
    void testCreateTokenHasExpiration() {
        Token token = tokenService.createToken(usuario);

        Algorithm algorithm = Algorithm.HMAC256(secret);
        var decoded = JWT.require(algorithm).build().verify(token.token());
        
        assertNotNull(decoded.getExpiresAtAsInstant());
        assertTrue(decoded.getExpiresAtAsInstant().isAfter(java.time.Instant.now()));
    }

    @Test
    @DisplayName("Should create token with DONO_RESTAURANTE role")
    void testCreateTokenWithDonoRestauranteRole() {
        usuario.setTipoUsuario(TipoUsuario.DONO_RESTAURANTE);
        
        Token token = tokenService.createToken(usuario);

        Algorithm algorithm = Algorithm.HMAC256(secret);
        var decoded = JWT.require(algorithm).build().verify(token.token());
        
        assertEquals(TipoUsuario.DONO_RESTAURANTE.toString(), decoded.getClaim("role").asString());
    }

    // Tests para getUsuarioFromToken
    @Test
    @DisplayName("Should extract usuario from token successfully")
    void testGetUsuarioFromTokenSuccess() {
        Token token = tokenService.createToken(usuario);
        
        Usuario extracted = tokenService.getUsuarioFromToken(token.token());

        assertNotNull(extracted);
        assertEquals(usuario.getId(), extracted.getId());
        assertEquals(usuario.getEmail(), extracted.getEmail());
        assertEquals(usuario.getTipoUsuario(), extracted.getTipoUsuario());
    }

    @Test
    @DisplayName("Should extract usuario with correct id from token")
    void testGetUsuarioFromTokenHasCorrectId() {
        usuario.setId(42L);
        Token token = tokenService.createToken(usuario);
        
        Usuario extracted = tokenService.getUsuarioFromToken(token.token());

        assertEquals(42L, extracted.getId());
    }

    @Test
    @DisplayName("Should extract usuario with correct email from token")
    void testGetUsuarioFromTokenHasCorrectEmail() {
        usuario.setEmail("test@email.com");
        Token token = tokenService.createToken(usuario);
        
        Usuario extracted = tokenService.getUsuarioFromToken(token.token());

        assertEquals("test@email.com", extracted.getEmail());
    }

    @Test
    @DisplayName("Should extract usuario with correct role from token")
    void testGetUsuarioFromTokenHasCorrectRole() {
        usuario.setTipoUsuario(TipoUsuario.DONO_RESTAURANTE);
        Token token = tokenService.createToken(usuario);
        
        Usuario extracted = tokenService.getUsuarioFromToken(token.token());

        assertEquals(TipoUsuario.DONO_RESTAURANTE, extracted.getTipoUsuario());
    }

    @Test
    @DisplayName("Should handle multiple token creations and extractions")
    void testMultipleTokens() {
        Usuario usuario2 = new Usuario();
        usuario2.setId(2L);
        usuario2.setEmail("outro@example.com");
        usuario2.setTipoUsuario(TipoUsuario.DONO_RESTAURANTE);

        Token token1 = tokenService.createToken(usuario);
        Token token2 = tokenService.createToken(usuario2);

        Usuario extracted1 = tokenService.getUsuarioFromToken(token1.token());
        Usuario extracted2 = tokenService.getUsuarioFromToken(token2.token());

        assertEquals(usuario.getId(), extracted1.getId());
        assertEquals(usuario2.getId(), extracted2.getId());
        assertEquals(usuario.getEmail(), extracted1.getEmail());
        assertEquals(usuario2.getEmail(), extracted2.getEmail());
    }

    @Test
    @DisplayName("Should throw exception with invalid token")
    void testGetUsuarioFromTokenWithInvalidToken() {
        String invalidToken = "invalid.token.here";

        assertThrows(Exception.class, () -> tokenService.getUsuarioFromToken(invalidToken));
    }
}

