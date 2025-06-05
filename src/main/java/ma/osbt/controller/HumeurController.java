package ma.osbt.controller;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import ma.osbt.entitie.Humeur;
import ma.osbt.service.HumeurService;

@RestController
@RequestMapping("/api/humeurs")
public class HumeurController {

    private final HumeurService humeurService;

    public HumeurController(HumeurService humeurService) {
        this.humeurService = humeurService;
    }

    @PostMapping
    public Humeur ajouterHumeur(@RequestBody Humeur humeur) {
        return humeurService.ajouterHumeur(humeur);
    }

    @GetMapping("/utilisateur/{utilisateurId}")
    public List<Humeur> getHumeursUtilisateur(@PathVariable Long utilisateurId) {
        return humeurService.getHumeursUtilisateur(utilisateurId);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerHumeur(@PathVariable Long id) {
        humeurService.supprimerHumeur(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Humeur> modifierHumeur(@PathVariable Long id, @RequestBody Humeur humeur) {
        return ResponseEntity.ok(humeurService.modifierHumeur(id, humeur));
    }

    @GetMapping("/utilisateur/{utilisateurId}/date")
    public List<Humeur> getHumeursParDate(
        @PathVariable Long utilisateurId,
        @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return humeurService.getHumeursParDate(utilisateurId, date);
    }

    @GetMapping("/utilisateur/{utilisateurId}/periode")
    public List<Humeur> getHumeursParPeriode(
        @PathVariable Long utilisateurId,
        @RequestParam("debut") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
        @RequestParam("fin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin
    ) {
        return humeurService.getHumeursParPeriode(utilisateurId, debut, fin);
    }

}
