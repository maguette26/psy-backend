package ma.osbt.controller;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.osbt.config.JwtUtils;
import ma.osbt.entitie.Role;
import ma.osbt.entitie.Utilisateur;
import ma.osbt.repository.UtilisateurRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private JwtUtils jwtUtil;

    @Autowired
    private UtilisateurRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @RestControllerAdvice
    public static class GlobalExceptionHandler {

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
            String errorMessage = ex.getBindingResult()
                                    .getAllErrors()
                                    .stream()
                                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                                    .collect(Collectors.joining(", "));
            return ResponseEntity.badRequest().body(errorMessage);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            Utilisateur user = userRepository.findByEmail(loginRequest.getEmail())
                                             .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            if (!passwordEncoder.matches(loginRequest.getMotDePasse(), user.getMotDePasse())) {
                return ResponseEntity.status(401).body("Identifiants incorrects");
            }

            String role = user.getRole().name();
            String token = jwtUtil.generateToken(user.getEmail(), role);
            String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());

            return ResponseEntity.ok(new AuthResponse(token, refreshToken, role));
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Identifiants incorrects");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Validated @RequestBody RegisterRequest signUpRequest) {

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body("Erreur : cet email est déjà utilisé !");
        }

        if (userRepository.existsByTelephone(signUpRequest.getTelephone())) {
            return ResponseEntity.badRequest().body("Erreur : ce numéro de téléphone est déjà utilisé !");
        }

        if (signUpRequest.getMotDePasse().length() < 6) {
            return ResponseEntity.badRequest().body("Erreur : le mot de passe doit contenir au moins 6 caractères !");
        }

        if (!signUpRequest.getMotDePasse().equals(signUpRequest.getConfirmMotDePasse())) {
            return ResponseEntity.badRequest().body("Erreur : les mots de passe ne correspondent pas !");
        }

        Utilisateur user = new Utilisateur();
        user.setNom(signUpRequest.getNom());
        user.setPrenom(signUpRequest.getPrenom());
        user.setEmail(signUpRequest.getEmail());
        user.setTelephone(signUpRequest.getTelephone());
        user.setMotDePasse(passwordEncoder.encode(signUpRequest.getMotDePasse()));
        user.setRole(Role.USER);

        userRepository.save(user);

        return ResponseEntity.ok("Utilisateur enregistré avec succès !");
    }

    @GetMapping("/")
    public ResponseEntity<String> home() {
        return ResponseEntity.ok("Bienvenue sur l'API sécurisée !");
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Refresh token manquant");
        }

        String refreshToken = authHeader.substring(7);

        if (!jwtUtil.validateToken(refreshToken)) {
            return ResponseEntity.status(401).body("Refresh token invalide ou expiré");
        }

        String email = jwtUtil.extractEmail(refreshToken);
        Utilisateur user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        String newAccessToken = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        return ResponseEntity.ok(new AuthResponse(newAccessToken, refreshToken, user.getRole().name()));
    }

    // DTOs
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginRequest {
        private String email;
        private String motDePasse;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegisterRequest {

        @NotBlank(message = "Le nom est obligatoire")
        private String nom;

        @NotBlank(message = "Le prénom est obligatoire")
        private String prenom;

        @NotBlank(message = "L'email est obligatoire")
        @Email(message = "L'email doit être valide")
        private String email;

        @NotBlank(message = "Le numéro de téléphone est obligatoire")
        private String telephone;

        @NotBlank(message = "Le mot de passe est obligatoire")
        @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
        private String motDePasse;

        @NotBlank(message = "La confirmation du mot de passe est obligatoire")
        private String confirmMotDePasse;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AuthResponse {
        private String token;
        private String refreshToken;
        private String role;
    }
}
