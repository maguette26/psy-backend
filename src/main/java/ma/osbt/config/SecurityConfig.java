package ma.osbt.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    private final UserDetailsService userDetailsService;

    public SecurityConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    @Bean
    @Order(1)
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        JsonUsernamePasswordAuthenticationFilter jsonAuthFilter = new JsonUsernamePasswordAuthenticationFilter();
        jsonAuthFilter.setAuthenticationManager(authenticationManager(http.getSharedObject(AuthenticationConfiguration.class)));
        jsonAuthFilter.setFilterProcessesUrl("/api/auth/login");

        http
            .securityMatcher("/api/**")
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.ALWAYS))
            .securityContext(context -> context.securityContextRepository(securityContextRepository()))
            .exceptionHandling(ex -> ex.authenticationEntryPoint(authenticationEntryPoint()))
            .authorizeHttpRequests(auth -> auth

            	    // 🔓 PUBLIC  
            	    .requestMatchers(
            	        "/api/auth/**",
            	        "/api/public/**",
            	        "/api/professionnels/inscription",
            	        "/api/fonctionnalites/citations",
            	        "/api/fonctionnalites/ressources/**",
            	        "/api/forum/**"
            	    ).permitAll()
            	    .requestMatchers(HttpMethod.GET, "/api/fonctionnalites/premium/**").permitAll()

            	    // 👤 AUTHENTIFIÉ GÉNÉRIQUE
            	    .requestMatchers("/api/auth/me").authenticated()
            	    .requestMatchers(HttpMethod.POST, "/api/forum/**").authenticated()
            	    .requestMatchers(HttpMethod.GET, "/api/fonctionnalites/**").authenticated()

            	    //   USER
            	    .requestMatchers("/api/humeurs/**").hasRole("USER")
            	    .requestMatchers("/api/reservations/utilisateur/*").hasRole("USER")
            	    .requestMatchers("/api/reservations/annuler/*").hasRole("USER")
            	    .requestMatchers(HttpMethod.POST, "/api/reservations").hasRole("USER")

            	    //  PROFESSIONNELS (PSYCHOLOGUE ou PSYCHIATRE)
            	    .requestMatchers("/api/disponibilites/**").hasAnyRole("PSYCHOLOGUE", "PSYCHIATRE")
            	    .requestMatchers(HttpMethod.GET, "/api/reservations/pro/**").hasAnyRole("PSYCHOLOGUE", "PSYCHIATRE")
            	    .requestMatchers(HttpMethod.PATCH, "/api/reservations/statut/**").hasAnyRole("PSYCHOLOGUE", "PSYCHIATRE")
            	    .requestMatchers(HttpMethod.PATCH, "/api/professionnel/prix-consultation").hasAnyRole("PSYCHOLOGUE", "PSYCHIATRE")
            	    .requestMatchers(HttpMethod.GET, "/api/professionnel/prix-consultation").hasAnyRole("PSYCHOLOGUE", "PSYCHIATRE")

            	    //  ADMIN
            	    .requestMatchers(
            	    		"/api/professionnels/en-attente","/api/professionnels/tous","/api/professionnels/fichiers/**","/api/utilisateurs/**"
            	    		).hasRole("ADMIN")
            	    .requestMatchers(HttpMethod.PATCH, "/api/professionnels/validation/*").hasRole("ADMIN")
            	    .requestMatchers("/api/fonctionnalites/**").hasRole("ADMIN") // si admin peut tout lire
            	    .requestMatchers("/api/fonctionnalites/premium/access/**").hasAnyRole("PREMIUM", "ADMIN")
            	    .requestMatchers(HttpMethod.PUT, "/api/fonctionnalites/upgrade-to-premium/**").hasRole("ADMIN")

            	    // Tout le reste nécessite d’être authentifié
            	    .anyRequest().authenticated()
            	)

            .authenticationProvider(authenticationProvider())
            .addFilterAt(jsonAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .logout(logout -> logout
                .logoutUrl("/api/auth/logout")
                .addLogoutHandler(logoutHandler())
                .logoutSuccessHandler(logoutSuccessHandler())
                .permitAll()
            );

        return http.build();
    }


    @Bean
    @Order(2)
    public SecurityFilterChain wsFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/ws-message/**", "/topic/**", "/app/**")
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.ALWAYS));
        return http.build();
    }

    @Bean
    @Order(3)
    public SecurityFilterChain defaultFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.ALWAYS));
        return http.build();
    }

    @Bean
    public LogoutHandler logoutHandler() {
        return (request, response, authentication) -> {
            if (authentication != null) {
                new SecurityContextLogoutHandler().logout(request, response, authentication);
            }
        };
    }

    @Bean
    public LogoutSuccessHandler logoutSuccessHandler() {
        return (request, response, authentication) -> {
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("Déconnexion réussie");
        };
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Accès interdit - " + authException.getMessage() + "\"}");
        };
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173", "http://192.168.1.122", "http://192.168.1.98"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*")); // ← important pour éviter les erreurs CORS
        config.setExposedHeaders(List.of("Authorization"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

}
