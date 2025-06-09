package ma.osbt.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ma.osbt.entitie.ReponseForum;
import ma.osbt.entitie.SujetForum;
import ma.osbt.entitie.Utilisateur;
import ma.osbt.repository.ReponseForumRepository;
import ma.osbt.service.SujetForumService;

@RestController
@RequestMapping("/api/forum")
public class ForumController {

    @Autowired private SujetForumService sujetService;
    @Autowired private ReponseForumRepository reponseRepo;

   
    @GetMapping("/sujets")
    public List<SujetForum> getAllSujets() {
        return sujetService.listerSujets();
    }
   
    @PostMapping("/sujets")
    public ResponseEntity<?> creerSujet(@RequestBody SujetForum sujet,
                                        @AuthenticationPrincipal Utilisateur utilisateur) {
        sujet.setAuteur(utilisateur);
        sujet.setDateCreation(LocalDateTime.now());
        return ResponseEntity.ok(sujetService.creerSujet(sujet));
    }

    
    @GetMapping("/sujets/reponses/{id}")
    public List<ReponseForum> getReponses(@PathVariable Long id) {
        return reponseRepo.findBySujetIdOrderByDateReponseAsc(id);
    }

    @PostMapping("/sujets/reponses/{id}")
    public ResponseEntity<?> repondre(@PathVariable Long id,
                                      @RequestBody ReponseForum reponse,
                                      @AuthenticationPrincipal Utilisateur utilisateur) {
        SujetForum sujet = sujetService.getSujet(id)
                .orElseThrow(() -> new RuntimeException("Sujet non trouvé"));
        reponse.setSujet(sujet);
        reponse.setAuteur(utilisateur);
        reponse.setDateReponse(LocalDateTime.now());
        return ResponseEntity.ok(reponseRepo.save(reponse));
    }
}

