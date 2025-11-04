package br.com.MeuXamego.repository;

import br.com.MeuXamego.model.RevokedToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RevokedTokenRepository extends JpaRepository<RevokedToken, Long> {

    // pra buscar um token específico se você quiser detalhes
    Optional<RevokedToken> findByToken(String token);

    // pra saber rápido se o token já foi revogado
    boolean existsByToken(String token);
}
