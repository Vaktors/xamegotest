package br.com.MeuXamego.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor 
@AllArgsConstructor
public class UsuarioResponseDTO {

    private Long id;
    private String nome;
    private String email;
    private String telefone;
    private String rua;
    private String numero;
    private String cidade;
    private String estado;
    private String cep;
    private String sexo;
    private String funcao;
    private boolean ativo;
    private String mensagem; // campo opcional para respostas customizadas
}
