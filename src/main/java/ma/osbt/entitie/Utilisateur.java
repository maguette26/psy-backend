package ma.osbt.entitie;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor @AllArgsConstructor
@DiscriminatorValue(value="UTILISATEUR")
public class Utilisateur extends Personne {
    
    private boolean anonymat;

    @OneToMany(mappedBy = "utilisateur")
    @JsonIgnore
    private List<Reservation> reservations;

    @JsonIgnore
    @OneToMany(mappedBy = "expediteur")
    private List<Message> messagesEnvoyes;

    @JsonIgnore
    @OneToMany(mappedBy = "destinataire")
    private List<Message> messagesRecus;
}
