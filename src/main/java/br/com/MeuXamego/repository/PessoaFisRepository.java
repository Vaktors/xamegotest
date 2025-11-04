package br.com.MeuXamego.repository;

import br.com.MeuXamego.model.PessoaFis;
import br.com.MeuXamego.model.Usuario;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PessoaFisRepository extends JpaRepository<PessoaFis, Long> {
    boolean existsByCpf(String cpf);
      Optional<PessoaFis> findByUsuario(Usuario usuario);
}
