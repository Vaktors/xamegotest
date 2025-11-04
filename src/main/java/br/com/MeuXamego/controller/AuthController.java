package br.com.MeuXamego.controller; // [ATENÇÃO] reescreve essa linha manualmente pra limpar caracteres invisíveis

import br.com.MeuXamego.Security.JwtUtil;
import br.com.MeuXamego.model.RevokedToken;
import br.com.MeuXamego.model.Usuario;
import br.com.MeuXamego.repository.RevokedTokenRepository;
import br.com.MeuXamego.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController                                             // indica que essa classe expõe endpoints REST
@RequestMapping("/api/auth")                                // prefixo comum /api/auth/...
@RequiredArgsConstructor                                   // Lombok: cria construtor com os "final"
@CrossOrigin(origins = "*")                                // libera CORS p/ testes front local
public class AuthController {

    private final UsuarioRepository usuarioRepository;          // injeção do repo de usuários
    private final RevokedTokenRepository revokedTokenRepository; // salva tokens de logout
    private final PasswordEncoder passwordEncoder;               // BCrypt pra validar senha
    private final JwtUtil jwtUtil;                               // gera/valida JWT

    // ADICIONE ESTA LINHA:
    private final br.com.MeuXamego.service.PasswordResetService passwordResetService;

    // ============================
    // 🔑 LOGIN
    // ============================
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        // request esperado:
        // { "email": "fulano@teste.com", "senha": "123456" }

        String email = request.get("email");                   // pega e-mail do body
        String senha = request.get("senha");                   // pega senha do body

        // [ALTERADO] usamos findByEmailIgnoreCase pra não dar erro se o usuário digitar maiúscula
        var usuarioOpt = usuarioRepository.findByEmailIgnoreCase(email);
        if (usuarioOpt.isEmpty()) {
            // 401 = Unauthorized
            return ResponseEntity.status(401).body(
                Map.of("erro", "Usuário não encontrado")
            );
        }

        Usuario usuario = usuarioOpt.get();                   // agora temos o usuário do banco

        
        //conta precisa estar ativa (e-mail verificado / não desativada)
        if (!usuario.isAtivo()) {
            return ResponseEntity.status(403).body(
                Map.of("erro",
                   "Conta inativa. Verifique seu e-mail para ativar ou reative pelo suporte.")
        );
        }

         
        // valida senha com BCrypt
        // passwordEncoder.matches(senhaDigitada, hashArmazenado)
        if (!passwordEncoder.matches(senha, usuario.getSenhaHash())) {
            return ResponseEntity.status(401).body(
                Map.of("erro", "Senha incorreta")
            );
        }

        // atualiza último login (auditoria / informação gerencial)
        usuario.setUltimoLogin(LocalDateTime.now());
        usuarioRepository.save(usuario);

        // gera o token JWT com base no e-mail
        String token = jwtUtil.generateToken(usuario.getEmail());


        String foto = usuario.getFotoPerfilUrl();
if (foto == null || foto.isBlank()) {
    foto = "/Home/img/default-avatar.png"; // garante que o front sempre tenha algo
}


        // devolve token + dados básicos do usuário
        // isso é ótimo pro front, pq ele já sabe quem logou e qual nome mostrar
        return ResponseEntity.ok(
            Map.of(
                "token", token,                // JWT
                "tipo",  "Bearer",             // prefixo a ser usado no header Authorization
                "usuario", usuario.getNome(),  // nome do usuário logado
                "email", usuario.getEmail(),    // e-mail normalizado
                "fotoPerfilUrl", foto
            )
        );
    }

    // ============================
    // 🚪 LOGOUT
    // ============================
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {

        // esperamos receber: Authorization: Bearer <jwt>
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body(
                Map.of("erro", "Token inválido")
            );
        }

        // corta o "Bearer " (7 caracteres)
        String token = authHeader.substring(7); // ex.: "eyJhbGciOi..."

        // === blacklist do token ===
        // ANTES (quebrando):
        // revokedTokenRepository.save(new RevokedToken(token, LocalDateTime.now()));

        // AGORA (compatível com QUALQUER RevokedToken que tenha setters):
        RevokedToken revoked = new RevokedToken();           // [ADICIONADO] cria objeto vazio
        revoked.setToken(token);                             // [ADICIONADO] salva o JWT que queremos bloquear
        revoked.setRevokedAt(LocalDateTime.now());           // [ADICIONADO] quando fez logout

        // Se sua classe RevokedToken tiver um campo "expiresAt", a gente também seta.
        // Você já tinha esse campo lá, então vamos preencher pra evitar valor null no banco.
        revoked.setExpiresAt(LocalDateTime.now().plusHours(2)); // [ADICIONADO] validade estimada desse token

        // salva no banco
        revokedTokenRepository.save(revoked);                // [ALTERADO] sem construtor custom

        // responde sucesso
        return ResponseEntity.ok(
            Map.of("mensagem", "Logout realizado com sucesso")
        );
    }

  

    @PostMapping("/password/esqueci")
    public ResponseEntity<?> esqueciSenha(@RequestBody Map<String, String> request) {

        String email = request.get("email");

        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest().body(
                    Map.of("erro", "Informe o e-mail.")
            );
        }

        try {
            passwordResetService.solicitarCodigo(email);
            return ResponseEntity.ok(
                    Map.of("mensagem", "Se este e-mail estiver cadastrado, você receberá um código.")
            );
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(
                    Map.of("erro", e.getMessage())
            );
        }
    }

    @PostMapping("/password/redefinir")
    public ResponseEntity<?> redefinirSenha(@RequestBody Map<String, String> request) {

        String email          = request.get("email");
        String codigo         = request.get("codigo");
        String novaSenha      = request.get("novaSenha");
        String confirmarSenha = request.get("confirmarSenha");

        try {
            passwordResetService.redefinirSenha(email, codigo, novaSenha, confirmarSenha);
            return ResponseEntity.ok(
                    Map.of("mensagem", "Senha redefinida com sucesso.")
            );
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(
                    Map.of("erro", e.getMessage())
            );
        }
    }
}


    

