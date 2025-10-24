package br.com.MeuXamego.Security;

import br.com.MeuXamego.repository.RevokedTokenRepository;
import br.com.MeuXamego.repository.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UsuarioRepository usuarioRepository;
    private final RevokedTokenRepository revokedTokenRepository;

    public JwtAuthenticationFilter(JwtUtil jwtUtil,
                                   UsuarioRepository usuarioRepository,
                                   RevokedTokenRepository revokedTokenRepository) {
        this.jwtUtil = jwtUtil;
        this.usuarioRepository = usuarioRepository;
        this.revokedTokenRepository = revokedTokenRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        String path = request.getServletPath();

        // 🚫 Ignorar autenticação para rotas públicas (login e cadastro)
        if (path.startsWith("/api/auth") || path.equals("/api/usuarios/cadastrar")) {
            chain.doFilter(request, response);
            return;
        }

        // 🔍 Extrai header Authorization
        String authHeader = request.getHeader("Authorization");

        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        // ❌ Token inválido ou revogado → segue sem autenticar
        if (!jwtUtil.isValid(token) || revokedTokenRepository.existsByToken(token)) {
            chain.doFilter(request, response);
            return;
        }

        // ✅ Extrai e-mail do token
        String email = jwtUtil.getUsernameFromToken(token);
        if (!StringUtils.hasText(email)) {
            chain.doFilter(request, response);
            return;
        }

        var usuarioOpt = usuarioRepository.findByEmail(email);
        if (usuarioOpt.isEmpty()) {
            chain.doFilter(request, response);
            return;
        }

        var usuario = usuarioOpt.get();

        var authorities = usuario.getRoles().stream()
                .map(r -> new org.springframework.security.core.authority.SimpleGrantedAuthority(r.getNome()))
                .toList();

        var auth = new UsernamePasswordAuthenticationToken(usuario, null, authorities);
        auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(auth);

        chain.doFilter(request, response);
    }
}