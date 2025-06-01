package ma.osbt.service.implementation;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ma.osbt.entitie.Message;
import ma.osbt.entitie.Personne;
import ma.osbt.repository.MessageRepository;
import ma.osbt.service.MessageService;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageRepository messageRepository;

    private final List<String> motsInterdits = List.of("suicide", "haine", "violence", "insulte");

    private boolean estInapproprie(String contenu) {
        if (contenu == null) return false;
        String lower = contenu.toLowerCase();
        return motsInterdits.stream().anyMatch(lower::contains);
    }

    @Override
    public Message envoyerMessage(Message message) {
        if (message.getContenu() == null || estInapproprie(message.getContenu())) {
            throw new IllegalArgumentException("Message inapproprié");
        }
        return messageRepository.save(message);
    }

    @Override
    public List<Message> getMessagesEntre(Personne p1, Personne p2) {
        return messageRepository.findByExpediteurOrDestinataireOrderByDateDesc(p1, p2).stream()
                .filter(m -> (m.getExpediteur().equals(p1) && m.getDestinataire().equals(p2)) ||
                             (m.getExpediteur().equals(p2) && m.getDestinataire().equals(p1)))
                .toList();
    }

    @Override
    public List<Message> getMessagesPourUtilisateur(Personne personne) {
        return messageRepository.findByExpediteurOrDestinataireOrderByDateDesc(personne, personne);
    }
}
