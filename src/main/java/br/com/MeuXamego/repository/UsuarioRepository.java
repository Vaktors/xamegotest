package br.com.MeuXamego.repository;

import br.com.MeuXamego.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> { // [ALTERADO] Integer, não Long
    Optional<Usuario> findByEmail(String email);            // busca um usuário pelo e-mail exato
    boolean existsByEmail(String email);                    // pra validar se já existe cadastrado
    Optional<Usuario> findByEmailIgnoreCase(String email);  // [ADICIONADO] ignora maiúsc./minúsc.
    Optional<Usuario> findById(Long userId);
}
