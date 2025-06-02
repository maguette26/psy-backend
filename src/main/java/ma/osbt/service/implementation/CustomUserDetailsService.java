package ma.osbt.service.implementation;

import ma.osbt.entitie.Personne;
import ma.osbt.repository.UtilisateurRepository;

 
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

 

@Service
//@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

	//@Autowired
    private final UtilisateurRepository utilisateurRepository;

    public CustomUserDetailsService(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Personne utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé"));

        return org.springframework.security.core.userdetails.User.builder()
                .username(utilisateur.getEmail())
                .password(utilisateur.getMotDePasse()) 
                .roles(utilisateur.getRole().name())
               // .authorities("ROLE_" + utilisateur.getRole().name())
                .build();
    }

}