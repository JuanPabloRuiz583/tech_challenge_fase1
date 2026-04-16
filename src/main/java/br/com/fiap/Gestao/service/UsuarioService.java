package br.com.fiap.Gestao.service;

import br.com.fiap.Gestao.convertordto.UsuarioMapper;
import br.com.fiap.Gestao.dto.TrocarSenhaRequestDTO;
import br.com.fiap.Gestao.dto.UsuarioRequestDTO;
import br.com.fiap.Gestao.dto.UsuarioUpdateDTO;
import br.com.fiap.Gestao.exception.SenhaInvalidaException;
import br.com.fiap.Gestao.exception.UsuarioDuplicadoException;
import br.com.fiap.Gestao.exception.UsuarioNotFoundException;
import br.com.fiap.Gestao.model.Usuario;
import br.com.fiap.Gestao.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    private static final Logger log = LoggerFactory.getLogger(UsuarioService.class);

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, UsuarioMapper usuarioMapper, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioMapper = usuarioMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Usuario> getAll(int page, int size) {
        log.debug("Buscando todas os usuarios no banco ");
        if (page < 1) page = 1;
        if (size < 1) size = 10;

        Pageable pageable = PageRequest.of(page - 1, size);
        return usuarioRepository.findAll(pageable).getContent();
    }

    public Usuario getById(Long id) {
        log.debug("Buscando usuario por id: {}", id);
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNotFoundException("Usuario nao encontrado para o id " + id));
    }

    @Transactional
    public void create(UsuarioRequestDTO dto) {
        validarUnicidade(dto.email(), dto.loginUsername(), null);

        Usuario usuario = usuarioMapper.toEntity(dto);
        usuario.setId(null);
        usuario.setDataUltimaAlteracao(LocalDateTime.now());

        usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario update(Long id, UsuarioUpdateDTO dto) {
        Usuario existente = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNotFoundException("Usuario nao encontrado para o id " + id));

        validarUnicidade(dto.email(), dto.loginUsername(), id);

        usuarioMapper.applyUpdates(existente, dto);
        existente.setDataUltimaAlteracao(LocalDateTime.now());
        return usuarioRepository.save(existente);
    }

    @Transactional
    public void trocarSenha(Long id, TrocarSenhaRequestDTO dto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNotFoundException("Usuario nao encontrado para o id " + id));

        if (!passwordEncoder.matches(dto.senhaAtual(), usuario.getSenha())) {
            throw new SenhaInvalidaException("Senha atual invalida");
        }

        if (!dto.novaSenha().equals(dto.confirmacaoNovaSenha())) {
            throw new SenhaInvalidaException("Nova senha e confirmacao estao diferentes");
        }

        usuario.setSenha(passwordEncoder.encode(dto.novaSenha()));
        usuario.setDataUltimaAlteracao(LocalDateTime.now());
        usuarioRepository.save(usuario);
    }

    private void validarUnicidade(String email, String loginUsername, Long idAtual) {
        usuarioRepository.findByEmail(email)
                .filter(usuario -> !Objects.equals(usuario.getId(), idAtual))
                .ifPresent(usuario -> {
                    log.warn("Tentativa de salvar usuario com email ja existente: {}", email);
                    throw new UsuarioDuplicadoException("Email ja cadastrado");
                });

        usuarioRepository.findByLoginUsername(loginUsername)
                .filter(usuario -> !Objects.equals(usuario.getId(), idAtual))
                .ifPresent(usuario -> {
                    log.warn("Tentativa de salvar usuario com login ja existente: {}", loginUsername);
                    throw new UsuarioDuplicadoException("LoginUsername ja cadastrado");
                });
    }

    public void delete(Long id) {
        if (!usuarioRepository.existsById(id)) {
            log.warn("Tentativa de deletar usuario inexistente com id: {}", id);
            throw new UsuarioNotFoundException("Usuario nao encontrado para o id " + id);
        }
        usuarioRepository.deleteById(id);
    }

    public List<UsuarioUpdateDTO> searchByNome(String nome) {
        String nomeNormalizado = nome == null ? "" : nome.trim();
        log.debug("Buscando usuarios por nome: {}", nomeNormalizado);

        if (nomeNormalizado.isEmpty()) {
            return List.of();
        }

        return usuarioRepository.findByNomeContainingIgnoreCase(nomeNormalizado)
                .stream()
                .map(usuarioMapper::toUpdateDTO)
                .collect(Collectors.toList());
    }
}
