package ma.osbt.controller;

import java.time.LocalTime;

import java.util.Date;
 

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import ma.osbt.entitie.Message;
import ma.osbt.entitie.Personne;
import ma.osbt.service.MessageService;
import ma.osbt.repository.PersonneRepository;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private PersonneRepository personneRepository;

    @PostMapping("/envoyer")
    public ResponseEntity<?> envoyerMessage(@RequestBody Message message,@AuthenticationPrincipal Personne expediteur) {
        message.setDate(new Date());
        message.setHeure(LocalTime.now());
        message.setExpediteur(expediteur);
        try {
            Message saved = messageService.envoyerMessage(message);
            return ResponseEntity.ok(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Message inapproprié détecté");
        }
    }

    @GetMapping("/entre/{autreId}")
    public ResponseEntity<?> getMessagesAvec(@PathVariable Long autreId,@AuthenticationPrincipal Personne utilisateur) {
        Personne autre = personneRepository.findById(autreId).orElse(null);
        if (autre == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(messageService.getMessagesEntre(utilisateur, autre));
    }
}
