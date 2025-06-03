package ma.osbt.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import ma.osbt.entitie.ProfessionnelSanteMentale;
import ma.osbt.entitie.Role;
import ma.osbt.entitie.StatutValidation;
import ma.osbt.service.ProfessionnelSanteService;

@RestController
@RequestMapping("/api/professionnels")
public class ProfessionnelSanteMentaleController {

    @Autowired
    private ProfessionnelSanteService service;

    @PostMapping("/inscription")
    public ResponseEntity<?> inscrireProfessionnel(
        @RequestParam("specialite") String specialite,
        @RequestParam("document") MultipartFile documentFile,
        @RequestParam("nom") String nom,
        @RequestParam("prenom") String prenom,
        @RequestParam("email") String email,
        @RequestParam("motDePasse") String motDePasse,
        @RequestParam("telephone") String telephone
    ) {
        try {
            // Validation de la spécialité : uniquement "psychiatrie" ou "psychologie"
            if (!specialite.equalsIgnoreCase("psychiatrie") && !specialite.equalsIgnoreCase("psychologie")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Spécialité invalide. Seules 'psychiatrie' ou 'psychologie' sont acceptées.");
            }

            // Sauvegarde du fichier sur disque
            String cheminFichier = saveFile(documentFile);

            ProfessionnelSanteMentale professionnel = new ProfessionnelSanteMentale();
            professionnel.setSpecialite(specialite);
            professionnel.setDocumentJustificatif(cheminFichier);
            professionnel.setStatutValidation(StatutValidation.EN_ATTENTE);
            professionnel.setNom(nom);
            professionnel.setPrenom(prenom);
            professionnel.setEmail(email);

            // Cryptage du mot de passe
            professionnel.setMotDePasse(new BCryptPasswordEncoder().encode(motDePasse));
            professionnel.setTelephone(telephone);

            // Attribution du rôle en fonction de la spécialité
            if (specialite.equalsIgnoreCase("psychiatrie")) {
                professionnel.setRole(Role.PSYCHIATRE);
            } else { // for psychologie
                professionnel.setRole(Role.PSYCHOLOGUE);
            }

            ProfessionnelSanteMentale saved = service.saveProfessionnel(professionnel);
            return ResponseEntity.ok(saved);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erreur: " + e.getMessage());
        }
    }
    @GetMapping("/en-attente")
    public ResponseEntity<List<ProfessionnelSanteMentale>> getProfessionnelsEnAttente() {
        List<ProfessionnelSanteMentale> pros = service.getProfessionnelsEnAttente();
        return ResponseEntity.ok(pros);
    }

    // Route PATCH pour mettre à jour le statut validation
    @PatchMapping("/{id}/validation")
    public ResponseEntity<?> updateValidation(
        @PathVariable Long id,
        @RequestBody Map<String, Boolean> body) {
        try {
            Boolean valide = body.get("valide");
            if (valide == null) {
                return ResponseEntity.badRequest().body("Le champ 'valide' est requis");
            }
            StatutValidation statut = valide ? StatutValidation.VALIDE : StatutValidation.REFUSE;
            ProfessionnelSanteMentale updated = service.updateStatutValidation(id, statut);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    

    // Méthode pour sauvegarder le fichier sur serveur local (simplifiée)
    private String saveFile(MultipartFile file) throws java.io.IOException {
        String dossier = "/chemin/vers/dossier/documents/";
        String nomFichier = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path chemin = Paths.get(dossier + nomFichier);
        Files.createDirectories(chemin.getParent());
        Files.write(chemin, file.getBytes());
        return nomFichier;
    }
}
