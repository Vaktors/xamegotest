package br.com.MeuXamego.controller;

import br.com.MeuXamego.DTO.UpdatePerfilDTO;
import br.com.MeuXamego.DTO.UpdatePerfilResponse;
import br.com.MeuXamego.DTO.UsuarioDTO;
import br.com.MeuXamego.model.Usuario;
import br.com.MeuXamego.service.AccountDeactivationService;
import br.com.MeuXamego.service.UsuarioService;
import br.com.MeuXamego.service.VerificationService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {
    
    
    
    private final VerificationService verificationService; // <--- adiciona isso
    private final UsuarioService usuarioService;
    private final AccountDeactivationService accountDeactivationService;

   @PostMapping("/cadastrar")
public ResponseEntity<?> cadastrarUsuario(@RequestBody UsuarioDTO dto) {
    try {
        // 1. cria usuário no banco (ativo = false ainda)
        Usuario u = usuarioService.cadastrarUsuario(dto);

        verificationService.generateAndSendCode(u.getId());

      return ResponseEntity.status(201).body(
         new CadastroResponse(
        u.getId(),
        "Usuário criado. Código de verificação enviado para " + u.getEmail()
    )
);
 

    } catch (IllegalArgumentException | IllegalStateException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e) {
        return ResponseEntity.internalServerError().body("Erro ao cadastrar: " + e.getMessage());
    }
}   

 

    @PutMapping("/me")
    public ResponseEntity<?> atualizarMeuPerfil(@RequestBody UpdatePerfilDTO dto) {
        try {
            UpdatePerfilResponse resp = usuarioService.atualizarPerfilUsuarioLogado(dto);
            return ResponseEntity.ok(resp);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                Map.of("erro", e.getMessage())
            );
        }
    }

     // 1) Solicitar código de desativação
    @PostMapping("/me/desativacao/solicitar")
    public ResponseEntity<?> solicitarDesativacao() {
        try {
            accountDeactivationService.solicitarDesativacaoConta();
            return ResponseEntity.ok(
                    Map.of(
                            "mensagem", "Código de desativação enviado para o seu e-mail.",
                            "expiraEmMinutos", 15
                    )
            );
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    // 2) Confirmar desativação com código
    @PostMapping("/me/desativacao/confirmar")
    public ResponseEntity<?> confirmarDesativacao(
            @RequestBody Map<String, String> body,
            @RequestHeader(name = "Authorization", required = false) String authHeader) {

        String codigo = body.get("codigo");

        try {
            accountDeactivationService.confirmarDesativacaoConta(codigo, authHeader);
            return ResponseEntity.ok(
                    Map.of("mensagem", "Conta desativada com sucesso.")
            );
        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

// DTO simples pra resposta do cadastro
@Getter
@AllArgsConstructor
static class CadastroResponse {
    private Integer userId;
    private String message;
}

}
