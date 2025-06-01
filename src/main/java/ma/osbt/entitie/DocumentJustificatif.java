package ma.osbt.entitie;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor @AllArgsConstructor  
public class DocumentJustificatif {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String nomFichier;
	private String urlStockage;
	private boolean valide;
	
	@Enumerated(EnumType.STRING)
	private TypeDocumentJustificatif type;
    
    @ManyToOne
    private ProfessionnelSanteMentale professionnel;
}
