package br.com.MeuXamego.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioDTO {
    private String nome;
    private String email;
    private String senha;
    private String telefone;
    private String rua;
    private String numero;
    private String cidade;
    private String estado;
    private String cep;
    private String sexo; // M, F, etc
    private String funcao; // ADMIN, USUARIO, ONG, PROTETOR

    // ONG
    private String nomeOng;
    private String cnpj;

    // PROTETOR
    private String cpf;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dataNascimento;
}
