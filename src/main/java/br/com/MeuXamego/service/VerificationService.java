package br.com.MeuXamego.service;

import br.com.MeuXamego.Security.JwtUtil;
import br.com.MeuXamego.model.EmailVerification;
import br.com.MeuXamego.model.Usuario;
import br.com.MeuXamego.repository.EmailVerificationRepository;
import br.com.MeuXamego.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class VerificationService {

    private final UsuarioRepository usuarioRepository;
    private final EmailVerificationRepository emailVerificationRepository;
    private final EmailService emailService;
    private final JwtUtil jwtUtil; // <--- injeta aqui

    /**
     * RF01.2
     * Gera um novo código, salva em email_verifications
     * e envia o e-mail para o usuário.
     */
    public String generateAndSendCode(Integer userId) {
        // 1. busca usuário
        Usuario user = usuarioRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        // 2. gerar código aleatório de 5 dígitos
        String codigo = gerarCodigo5Digitos();

        // 3. salvar registro de verificação
        EmailVerification ev = new EmailVerification();
        ev.setUsuario(user);
        ev.setCodigo(codigo);
        ev.setCriadoEm(LocalDateTime.now());
        ev.setExpiraEm(LocalDateTime.now().plusMinutes(15));
        ev.setTentativasInvalidas(0);
        ev.setReenviados(0);
        ev.setUsado(false);

        emailVerificationRepository.save(ev);

        // 4. mandar e-mail real
        emailService.enviarCodigoVerificacao(
            user.getEmail(),
            codigo
        );

        return codigo;
    }

    /**
     * RF01.2 (confirmação)
     * Valida o código digitado e marca o usuário como ativo se estiver OK.
     * Retorna um token JWT pra já logar.
     */
    public String verifyCode(Integer userId, String codigoDigitado) {
        // pega usuário
        Usuario user = usuarioRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

        // pega último código gerado pra esse user que ainda não foi usado
        EmailVerification ev = emailVerificationRepository
            .findTopByUsuarioIdAndUsadoOrderByCriadoEmDesc(userId, false)
            .orElseThrow(() ->
                new IllegalArgumentException("Nenhum código ativo encontrado.")
            );

        // expirado?
        if (ev.getExpiraEm().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Código expirado.");
        }

        // bateu?
        if (!ev.getCodigo().equals(codigoDigitado)) {
            ev.setTentativasInvalidas(ev.getTentativasInvalidas() + 1);
            emailVerificationRepository.save(ev);
            throw new IllegalArgumentException("Código inválido.");
        }

        // marca como usado
        ev.setUsado(true);
        emailVerificationRepository.save(ev);

        // ativa o usuário
        user.setAtivo(true);
        usuarioRepository.save(user);

        // gera e devolve JWT
        return jwtUtil.generateToken(user.getEmail());
    }

    // helper pra gerar "00000".."99999"
    private String gerarCodigo5Digitos() {
        Random r = new Random();
        int n = r.nextInt(100000);
        return String.format("%05d", n);
    }
}
