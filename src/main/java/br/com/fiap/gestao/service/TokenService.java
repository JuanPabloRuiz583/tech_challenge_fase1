package br.com.fiap.gestao.service;

import java.time.Instant;

import br.com.fiap.gestao.jwt.Token;
import br.com.fiap.gestao.model.TipoUsuario;
import br.com.fiap.gestao.model.Usuario;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.stereotype.Service;

@Service
public class TokenService {
    private static final long EXPIRATION_MINUTES = 120;
    private final Algorithm algorithm = Algorithm.HMAC256("secret");

    public Token createToken(Usuario user) {
        Instant expiresAt = Instant.now().plusSeconds(EXPIRATION_MINUTES * 60);

        var jwt = JWT.create()
                .withSubject(user.getId().toString())
                .withClaim("email", user.getEmail())
                .withClaim("role", user.getTipoUsuario().toString())
                .withExpiresAt(expiresAt)
                .sign(algorithm);

        return new Token(jwt, user.getEmail());
    }

    public Usuario getUsuarioFromToken(String jwt) {
        var jwtVerified = JWT.require(algorithm).build().verify(jwt);

        Usuario usuario = new Usuario();
        usuario.setId(Long.valueOf(jwtVerified.getSubject()));
        usuario.setEmail(jwtVerified.getClaim("email").asString());
        usuario.setTipoUsuario(TipoUsuario.valueOf(jwtVerified.getClaim("role").asString()));

        return usuario;
    }
}
