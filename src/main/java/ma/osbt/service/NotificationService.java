package ma.osbt.service;

import ma.osbt.entitie.Utilisateur;

public interface NotificationService {
	 void notifierUtilisateur(Utilisateur utilisateur, String message);
}
