package br.com.MeuXamego.DTO;

import lombok.*;

// [ADICIONADO] Lombok para gerar tudo que precisamos
@Getter @Setter                     // gera get/set
@NoArgsConstructor                  // construtor vazio
@AllArgsConstructor                 // construtor com todos os campos
@Builder                            // opcional: LoginResponse.builder()...
public class LoginResponse {

    private String token;           // [ADICIONADO] token JWT
    private Long expiresIn;         // [ADICIONADO] tempo de expiração (segundos, milis, o que vc usa)
    private String email;           // [ADICIONADO] e-mail do usuário logado
    private String nome;            // [ADICIONADO] nome do usuário (legal pra dashboard)
}
