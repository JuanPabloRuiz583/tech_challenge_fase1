package br.com.fiap.Gestao.service;

import br.com.fiap.Gestao.convertordto.UsuarioMapper;
import br.com.fiap.Gestao.dto.UsuarioRequestDTO;
import br.com.fiap.Gestao.model.Usuario;
import br.com.fiap.Gestao.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UsuarioService {

    private static final Logger log = LoggerFactory.getLogger(UsuarioService.class);

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;

    public UsuarioService(UsuarioRepository usuarioRepository, UsuarioMapper usuarioMapper) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioMapper = usuarioMapper;
    }

    public List<Usuario> getAll(int page, int size) {
        log.debug("Buscando todas os usuarios no banco ");
        if (page < 1) page = 1;
        if (size < 1) size = 10;

        Pageable pageable = PageRequest.of(page - 1, size); // JPA usa pagina zero-based
        return usuarioRepository.findAll(pageable).getContent();
    }

    public Usuario getById(Long id){
        log.debug("Buscando usuario por id: {}", id);
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario não encontrada para o id " + id));

    }

    @Transactional
    public Usuario create(UsuarioRequestDTO dto){
        validarUnicidade(dto.email(), dto.loginUsername(), null);

        Usuario usuario = usuarioMapper.toEntity(dto);
        usuario.setId(null);
        usuario.setDataUltimaAlteracao(LocalDateTime.now());

        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario update(Long id, UsuarioRequestDTO dto){
        Usuario existente = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario nao encontrado para o id " + id));

        validarUnicidade(dto.email(), dto.loginUsername(), id);

        usuarioMapper.applyUpdates(existente, dto);
        existente.setDataUltimaAlteracao(LocalDateTime.now());
        return usuarioRepository.save(existente);
    }

    private void validarUnicidade(String email, String loginUsername, Long idAtual) {
        usuarioRepository.findByEmail(email)
                .filter(usuario -> idAtual == null || !usuario.getId().equals(idAtual))
                .ifPresent(usuario -> {
                    log.warn("Tentativa de salvar usuario com email ja existente: {}", email);
                    throw new RuntimeException("Email ja cadastrado");
                });

        usuarioRepository.findByLoginUsername(loginUsername)
                .filter(usuario -> idAtual == null || !usuario.getId().equals(idAtual))
                .ifPresent(usuario -> {
                    log.warn("Tentativa de salvar usuario com login ja existente: {}", loginUsername);
                    throw new RuntimeException("LoginUsername ja cadastrado");
                });
    }

    public void delete(Long id){
        if (!usuarioRepository.existsById(id)){
            log.warn("Tentativa de deletar usuario inexistente com id: {}", id);
            throw new RuntimeException("Usuario não encontrado para o id " + id);
        }
        usuarioRepository.deleteById(id);
    }
}
