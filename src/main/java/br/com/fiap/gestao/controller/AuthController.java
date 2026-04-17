package br.com.fiap.gestao.controller;


import br.com.fiap.gestao.jwt.Credentials;
import br.com.fiap.gestao.jwt.Token;
import br.com.fiap.gestao.model.Usuario;
import br.com.fiap.gestao.service.AuthService;
import br.com.fiap.gestao.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private TokenService tokenService;

    @PostMapping("/login")
    @Operation(
            summary = "Realiza login do usuario e gera token JWT",
            description = "Autentica o usuario com email e senha. Retorna um token JWT se as credenciais estiverem corretas."
    )
    public Token login(@RequestBody Credentials credentials){
        Usuario user = authService.autenticar(credentials.email(), credentials.password());
        return tokenService.createToken(user);
    }
}
