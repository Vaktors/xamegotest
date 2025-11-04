package br.com.MeuXamego.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;              // continua igual
import br.com.MeuXamego.model.Bairro;                            // [ADICIONADO] enum para regiões do DF/entorno
import br.com.MeuXamego.model.Sexo;                              // [ADICIONADO] enum FEMININO/MASCULINO/OUTROS
import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioDTO {

    private String nome;             // continua
    private String email;            // continua
    private String senha;            // continua (texto cru que vamos criptografar)

    // -------- CONTATO ----------
    private String telefone;         // mantemos telefone, útil pra ONG/protetor falar com adotante

    // -------- ENDEREÇO ----------
    private Bairro bairro;           // [ADICIONADO] em vez de rua/cidade/estado/cep
    private String enderecoDetalhes; // [ADICIONADO] ex: "QS 12 Conjunto B Casa 3"

    // -------- PERFIL PESSOAL ----------
    private Sexo sexo;               // [ALTERADO] era String "M/F", agora enum Sexo
                                     // valores permitidos: FEMININO, MASCULINO, OUTROS

    // -------- PAPEL DO USUÁRIO ----------
    private String roleNome;         // [ADICIONADO]
                                     // Exatamente um de:
                                     // "ROLE_USUARIO", "ROLE_ONG", "ROLE_PROTETOR", "ROLE_ADMIN"
                                     // isso substitui "funcao"

    // -------- CAMPOS EXTRAS SE FOR ONG ----------
    private String nomeOng;          // ainda precisamos pra cadastrar ONG
    private String cnpj;             // ainda precisamos pra cadastrar ONG

    // -------- CAMPOS EXTRAS SE FOR PROTETOR ----------
    private String cpf;              // CPF do protetor

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dataNascimento; // data de nascimento do protetor
}
