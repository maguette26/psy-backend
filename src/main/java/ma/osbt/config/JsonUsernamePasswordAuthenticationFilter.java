package ma.osbt.config;

 
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Map;

public class JsonUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        try {
            Map<String, String> credentials = objectMapper.readValue(request.getInputStream(), Map.class);
            String email = credentials.get("email");
            String motDePasse = credentials.get("motDePasse");

            UsernamePasswordAuthenticationToken authRequest =
                    new UsernamePasswordAuthenticationToken(email, motDePasse);

            setDetails(request, authRequest);

            return this.getAuthenticationManager().authenticate(authRequest);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        logger.info("Session ID: " + request.getSession(false).getId());

        // Créer le SecurityContext
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authResult);
        SecurityContextHolder.setContext(context);

        // Forcer la création de session
        request.getSession(true);

        // Ajouter manuellement un cookie pour tester
        /*Cookie sessionCookie = new Cookie("JSESSIONID", request.getSession(false).getId());
        sessionCookie.setHttpOnly(true);
        sessionCookie.setPath("/");
        sessionCookie.setMaxAge(-1); // session cookie
       response.addCookie(sessionCookie);*/ 

        logger.info("User roles: " + authResult.getAuthorities());

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");

        String rawRole = authResult.getAuthorities().iterator().next().getAuthority(); 
        String roleWithoutPrefix = rawRole.startsWith("ROLE_") ? rawRole.substring(5) : rawRole;

        Map<String, String> responseBody = Map.of(
            "message", "Authentification réussie",
            "email", authResult.getName(),
            "role", roleWithoutPrefix
        );

        objectMapper.writeValue(response.getWriter(), responseBody);
    }
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
                                              HttpServletResponse response,
                                              AuthenticationException failed) throws IOException, ServletException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

        Map<String, String> responseBody = Map.of(
            "error", "Échec de l'authentification",
            "message", failed.getMessage()
        );

        objectMapper.writeValue(response.getWriter(), responseBody);
    }

}

