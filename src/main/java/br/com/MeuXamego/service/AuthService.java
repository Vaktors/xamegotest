package br.com.MeuXamego.service;

import br.com.MeuXamego.DTO.LoginRequest;
import br.com.MeuXamego.DTO.LoginResponse;
import br.com.MeuXamego.DTO.PasswordChangeRequest;
import br.com.MeuXamego.model.RevokedToken;
import br.com.MeuXamego.model.Usuario;
import br.com.MeuXamego.repository.RevokedTokenRepository;
import br.com.MeuXamego.repository.UsuarioRepository;
import br.com.MeuXamego.Security.JwtUtil;           // <<< ajuste o pacote conforme o seu arquivo JwtUtil.java
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final RevokedTokenRepository revokedTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;                  // <<< injeta o seu util (SEM generics/cast)

    private static final int MAX_TENTATIVAS = 5;
    private static final int BLOQUEIO_MINUTOS = 15;

    @Transactional
    public LoginResponse login(LoginRequest req) {
        // Validações básicas
        if (req.getEmail() == null || req.getEmail().isBlank()) {
            throw new IllegalArgumentException("E-mail é obrigatório.");
        }
        if (req.getSenha() == null || req.getSenha().isBlank()) {
            throw new IllegalArgumentException("Senha é obrigatória.");
        }

        Usuario u = usuarioRepository.findByEmail(req.getEmail())
            .orElseThrow(() -> new IllegalArgumentException("E-mail não encontrado. Verifique e tente novamente."));

        // Bloqueio por tentativas
        if (u.getBloqueadoAte() != null && u.getBloqueadoAte().isAfter(LocalDateTime.now())) {
            throw new IllegalStateException("Número máximo de tentativas atingido. Tente novamente em 15 minutos.");
        }

        // Senha incorreta
        if (!passwordEncoder.matches(req.getSenha(), u.getSenhaHash())) {
            int t = u.getTentativasFalhas() + 1;
            u.setTentativasFalhas(t);
            if (t >= MAX_TENTATIVAS) {
                u.setBloqueadoAte(LocalDateTime.now().plusMinutes(BLOQUEIO_MINUTOS));
            }
            usuarioRepository.save(u);
            throw new IllegalArgumentException("Senha incorreta. Tente novamente.");
        }

        // Correta: zera tentativas
        u.setTentativasFalhas(0);
        u.setBloqueadoAte(null);

        // Conta ativa?
        if (!u.isAtivo()) {
            usuarioRepository.save(u);
            throw new IllegalStateException("Conta desativada. Entre em contato com o administrador.");
        }

        // Atualiza último login
        u.setUltimoLogin(LocalDateTime.now());
        usuarioRepository.save(u);

        // Gera token
        String token = jwtUtil.generateToken(u.getEmail());
        return new LoginResponse(token, jwtUtil.getExpirationSeconds(), "Autenticado com sucesso.");
    }

    @Transactional
public void logout(String token) {
    // Persiste blacklist até expirar
    var exp = LocalDateTime.now().plusSeconds(jwtUtil.getExpirationSeconds());

    if (!revokedTokenRepository.existsByToken(token)) {
        revokedTokenRepository.save(
            RevokedToken.builder()
                .token(token)
                .revokedAt(exp)
                .build()
        );
    }
}


    @Transactional
    public void changePassword(PasswordChangeRequest req) {
        if (req.getEmail() == null || req.getEmail().isBlank()
                || req.getSenhaAtual() == null || req.getSenhaAtual().isBlank()
                || req.getNovaSenha() == null || req.getNovaSenha().isBlank()) {
            throw new IllegalArgumentException("E-mail, senha atual e nova senha são obrigatórios.");
        }

        Usuario u = usuarioRepository.findByEmail(req.getEmail())
            .orElseThrow(() -> new IllegalArgumentException("E-mail não encontrado."));

        if (!passwordEncoder.matches(req.getSenhaAtual(), u.getSenhaHash())) {
            throw new IllegalArgumentException("Senha atual incorreta.");
        }

        u.setSenhaHash(passwordEncoder.encode(req.getNovaSenha()));
        usuarioRepository.save(u);
    }
}
