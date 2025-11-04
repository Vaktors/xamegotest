package br.com.MeuXamego.Security;

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

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    

    @Bean PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
  .csrf(csrf -> csrf.disable())
  .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
  .authorizeHttpRequests(auth -> auth
        .requestMatchers(
                "/api/auth/login",
                "/api/auth/logout",

                // 🔓 ESQUECI / REDEFINIR SENHA – ABERTOS
                "/api/auth/password/esqueci",
                "/api/auth/password/redefinir",

                "/api/usuarios/cadastrar",
                "/auth/email/**",
                "/", "/login/**", "/cadastro/**", "/Home/**",
                "/css/**", "/js/**", "/img/**", "/static/**"
        ).permitAll()
        .requestMatchers("/api/usuarios/me/**").authenticated()
        .anyRequest().authenticated()
)

  
  // garanta a ordem estável:
  .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
  


        return http.build();
    }
}
