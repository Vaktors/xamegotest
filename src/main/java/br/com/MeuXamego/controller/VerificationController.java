package br.com.MeuXamego.controller;

import br.com.MeuXamego.service.VerificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/email")
@CrossOrigin(origins = "*")
public class VerificationController {

    private final VerificationService verificationService;

    public VerificationController(VerificationService verificationService) {
        this.verificationService = verificationService;
    }

    @PostMapping("/send-code")
    public ResponseEntity<?> sendCode(@RequestBody SendCodeRequest body) {
        verificationService.generateAndSendCode(body.getUserId());
        return ResponseEntity.ok().body("Código enviado para o e-mail do usuário.");
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verify(@RequestBody VerifyCodeRequest body) {
        // agora NÃO passamos JwtUtil, porque já está dentro do service
        String jwt = verificationService.verifyCode(body.getUserId(), body.getCodigo());
        return ResponseEntity.ok().body(new VerifyResponse(jwt));
    }

    // DTOs internos
    public static class SendCodeRequest {
        private Integer userId;
        public Integer getUserId() { return userId; }
        public void setUserId(Integer userId) { this.userId = userId; }
    }

    public static class VerifyCodeRequest {
        private Integer userId;
        private String codigo;
        public Integer getUserId() { return userId; }
        public void setUserId(Integer userId) { this.userId = userId; }
        public String getCodigo() { return codigo; }
        public void setCodigo(String codigo) { this.codigo = codigo; }
    }

    public static class VerifyResponse {
        private final String token;
        public VerifyResponse(String token) { this.token = token; }
        public String getToken() { return token; }
    }
}
