package br.com.MeuXamego.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "roles")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ex.: ROLE_ADMIN, ROLE_USUARIO, ROLE_ONG, ROLE_PROTETOR
    @Column(nullable = false, unique = true)
    private String nome;
}
