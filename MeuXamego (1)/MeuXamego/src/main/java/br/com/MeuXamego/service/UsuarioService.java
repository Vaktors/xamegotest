package br.com.MeuXamego.service;

import br.com.MeuXamego.DTO.UsuarioDTO;
import br.com.MeuXamego.model.Empresa;
import br.com.MeuXamego.model.PessoaFis;
import br.com.MeuXamego.model.Role;
import br.com.MeuXamego.model.Usuario;
import br.com.MeuXamego.repository.EmpresaRepository;
import br.com.MeuXamego.repository.PessoaFisRepository;
import br.com.MeuXamego.repository.RoleRepository;
import br.com.MeuXamego.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RoleRepository roleRepository;
    private final PessoaFisRepository pessoaFisRepository;
    private final EmpresaRepository empresaRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Usuario cadastrarUsuario(UsuarioDTO dto) {

        if (dto.getFuncao() == null || dto.getFuncao().isBlank()) {
            throw new IllegalArgumentException("Função é obrigatória (ADMIN, USUARIO, ONG, PROTETOR).");
        }
        final String funcao = dto.getFuncao().trim().toUpperCase();

        // e-mail único
        if (usuarioRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalStateException("E-mail já cadastrado.");
        }

        // regras por função
        if ("ONG".equals(funcao)) {
            if (isBlank(dto.getNomeOng())) throw new IllegalArgumentException("Para ONG, 'nomeOng' é obrigatório.");
            if (isBlank(dto.getCnpj()))   throw new IllegalArgumentException("Para ONG, 'cnpj' é obrigatório.");
            if (empresaRepository.existsByCnpj(dto.getCnpj())) throw new IllegalStateException("CNPJ já cadastrado.");
        } else if ("PROTETOR".equals(funcao)) {
            if (isBlank(dto.getCpf())) throw new IllegalArgumentException("Para PROTETOR, 'cpf' é obrigatório.");
            if (dto.getDataNascimento() == null) throw new IllegalArgumentException("Para PROTETOR, 'dataNascimento' é obrigatório (YYYY-MM-DD).");
            if (pessoaFisRepository.existsByCpf(dto.getCpf())) throw new IllegalStateException("CPF já cadastrado.");
        }

        // monta usuário
        Usuario usuario = new Usuario();
        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        usuario.setSenhaHash(passwordEncoder.encode(dto.getSenha()));
        usuario.setTelefone(dto.getTelefone());
        usuario.setRua(dto.getRua());
        usuario.setNumero(dto.getNumero());
        usuario.setCidade(dto.getCidade());
        usuario.setEstado(dto.getEstado());
        usuario.setCep(dto.getCep());
        usuario.setSexo(normalizaSexo(dto.getSexo()));
        usuario.setFuncao(funcao);

        // role
        String roleName = "ROLE_" + funcao;
        Role role = roleRepository.findByNome(roleName).orElseGet(() -> {
            Role r = new Role();
            r.setNome(roleName);
            return roleRepository.save(r);
        });
        usuario.getRoles().add(role);

        // aprovado conforme regra
        boolean aprovado = "ADMIN".equals(funcao) || "USUARIO".equals(funcao);
        usuario.setAprovado(aprovado);

        // salva usuário
        Usuario salvo = usuarioRepository.save(usuario);

        // relacionais
        if ("ONG".equals(funcao)) {
            Empresa empresa = new Empresa();
            empresa.setCnpj(dto.getCnpj());
            empresa.setNomeOng(dto.getNomeOng());
            empresa.setUsuario(salvo);
            empresaRepository.save(empresa);
        } else if ("PROTETOR".equals(funcao)) {
            PessoaFis pf = new PessoaFis();
            pf.setCpf(dto.getCpf());
            pf.setSexo(normalizaSexo(dto.getSexo())); // 1 char (M/F)
            pf.setDataNascimento(dto.getDataNascimento());
            pf.setUsuario(salvo);
            pessoaFisRepository.save(pf);
        }

        return salvo;
    }

    // helpers
    private boolean isBlank(String s) { return s == null || s.isBlank(); }

    private String normalizaSexo(String sexo) {
        if (sexo == null) return null;
        String v = sexo.trim().toUpperCase();
        if (v.startsWith("M")) return "M";
        if (v.startsWith("F")) return "F";
        return v; // ex.: "N/A"
    }
}
