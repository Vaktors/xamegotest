package br.com.MeuXamego.repository;

import br.com.MeuXamego.model.Empresa;
import br.com.MeuXamego.model.Usuario;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EmpresaRepository extends JpaRepository<Empresa, Long> {
    boolean existsByCnpj(String cnpj);
    Optional<Empresa> findByUsuario(Usuario usuario);
}
