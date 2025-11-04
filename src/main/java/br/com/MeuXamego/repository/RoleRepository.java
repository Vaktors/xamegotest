package br.com.MeuXamego.repository;

import br.com.MeuXamego.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByNome(String nome);
}
