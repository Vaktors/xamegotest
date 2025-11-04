package br.com.MeuXamego.service;

import br.com.MeuXamego.DTO.LoginRequest;
import br.com.MeuXamego.DTO.LoginResponse;
import br.com.MeuXamego.DTO.PasswordChangeRequest;
import br.com.MeuXamego.model.Usuario;
import br.com.MeuXamego.model.RevokedToken;
import br.com.MeuXamego.repository.UsuarioRepository;
import br.com.MeuXamego.repository.RevokedTokenRepository;

import org.eclipse.angus.mail.imap.protocol.BODY;
import org.springframework.http.StreamingHttpOutputMessage.Body;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final RevokedTokenRepository revokedTokenRepository;
    // + provavel PasswordEncoder, JwtUtil, etc. (não vi seu código, então vou deixar nomes genéricos)

    public AuthService(
        UsuarioRepository usuarioRepository,
        RevokedTokenRepository revokedTokenRepository
    ) {
        this.usuarioRepository = usuarioRepository;
        this.revokedTokenRepository = revokedTokenRepository;
    }

    @Transactional
    public LoginResponse login(LoginRequest req) {
        // [ALTERADO] precisamos garantir que LoginRequest tenha getEmail() e getSenha()
        Usuario u = usuarioRepository.findByEmailIgnoreCase(req.getEmail())
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // 1. usuário está bloqueado?
        if (u.getBloqueadoAte() != null && u.getBloqueadoAte().isAfter(LocalDateTime.now())) { // [ALTERADO]
            throw new RuntimeException("Conta temporariamente bloqueada");
        }

        // 2. senha bate?
        // aqui você deve comparar o hash, ex: passwordEncoder.matches(req.getSenha(), u.getSenhaHash())
        boolean senhaOk = req.getSenha().equals("validarHASH_AQUI"); // [ALTERADO] stub
        if (!senhaOk) {
            // incrementa tentativas
            int falhas = u.getTentativasFalhas() + 1;             // [ADICIONADO]
            u.setTentativasFalhas(falhas);                        // [ADICIONADO]

            if (falhas >= 5) {                                   // regra de bloqueio que você quiser
                u.setBloqueadoAte(LocalDateTime.now().plusMinutes(15)); // [ADICIONADO]
            }

            usuarioRepository.save(u);
            throw new RuntimeException("Senha inválida");
        }

        // 3. reset tentativas/ bloqueio
        u.setTentativasFalhas(0);                                // [ADICIONADO]
        u.setBloqueadoAte(null);                                 // [ADICIONADO]

        // 4. conta ativa?
        if (!u.isAtivo()) {                                     // [ALTERADO] usa isAtivo() gerado pelo Lombok
            throw new RuntimeException("Conta não verificada");
        }

        // 5. atualiza último login
        u.setUltimoLogin(LocalDateTime.now());                  // [ADICIONADO]
        usuarioRepository.save(u);

        // 6. gerar token JWT e expiry
        String token = "TOKEN_JWT_AQUI";                        // [ALTERADO] você gera com seu JwtUtil
        long expiresIn = 7200L;                                 // [ALTERADO] segundos ou o que vc usa

        // 7. montar a resposta
        LoginResponse resp = new LoginResponse();               // [ALTERADO] construtor vazio + setters
        resp.setToken(token);                                   // [ADICIONADO]
        resp.setExpiresIn(expiresIn);                           // [ADICIONADO]
        resp.setEmail(u.getEmail());                            // [ADICIONADO]
        resp.setNome(u.getNome());                              // [ADICIONADO] se quiser exibir o nome
        return resp;
    }

    @Transactional
    public void logout(String token, LocalDateTime expiresAt) {
        RevokedToken revoked = RevokedToken.builder()           // [ADICIONADO] precisa @Builder no RevokedToken
            .token(token)
            .revokedAt(LocalDateTime.now())
            .expiresAt(expiresAt)
            .build();

        revokedTokenRepository.save(revoked);
    }

    @Transactional
    public void changePassword(PasswordChangeRequest req) {
        // [ALTERADO] precisamos garantir getters em PasswordChangeRequest:
        // getEmail(), getSenhaAtual(), getNovaSenha()

        Usuario u = usuarioRepository.findByEmailIgnoreCase(req.getEmail())
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // valida senha atual
        boolean senhaAtualOk = req.getSenhaAtual().equals("validarHASH_AQUI"); // [ALTERADO] stub
        if (!senhaAtualOk) {
            throw new RuntimeException("Senha atual incorreta");
        }

        // atualiza hash da nova senha
        u.setSenhaHash(req.getNovaSenha()); // [ALTERADO] aplicar hash aqui antes de setar
        usuarioRepository.save(u);
    }

    public Usuario getUsuarioLogado() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getUsuarioLogado'");
    }

    
}
