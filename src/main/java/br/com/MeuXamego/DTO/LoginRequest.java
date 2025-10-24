package br.com.MeuXamego.DTO;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO responsável por receber as credenciais de login do usuário.
 * Usado pelo AuthController e AuthService para autenticação.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    private String email;
    private String senha;
}
