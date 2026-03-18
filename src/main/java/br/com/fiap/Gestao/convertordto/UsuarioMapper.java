package br.com.fiap.Gestao.convertordto;

import br.com.fiap.Gestao.dto.UsuarioRequestDTO;
import br.com.fiap.Gestao.model.Endereco;
import br.com.fiap.Gestao.model.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    public Usuario toEntity(UsuarioRequestDTO dto) {
        Usuario usuario = new Usuario();
        applyUpdates(usuario, dto);
        return usuario;
    }

    public void applyUpdates(Usuario usuario, UsuarioRequestDTO dto) {
        usuario.setNome(dto.nome());
        usuario.setEmail(dto.email());
        usuario.setLoginUsername(dto.loginUsername());
        usuario.setSenha(dto.senha());
        usuario.setTipoUsuario(dto.tipoUsuario());

        Endereco endereco = new Endereco();
        endereco.setRua(dto.endereco().rua());
        endereco.setNumero(dto.endereco().numero());
        endereco.setCidade(dto.endereco().cidade());
        endereco.setEstado(dto.endereco().estado());
        endereco.setCep(dto.endereco().cep());
        usuario.setEndereco(endereco);
    }
}