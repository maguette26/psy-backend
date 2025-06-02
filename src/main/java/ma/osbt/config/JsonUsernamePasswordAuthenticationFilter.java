package ma.osbt.config;

 
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
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
            String password = credentials.get("motDePasse");

            UsernamePasswordAuthenticationToken authRequest =
                    new UsernamePasswordAuthenticationToken(email, password);

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

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");

        Map<String, String> responseBody = Map.of(
            "message", "Authentification réussie",
            "email", authResult.getName()
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

