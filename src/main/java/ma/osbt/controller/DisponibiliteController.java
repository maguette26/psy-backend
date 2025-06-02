package ma.osbt.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import ma.osbt.entitie.Disponibilite;
import ma.osbt.service.DisponibiliteService;

@RestController
@RequestMapping("/api/disponibilites")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DisponibiliteController {

    private final DisponibiliteService disponibiliteService;

    @PostMapping
    public ResponseEntity<Disponibilite> ajouter(@RequestBody Disponibilite disponibilite) {
        return ResponseEntity.ok(disponibiliteService.ajouterDisponibilite(disponibilite));
    }

    @GetMapping
    public ResponseEntity<List<Disponibilite>> getDisponibilites() {
        return ResponseEntity.ok(disponibiliteService.getDisponibilitesProfessionnelConnecte());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Disponibilite> modifier(@PathVariable Long id, @RequestBody Disponibilite disponibilite) {
        return ResponseEntity.ok(disponibiliteService.modifierDisponibilite(id, disponibilite));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        disponibiliteService.supprimerDisponibilite(id);
        return ResponseEntity.noContent().build();
    }
}
