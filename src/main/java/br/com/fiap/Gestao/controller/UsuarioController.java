package br.com.fiap.Gestao.controller;

import br.com.fiap.Gestao.dto.TrocarSenhaRequestDTO;
import br.com.fiap.Gestao.dto.UsuarioRequestDTO;
import br.com.fiap.Gestao.dto.UsuarioUpdateDTO;
import br.com.fiap.Gestao.model.Usuario;
import br.com.fiap.Gestao.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Operation(summary = "Listar todos os usuarios", description = "Retorna uma lista paginada de usuarios cadastrados no sistema")
    @GetMapping
    public ResponseEntity<List<Usuario>> getAllUsuarios(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        List<Usuario> usuarios = usuarioService.getAll(page, size);
        return ResponseEntity.ok(usuarios);
    }

    @Operation(summary = "Buscar usuário por ID", description = "Retorna um usuário específico pelo ID informado")
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> getById(@PathVariable("id") Long id) {
        Usuario usuario = usuarioService.getById(id);
        return ResponseEntity.ok(usuario);
    }

    @Operation(summary = "Buscar usuários por nome", description = "Retorna uma lista de usuários que correspondem ao nome informado (sem expor senha)")
    @GetMapping("/search/nome")
    public ResponseEntity<List<UsuarioUpdateDTO>> searchByNome(@RequestParam String nome) {
        return ResponseEntity.ok(usuarioService.searchByNome(nome));
    }

    @Operation(summary = "Criar Usuarios", description = "Cria um novo usuário com as informações fornecidas no corpo da requisição. Os tipos de usuarios permitidos são: DONO_RESTAURANTE, CLIENTE. O email e loginUsername devem ser únicos no sistema. Se um usuário com o mesmo email ou loginUsername já existir, retorna um erro 409.")
    @PostMapping
    public ResponseEntity<Void> create(@Valid @RequestBody UsuarioRequestDTO usuarioRequestDTO) {
        this.usuarioService.create(usuarioRequestDTO);
        return ResponseEntity.status(201).build();
    }

    @Operation(summary = "Atualizar dados do usuario", description = "Atualiza apenas as informações cadastrais do usuário, sem alterar a senha.")
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> update(@PathVariable("id") Long id, @Valid @RequestBody UsuarioUpdateDTO usuarioUpdateDTO) {
        return ResponseEntity.ok(usuarioService.update(id, usuarioUpdateDTO));
    }

    @Operation(summary = "Trocar senha do usuario", description = "Atualiza apenas a senha do usuário.")
    @PatchMapping("/{id}/senha")
    public ResponseEntity<Void> trocarSenha(
            @PathVariable("id") Long id,
            @Valid @RequestBody TrocarSenhaRequestDTO trocarSenhaRequestDTO
    ) {
        usuarioService.trocarSenha(id, trocarSenhaRequestDTO);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Deleta usuário por ID", description = "Deleta um usuário específico pelo ID informado. Se o usuário não existir, retorna um erro 404.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        this.usuarioService.delete(id);
        return ResponseEntity.noContent().build();
    }


}
