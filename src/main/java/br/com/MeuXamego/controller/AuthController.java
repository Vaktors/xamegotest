package br.com.MeuXamego.controller;
import br.com.MeuXamego.model.RevokedToken;

import br.com.MeuXamego.model.Usuario;
import br.com.MeuXamego.repository.RevokedTokenRepository;
import br.com.MeuXamego.repository.UsuarioRepository;
import br.com.MeuXamego.Security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final RevokedTokenRepository revokedTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // 🔑 LOGIN
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String senha = request.get("senha");

        var usuarioOpt = usuarioRepository.findByEmail(email);
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("erro", "Usuário não encontrado"));
        }

        Usuario usuario = usuarioOpt.get();

        // valida senha com BCrypt
        if (!passwordEncoder.matches(senha, usuario.getSenhaHash())) {
            return ResponseEntity.status(401).body(Map.of("erro", "Senha incorreta"));
        }

        // atualiza último login
        usuario.setUltimoLogin(LocalDateTime.now());
        usuarioRepository.save(usuario);

        // gera o token
        String token = jwtUtil.generateToken(usuario.getEmail());

        return ResponseEntity.ok(Map.of(
                "token", token,
                "tipo", "Bearer",
                "usuario", usuario.getNome(),
                "email", usuario.getEmail()
        ));
    }

    // 🚪 LOGOUT (revoga token)
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body(Map.of("erro", "Token inválido"));
        }

        String token = authHeader.substring(7);
        revokedTokenRepository.save(new RevokedToken(token, LocalDateTime.now()));



        return ResponseEntity.ok(Map.of("mensagem", "Logout realizado com sucesso"));
    }
}
