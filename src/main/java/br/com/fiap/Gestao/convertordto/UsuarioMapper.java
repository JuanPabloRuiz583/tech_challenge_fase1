package br.com.fiap.Gestao.convertordto;

import br.com.fiap.Gestao.dto.UsuarioRequestDTO;
import br.com.fiap.Gestao.dto.UsuarioUpdateDTO;
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
        usuario.setEndereco(mapEndereco(dto.endereco()));
    }

    public void applyUpdates(Usuario usuario, UsuarioUpdateDTO dto) {
        usuario.setNome(dto.nome());
        usuario.setEmail(dto.email());
        usuario.setLoginUsername(dto.loginUsername());
        usuario.setTipoUsuario(dto.tipoUsuario());
        usuario.setEndereco(mapEndereco(dto.endereco()));
    }

    public UsuarioUpdateDTO toUpdateDTO(Usuario usuario) {
        return new UsuarioUpdateDTO(
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getLoginUsername(),
                usuario.getTipoUsuario(),
                mapEnderecoToDTO(usuario.getEndereco())
        );
    }

    private UsuarioRequestDTO.EnderecoDTO mapEnderecoToDTO(Endereco endereco) {
        return new UsuarioRequestDTO.EnderecoDTO(
                endereco.getRua(),
                endereco.getNumero(),
                endereco.getCidade(),
                endereco.getEstado(),
                endereco.getCep()
        );
    }

    private Endereco mapEndereco(UsuarioRequestDTO.EnderecoDTO enderecoDTO) {
        Endereco endereco = new Endereco();
        endereco.setRua(enderecoDTO.rua());
        endereco.setNumero(enderecoDTO.numero());
        endereco.setCidade(enderecoDTO.cidade());
        endereco.setEstado(enderecoDTO.estado());
        endereco.setCep(enderecoDTO.cep());
        return endereco;
    }
}