package com.rosetas.config;

import com.rosetas.repository.UsuarioRepository;
import com.rosetas.security.FirebaseIdentityResolver;
import com.rosetas.security.IdentityAuthFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;
import java.util.Optional;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http,
                                    UsuarioRepository usuarios,
                                    @Value("${app.security.mode}") String mode) throws Exception {

        // Filtro criado com "new" (e não como @Bean) para não ser registrado duas vezes no container.
        IdentityAuthFilter.IdentityResolver resolver = "dev".equals(mode)
                ? req -> Optional.ofNullable(req.getHeader("X-Dev-Uid"))
                        .filter(s -> !s.isBlank())
                        .map(uid -> new IdentityAuthFilter.Identity(uid, uid + "@dev.local"))
                : new FirebaseIdentityResolver();

        http.csrf(c -> c.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(e -> e.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .authorizeHttpRequests(a -> a
                        // Público (cliente sem conta): catálogo, pedido por código e webhook do gateway
                        .requestMatchers("/api/catalogo/**", "/api/arquivos/**", "/api/publico/**", "/api/pagamentos/webhook").permitAll()
                        // Equipe
                        .requestMatchers("/api/admin/usuarios/**").hasRole("ROOT")
                        .requestMatchers("/api/admin/**").hasAnyRole("ROOT", "ADMIN")
                        .requestMatchers("/api/producao/**").hasAnyRole("ROOT", "ADMIN", "FUNCIONARIA")
                        .anyRequest().authenticated())
                .addFilterBefore(new IdentityAuthFilter(resolver, usuarios), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource(@Value("${app.cors.origins}") List<String> origins) {
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowedOrigins(origins);
        cfg.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        cfg.setAllowedHeaders(List.of("*"));
        UrlBasedCorsConfigurationSource src = new UrlBasedCorsConfigurationSource();
        src.registerCorsConfiguration("/api/**", cfg);
        return src;
    }
}
