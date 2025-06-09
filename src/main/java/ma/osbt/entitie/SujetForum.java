package ma.osbt.entitie;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SujetForum {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titre;

    @Lob
    private String contenu;

    private LocalDateTime dateCreation = LocalDateTime.now();
 
    @ManyToOne
    private Utilisateur auteur;

    @OneToMany(mappedBy = "sujet", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<ReponseForum> reponses = new ArrayList<>();
}

