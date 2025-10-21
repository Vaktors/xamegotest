package br.com.MeuXamego.repository;

import br.com.MeuXamego.model.PessoaFis;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PessoaFisRepository extends JpaRepository<PessoaFis, Long> {
    boolean existsByCpf(String cpf);
}
