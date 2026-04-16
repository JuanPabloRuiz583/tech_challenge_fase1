package br.com.fiap.Gestao.controller;


import br.com.fiap.Gestao.jwt.Credentials;
import br.com.fiap.Gestao.jwt.Token;
import br.com.fiap.Gestao.model.Usuario;
import br.com.fiap.Gestao.service.AuthService;
import br.com.fiap.Gestao.service.TokenService;
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
