package br.com.MeuXamego.service;

import br.com.MeuXamego.DTO.UpdatePerfilDTO;
import br.com.MeuXamego.DTO.UpdatePerfilResponse;
import br.com.MeuXamego.DTO.UsuarioDTO;
import br.com.MeuXamego.model.*;
import br.com.MeuXamego.repository.EmpresaRepository;
import br.com.MeuXamego.repository.PessoaFisRepository;
import br.com.MeuXamego.repository.RoleRepository;
import br.com.MeuXamego.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RoleRepository roleRepository;
    private final EmpresaRepository empresaRepository;
    private final PessoaFisRepository pessoaFisRepository;
    private final VerificationService verificationService;
    private final PasswordEncoder passwordEncoder;

    // ============================================================
    // 🧾 CADASTRO DE USUÁRIO (usado no controller /cadastrar)
    // ============================================================
    public Usuario cadastrarUsuario(UsuarioDTO dto) {

        validarCamposCadastro(dto);

        // normaliza e-mail
        String email = dto.getEmail().trim().toLowerCase();

        if (usuarioRepository.existsByEmail(email)) {
            throw new IllegalStateException("Já existe uma conta com esse e-mail.");
        }

        // monta entidade Usuario
        Usuario usuario = new Usuario();
        usuario.setNome(dto.getNome().trim());
        usuario.setEmail(email);
        usuario.setSenhaHash(passwordEncoder.encode(dto.getSenha())); // senha -> hash
        usuario.setAtivo(false);                                      // só ativa após verificar e-mail
        usuario.setTelefone(dto.getTelefone());
        usuario.setBairro(dto.getBairro());
        usuario.setEnderecoDetalhes(dto.getEnderecoDetalhes());
        usuario.setUltimoLogin(null);                                 // ainda não logou
        // se tiver campo de data de criação, pode setar aqui:
        // usuario.setCriadoEm(LocalDateTime.now());

        // ROLE (ROLE_USUARIO / ROLE_ONG / ROLE_PROTETOR / ROLE_ADMIN)
        String roleNome = dto.getRoleNome(); // se no seu DTO tiver outro nome, troque esse getter
        if (roleNome == null || roleNome.isBlank()) {
            throw new IllegalArgumentException("Papel do usuário (role) é obrigatório.");
        }

        Role role = roleRepository.findByNome(roleNome)
                .orElseThrow(() -> new IllegalArgumentException("Papel inválido: " + roleNome));

        usuario.setRole(role);

        // salva usuário
        Usuario salvo = usuarioRepository.save(usuario);

        // =============================
        // Dados extras por tipo de role
        // =============================
        if ("ROLE_ONG".equals(roleNome)) {
            // ONG precisa de nomeOng + CNPJ
            if (dto.getCnpj() == null || dto.getCnpj().isBlank()) {
                throw new IllegalArgumentException("CNPJ é obrigatório para cadastro de ONG.");
            }
            if (empresaRepository.existsByCnpj(dto.getCnpj())) {
                throw new IllegalStateException("Já existe uma ONG cadastrada com esse CNPJ.");
            }

            Empresa emp = new Empresa();
            emp.setUsuario(salvo);
            emp.setNomeOng(dto.getNomeOng());
            emp.setCnpj(dto.getCnpj());
            emp.setBio(null); // pode ser preenchida depois

            empresaRepository.save(emp);

        } else if ("ROLE_PROTETOR".equals(roleNome)) {
            // PROTETOR precisa de CPF
            if (dto.getCpf() == null || dto.getCpf().isBlank()) {
                throw new IllegalArgumentException("CPF é obrigatório para cadastro de protetor.");
            }
            if (pessoaFisRepository.existsByCpf(dto.getCpf())) {
                throw new IllegalStateException("Já existe protetor cadastrado com esse CPF.");
            }

            PessoaFis pf = new PessoaFis();
            pf.setUsuario(salvo);
            pf.setCpf(dto.getCpf());
            pf.setDataNascimento(dto.getDataNascimento());
            if (dto.getSexo() != null) {
                // armazena só a inicial (M/F/O) se quiser
                pf.setSexo(dto.getSexo().name().substring(0, 1));
            }
            pf.setBio(null);

            pessoaFisRepository.save(pf);
        }

        // gera e envia código de verificação de e-mail
        verificationService.generateAndSendCode(salvo.getId());

        return salvo;
    }

    // ============================================================
    // ✏️ ATUALIZAR PERFIL DO USUÁRIO LOGADO (PUT /api/usuarios/me)
    // ============================================================
    public UpdatePerfilResponse atualizarPerfilUsuarioLogado(UpdatePerfilDTO dto) {

        // pega usuário logado a partir do e-mail no JWT
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalStateException("Usuário não autenticado.");
        }

        String emailLogado = auth.getName(); // JwtAuthenticationFilter usa o e-mail como username
        Usuario usuarioAtual = usuarioRepository.findByEmailIgnoreCase(emailLogado)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

        validarCamposUpdate(dto);

        // atualiza dados básicos
        usuarioAtual.setNome(dto.getNome().trim());
        usuarioAtual.setTelefone(dto.getTelefone());
        usuarioAtual.setBairro(dto.getBairro());
        usuarioAtual.setEnderecoDetalhes(dto.getEnderecoDetalhes());

        // ==========================
        // Bio por tipo (ONG / PROTETOR)
        // ==========================
        String role = usuarioAtual.getRole() != null ? usuarioAtual.getRole().getNome() : null;

        if (dto.getBio() != null && !dto.getBio().isBlank()) {
            if ("ROLE_ONG".equals(role)) {
                // precisa ter método findByUsuario no EmpresaRepository
                Empresa empresa = empresaRepository.findByUsuario(usuarioAtual)
                        .orElseGet(() -> {
                            Empresa e = new Empresa();
                            e.setUsuario(usuarioAtual);
                            e.setNomeOng(usuarioAtual.getNome());
                            return e;
                        });
                empresa.setBio(dto.getBio().trim());
                empresaRepository.save(empresa);

            } else if ("ROLE_PROTETOR".equals(role)) {
                // precisa ter método findByUsuario no PessoaFisRepository
                PessoaFis pessoaFis = pessoaFisRepository.findByUsuario(usuarioAtual)
                        .orElseGet(() -> {
                            PessoaFis p = new PessoaFis();
                            p.setUsuario(usuarioAtual);
                            return p;
                        });
                pessoaFis.setBio(dto.getBio().trim());
                pessoaFisRepository.save(pessoaFis);
            }
            // ROLE_USUARIO: bio é ignorada propositalmente
        }

        // ==========================
        // Troca de e-mail
        // ==========================
        boolean emailMudou = dto.getEmail() != null &&
                !dto.getEmail().equalsIgnoreCase(usuarioAtual.getEmail());

        if (emailMudou) {
            String emailNovo = dto.getEmail().trim().toLowerCase();

            if (usuarioRepository.existsByEmail(emailNovo)) {
                throw new IllegalStateException("Já existe uma conta com esse e-mail.");
            }

            usuarioAtual.setEmail(emailNovo);
            usuarioAtual.setAtivo(false); // precisa confirmar de novo
            usuarioRepository.save(usuarioAtual);

            verificationService.generateAndSendCode(usuarioAtual.getId());

            return UpdatePerfilResponse.builder()
                    .mensagem("Perfil atualizado. Verifique o novo e-mail para confirmar.")
                    .requerVerificacaoEmail(true)
                    .build();
        }

        // se e-mail não mudou, só salva normalmente
        usuarioRepository.save(usuarioAtual);

        return UpdatePerfilResponse.builder()
                .mensagem("Perfil atualizado com sucesso.")
                .requerVerificacaoEmail(false)
                .build();
    }

    // ============================================================
    // 🔎 Validações
    // ============================================================

    private static final Pattern EMAIL_REGEX =
            Pattern.compile("^[^@]+@[^@]+\\.[^@]+$");

    private void validarCamposCadastro(UsuarioDTO dto) {
        if (dto.getNome() == null || dto.getNome().isBlank()) {
            throw new IllegalArgumentException("Nome é obrigatório.");
        }
        if (dto.getEmail() == null || dto.getEmail().isBlank()
                || !EMAIL_REGEX.matcher(dto.getEmail()).matches()) {
            throw new IllegalArgumentException("E-mail inválido.");
        }
        if (dto.getSenha() == null || dto.getSenha().isBlank()) {
            throw new IllegalArgumentException("Senha é obrigatória.");
        }
        if (dto.getBairro() == null) {
            throw new IllegalArgumentException("Bairro é obrigatório.");
        }
        // telefone é opcional, mas se vier só aceita dígitos
        if (dto.getTelefone() != null && !dto.getTelefone().isBlank()
                && !dto.getTelefone().matches("^\\d{10,11}$")) {
            throw new IllegalArgumentException("Telefone deve conter apenas dígitos (DDD + número).");
        }
    }

    private void validarCamposUpdate(UpdatePerfilDTO dto) {
        if (dto.getNome() == null || dto.getNome().isBlank()) {
            throw new IllegalArgumentException("Nome é obrigatório.");
        }
        if (dto.getEmail() == null || dto.getEmail().isBlank()
                || !EMAIL_REGEX.matcher(dto.getEmail()).matches()) {
            throw new IllegalArgumentException("E-mail inválido.");
        }
        if (dto.getBairro() == null) {
            throw new IllegalArgumentException("Bairro é obrigatório.");
        }
        if (dto.getTelefone() != null && !dto.getTelefone().isBlank()
                && !dto.getTelefone().matches("^\\d{10,11}$")) {
            throw new IllegalArgumentException("Telefone deve conter apenas dígitos (DDD + número).");
        }
    }
}
