package br.com.MeuXamego.Security;

import java.io.IOException; // <- usar o IOException do Java
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import br.com.MeuXamego.repository.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UsuarioRepository usuarioRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain)
            throws ServletException, IOException {

        // 1) Lê o Authorization: "Bearer <token>"
        final String authHeader = req.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // Sem header ou formato inválido -> deixa a cadeia seguir (pode virar 401/403 depois)
            chain.doFilter(req, res);
            return;
        }

        // 2) Extrai somente o token
        final String token = authHeader.substring(7);

        // 3) Extrai o "username" (email) do token; se quebrar, segue sem autenticar
        final String email;
        try {
            email = jwtUtil.getUsernameFromToken(token);
        } catch (Exception e) {
            chain.doFilter(req, res);
            return;
        }

        // 4) Se já existe Authentication no contexto OU email veio nulo -> não reautentica
        if (email == null || SecurityContextHolder.getContext().getAuthentication() != null) {
            chain.doFilter(req, res);
            return;
        }

        // 5) Busca o usuário no banco e valida assinatura/expiração do JWT
        var uOpt = usuarioRepository.findByEmailIgnoreCase(email);
        if (uOpt.isEmpty() || !jwtUtil.isValid(token)) {
            chain.doFilter(req, res);
            return;
        }

        var u = uOpt.get();

        // 6) Monta as autoridades a partir da role única do usuário
        var authorities = List.of(new SimpleGrantedAuthority(u.getRole().getNome()));

        // 7) Cria o Authentication para o Spring Security
        //    - principal: email (String). Assim, no filtro DeactivatedAccountFilter você pode usar auth.getName().
        //    - credentials: null (não precisamos da senha aqui).
        //    - authorities: permissões do usuário.
        var authToken = new UsernamePasswordAuthenticationToken(
                u.getEmail(),   // principal
                null,           // credentials
                authorities     // authorities
        );

        // 8) Adiciona detalhes da requisição (IP, user-agent)
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));

        // 9) Registra o usuário autenticado no contexto
        SecurityContextHolder.getContext().setAuthentication(authToken);

        // 10) Segue a cadeia de filtros
        chain.doFilter(req, res);
    }
}
