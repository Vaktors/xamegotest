package br.com.MeuXamego.repository;

import br.com.MeuXamego.model.EmailVerification;
import br.com.MeuXamego.model.Usuario;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Integer> {

    /**
     * Busca o registro de verificação de e-mail MAIS RECENTE (mais novo primeiro),
     * de um usuário específico, que ainda NÃO foi usado.
     *
     * - usuarioId: id do usuário dono do código
     * - usado: normalmente false (queremos códigos ainda ativos)
     *
     * Exemplo de uso:
     *   findTopByUsuarioIdAndUsadoOrderByCriadoEmDesc(userId, false)
     */
    Optional<EmailVerification> findTopByUsuarioIdAndUsadoOrderByCriadoEmDesc(
        Integer usuarioId,
        boolean usado
    );

    Optional<Usuario> findTopByUsuarioIdOrderByCriadoEmDesc(Integer userId);

}
