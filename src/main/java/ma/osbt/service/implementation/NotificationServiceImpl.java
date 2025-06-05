package ma.osbt.service.implementation;

 

import org.springframework.stereotype.Service;

import ma.osbt.entitie.Utilisateur;
import ma.osbt.service.NotificationService;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Override
    public void notifierUtilisateur(Utilisateur utilisateur, String message) {
        // Ici on simule un envoi email ou notification
        System.out.println("Notification pour " + utilisateur.getEmail() + " : " + message);

        // Si tu veux envoyer un vrai email, intégrer JavaMailSender ou un service tiers ici.
    }
}
