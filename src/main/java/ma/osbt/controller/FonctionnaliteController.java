package ma.osbt.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import ma.osbt.entitie.Fonctionnalite;
import ma.osbt.service.FonctionnaliteService;

@RestController
@RequestMapping("/api/fonctionnalites")
public class FonctionnaliteController {
	 
	private final FonctionnaliteService fonctionnaliteService;

    public FonctionnaliteController(FonctionnaliteService fonctionnaliteService) {
        this.fonctionnaliteService = fonctionnaliteService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<Fonctionnalite> listFonctionnalites() {
    	
        return fonctionnaliteService.listFonctionnalites();
    }

   
    @GetMapping("/{id}")
    public Fonctionnalite fonctionnaliteParId(@PathVariable Long id) {
        return fonctionnaliteService.fonctionnaliteParId(id)
                .orElseThrow(() -> new RuntimeException("Fonctionnalité non trouvée avec id: " + id));
    }

    
    @PostMapping
    public Fonctionnalite ajouterFonctionnalite(@RequestBody Fonctionnalite fonctionnalite) {
        return fonctionnaliteService.ajouterFonctionnalite(fonctionnalite);
    }

   
    @PutMapping("/{id}")
    public void modifierFonctionnalite(@PathVariable Long id, @RequestBody Fonctionnalite fonctionnalite) {
        fonctionnalite.setId(id);  
        fonctionnaliteService.modifierFonctionnalite(fonctionnalite);
    }

  //  @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void supprimerFonctionnalite(@PathVariable Long id) {
        if (fonctionnaliteService.fonctionnaliteParId(id).isEmpty()) {
            throw new RuntimeException("Fonctionnalité non trouvée avec id: " + id);
        }
        fonctionnaliteService.supprimerFonctionnalite(id);
    }
}
