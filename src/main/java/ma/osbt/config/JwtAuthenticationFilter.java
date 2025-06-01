package ma.osbt.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtUtils jwtUtil;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtUtils jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getServletPath();

        // Bypass pour les routes publiques
        if (path.startsWith("/api/auth/")
                || path.startsWith("/api/public/")
                || path.equals("/api/professionnels")
                || path.startsWith("/api/professionnels/")) {
            filterChain.doFilter(request, response);
            return;
        }
        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warn("Token manquant ou mal formé pour la requête: {}", path);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token manquant ou mal formé");
            return;
        }

        String jwt = authHeader.substring(7);

        try {
            if (!jwtUtil.validateToken(jwt)) {
                logger.warn("Token invalide ou expiré pour la requête: {}", path);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token expiré ou invalide");
                return;
            }

            Claims claims = jwtUtil.extractAllClaims(jwt);
            String email = claims.getSubject();

            // Si email absent ou déjà authentifié, on bloque
            if (email == null || SecurityContextHolder.getContext().getAuthentication() != null) {
                logger.warn("Token invalide ou contexte d'authentification déjà existant");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token invalide");
                return;
            }

            String role = claims.get("role", String.class);
            if (role == null) {
                logger.warn("Role absent dans le token");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Role absent dans le token");
                return;
            }

            List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));

            // Charger les détails utilisateur depuis la base si besoin
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    authorities
            );

            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);

            logger.debug("Authentifié: {} | Rôle: {} | Path: {}", email, role, path);

        } catch (ExpiredJwtException e) {
            logger.error("Token expiré: {}", e.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token expiré");
            return;
        } catch (JwtException | IllegalArgumentException e) {
            logger.error("Erreur JWT: {}", e.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token invalide");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
