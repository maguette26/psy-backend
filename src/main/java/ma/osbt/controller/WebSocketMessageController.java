package ma.osbt.controller;
 
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import ma.osbt.entitie.Message;

@Controller
public class WebSocketMessageController {

    private final List<String> motsInterdits = List.of("suicide", "haine", "violence", "insulte");

    private boolean estInapproprie(String contenu) {
        if (contenu == null) return false;
        String lower = contenu.toLowerCase();
        return motsInterdits.stream().anyMatch(lower::contains);
    }

    @MessageMapping("/send") // /app/send
    @SendTo("/topic/messages") // tout le monde abonné reçoit
    public Message envoyerMessage(Message message) throws Exception {
        if (estInapproprie(message.getContenu())) {
            return null; // ou gérer avec une exception spécifique
        }
        message.setDate(new Date());
        message.setHeure(LocalTime.now());
        return message;
    }
}

