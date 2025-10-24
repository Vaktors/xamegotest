package br.com.MeuXamego.controller;

import br.com.MeuXamego.DTO.UsuarioDTO;
import br.com.MeuXamego.model.Usuario;
import br.com.MeuXamego.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping("/cadastrar")
    public ResponseEntity<?> cadastrarUsuario(@RequestBody UsuarioDTO dto) {
        try {
            Usuario u = usuarioService.cadastrarUsuario(dto);
            return ResponseEntity.ok(u); // em prod você pode retornar um DTO sem senhaHash
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erro ao cadastrar: " + e.getMessage());
        }
    }
}
