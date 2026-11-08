package com.rosetas.security;

import com.rosetas.domain.Usuario;
import com.rosetas.repository.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/** Resolve a identidade (token Firebase ou header dev) e carrega o perfil do banco. */
public class IdentityAuthFilter extends OncePerRequestFilter {

    public record Identity(String uid, String email) {
    }

    @FunctionalInterface
    public interface IdentityResolver {
        Optional<Identity> resolve(HttpServletRequest request);
    }

    private final IdentityResolver resolver;
    private final UsuarioRepository usuarios;

    public IdentityAuthFilter(IdentityResolver resolver, UsuarioRepository usuarios) {
        this.resolver = resolver;
        this.usuarios = usuarios;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        resolver.resolve(request).ifPresent(id -> {
            Usuario u = usuarios.findById(id.uid()).filter(Usuario::isAtivo).orElse(null);
            var authorities = u == null
                    ? List.<SimpleGrantedAuthority>of()
                    : List.of(new SimpleGrantedAuthority("ROLE_" + u.getPerfil().name()));
            AuthUser principal = new AuthUser(id.uid(), id.email(), u == null ? null : u.getPerfil());
            SecurityContextHolder.getContext()
                    .setAuthentication(new UsernamePasswordAuthenticationToken(principal, null, authorities));
        });
        chain.doFilter(request, response);
    }
}
