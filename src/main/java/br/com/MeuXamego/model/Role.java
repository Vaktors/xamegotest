package br.com.MeuXamego.model; // mesmo pacote

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "roles")                 // tabela 'roles'
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // AUTO_INCREMENT
    @Column(name = "id")
    private Integer id;               // INT, bate com nossa migração [ALTERADO se antes era Long]

    @Column(name = "nome", unique = true, nullable = false)
    private String nome;              // ex.: "ROLE_ADMIN", "ROLE_USUARIO", "ROLE_ONG", "ROLE_PROTETOR"
}
