package br.com.MeuXamego.Security;

import br.com.MeuXamego.repository.RevokedTokenRepository;
import br.com.MeuXamego.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final UsuarioRepository usuarioRepository;
    private final RevokedTokenRepository revokedTokenRepository;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Instancia o filtro JWT
        JwtAuthenticationFilter jwtFilter =
                new JwtAuthenticationFilter(jwtUtil, usuarioRepository, revokedTokenRepository);

        http
            // Desativa CSRF (não precisa em API REST)
            .csrf(csrf -> csrf.disable())

            // Define política sem sessão (JWT)
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // Regras de autorização
            .authorizeHttpRequests(auth -> auth
              .requestMatchers(
              "/api/auth/login",
              "/api/auth/logout",
              "/api/usuarios/cadastrar"
            ).permitAll()
            .anyRequest().authenticated()
)


            // Adiciona o filtro JWT antes do filtro padrão de autenticação
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
