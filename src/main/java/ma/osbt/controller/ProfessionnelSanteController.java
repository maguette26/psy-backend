package ma.osbt.controller;

import java.util.List;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import ma.osbt.entitie.Disponibilite;
import ma.osbt.entitie.DocumentJustificatif;
import ma.osbt.entitie.Personne;
import ma.osbt.entitie.ProfessionnelSanteMentale;
import ma.osbt.service.ProfessionnelSanteService;

@RestController
@RequestMapping("/api/professionnels")
public class ProfessionnelSanteController {

    @Autowired
    private ProfessionnelSanteService professionnelSanteService;

    // 🔹 Lister toutes les personnes (ancienne route /tous)
    @GetMapping("/personnes")
    public ResponseEntity<List<Personne>> listProfessionnelsPersonne() {
        return ResponseEntity.ok(professionnelSanteService.listProfessionnelSantes());
    }

    // 🔹 Lister tous les professionnels
    //@GetMapping("/professionnels-sante")
   // public ResponseEntity<List<ProfessionnelSanteMentale>> listProfessionnel() {
    //    return ResponseEntity.ok(professionnelSanteService.listProfessionnelSante());
    //}

    // 🔹 Obtenir un professionnel par ID
    @GetMapping("/{id}")
    public ResponseEntity<ProfessionnelSanteMentale> professionnelById(@PathVariable Long id) {
        return professionnelSanteService.professionnelById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 🔹 Ajouter un professionnel
    @PostMapping("/ajouter")
    public ResponseEntity<Void> ajouterProfessionnel(@Valid @RequestBody ProfessionnelSanteMentale p) {
        professionnelSanteService.ajouterProfessionnel(p);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // 🔹 Supprimer un professionnel
    @DeleteMapping("/supprimer/{id}")
    public ResponseEntity<Void> supprimerProfessionnel(@PathVariable Long id) {
        professionnelSanteService.supprimerProfessionnel(id);
        return ResponseEntity.noContent().build();
    }

    // 🔹 Modifier un professionnel
    @PutMapping("/modifier/{id}")
    public ResponseEntity<Void> modifierProfessionnel(@PathVariable long id, @Valid @RequestBody ProfessionnelSanteMentale p) {
        p.setId(id);
        professionnelSanteService.modifierProfessionnel(p);
        return ResponseEntity.ok().build();
    }

    // 🔹 Lister les professionnels en attente
    @GetMapping("/en-attente")
    public ResponseEntity<List<ProfessionnelSanteMentale>> professionnelsEnAttente() {
        return ResponseEntity.ok(professionnelSanteService.professionnelsEnAttente());
    }

    // 🔹 Valider un professionnel
    @PostMapping("/valider/{id}")
    public ResponseEntity<String> validerProfessionnel(@PathVariable Long id) {
        professionnelSanteService.validerProfessionnel(id);
        return ResponseEntity.ok("Professionnel validé.");
    }

    // 🔹 Refuser un professionnel
    @PostMapping("/refuser/{id}")
    public ResponseEntity<String> refuserProfessionnel(@PathVariable Long id) {
        professionnelSanteService.refuserProfessionnel(id);
        return ResponseEntity.ok("Professionnel refusé.");
    }

    // 🔹 Ajouter un document justificatif
    @PostMapping("/ajouterdocument/{id}")
    public ResponseEntity<String> ajouterDocument(@PathVariable Long id, @RequestParam("fichier") MultipartFile fichier) {
        professionnelSanteService.ajouterDocument(id, fichier);
        return ResponseEntity.ok("Document ajouté avec succès.");
    }

    // 🔹 Lister les documents d’un professionnel
    @GetMapping("/documentsparprofessionnel/{id}")
    public ResponseEntity<List<DocumentJustificatif>> getDocumentsParProfessionnel(@PathVariable("id") Long professionnelId) {
        return ResponseEntity.ok(professionnelSanteService.getDocumentsParProfessionnel(professionnelId));
    }

    // 🔹 Ajouter une disponibilité
    @PostMapping("/{professionnelId}/disponibilites")
    public ResponseEntity<Disponibilite> ajouterDisponibilite(
            @PathVariable Long professionnelId,
            @Valid @RequestBody Disponibilite disponibilite) {
        Disponibilite result = professionnelSanteService.ajouterDisponibilite(professionnelId, disponibilite);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    // 🔹 Supprimer une disponibilité
    @DeleteMapping("/disponibilites/{id}")
    public ResponseEntity<Void> supprimerDisponibilite(@PathVariable Long id) {
        professionnelSanteService.supprimerDisponibilite(id);
        return ResponseEntity.noContent().build();
    }

    // 🔹 Modifier une disponibilité
    @PutMapping("/disponibilites/{id}")
    public ResponseEntity<Void> modifierDisponibilite(
            @PathVariable Long id,
            @Valid @RequestBody Disponibilite disponibilite) {
        professionnelSanteService.modifierDisponibilite(id, disponibilite);
        return ResponseEntity.ok().build();
    }

    // 🔹 Lister les disponibilités d’un professionnel
    @GetMapping("/{professionnelId}/disponibilites")
    public ResponseEntity<List<Disponibilite>> listeDisponibilitesParProfessionnel(@PathVariable Long professionnelId) {
        return ResponseEntity.ok(professionnelSanteService.listeDisponibilitesParPro(professionnelId));
    }
    
   
}
