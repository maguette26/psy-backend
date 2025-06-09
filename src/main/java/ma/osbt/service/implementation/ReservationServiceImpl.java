package ma.osbt.service.implementation;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ma.osbt.entitie.Consultation;
import ma.osbt.entitie.Reservation;
import ma.osbt.entitie.ProfessionnelSanteMentale;
import ma.osbt.repository.ConsultationRepository;
import ma.osbt.repository.ProfessionnelSanteMentaleRepository;
import ma.osbt.repository.ReservationRepository;
import ma.osbt.repository.UtilisateurRepository;
import ma.osbt.service.NotificationService;
import ma.osbt.service.ReservationService;
@Service
@Transactional
public class ReservationServiceImpl implements ReservationService {
	@Autowired
	private   ProfessionnelSanteMentaleRepository professionnelRepository;
    private  ReservationRepository reservationRepository;
    private   ConsultationRepository consultationRepository;
    private   NotificationService notificationService;
    public ReservationServiceImpl(UtilisateurRepository utilisateurRepository,
                                  ReservationRepository reservationRepository,
                                  ConsultationRepository consultationRepository,
                                  NotificationService notificationService,
                                  ProfessionnelSanteMentaleRepository professionnelRepository) {
        this.reservationRepository = reservationRepository;
        this.consultationRepository = consultationRepository;
        this.notificationService = notificationService;
    }

    @Override
    public Reservation save(Reservation reservation) {
        Long proId = reservation.getProfessionnel().getId();
        ProfessionnelSanteMentale pro = professionnelRepository.findById(proId)
            .orElseThrow(() -> new RuntimeException("Pro non trouvé"));
        
        reservation.setPrix(pro.getPrixConsultation());
        // autres règles métier...
        
        return reservationRepository.save(reservation);
    }

    
    @Override
    public Optional<Reservation> getById(Long id) {
        return reservationRepository.findById(id);
    }

    @Override
    public List<Reservation> getReservationsPourPro(ProfessionnelSanteMentale professionnel) {
        return reservationRepository.findByProfessionnel(professionnel);
    }

    @Override
    public Reservation validerOuRefuserReservation(Long id, String statut, ProfessionnelSanteMentale professionnel) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation non trouvée"));

        if (!reservation.getProfessionnel().getId().equals(professionnel.getId())) {
            throw new RuntimeException("Action non autorisée");
        }

        reservation.setStatut(statut.toUpperCase());
        reservationRepository.save(reservation);

        if ("VALIDE".equalsIgnoreCase(statut)) {
            Consultation consultation = new Consultation();
            consultation.setDateConsultation(reservation.getDateReservation());
            consultation.setHeure(java.time.LocalTime.now());
            consultation.setProfessionnel(professionnel);
            consultation.setReservation(reservation);
            consultation.setPrix(reservation.getPrix());

            consultationRepository.save(consultation);

            notificationService.notifierUtilisateur(reservation.getUtilisateur(),
                    "Votre réservation a été validée et la consultation est planifiée.");
        }
 else if ("REFUSE".equalsIgnoreCase(statut)) {
            notificationService.notifierUtilisateur(reservation.getUtilisateur(),
                    "Votre réservation a été refusée par le professionnel.");
        }

        return reservation;
    }

    @Override
    public List<Reservation> getReservationsPourUtilisateur(Long utilisateurId) {
        return reservationRepository.findByUtilisateurId(utilisateurId);
    }

    @Override
    public Reservation annulerReservation(Long reservationId, Long utilisateurId) {
        Reservation reservation = reservationRepository.findById(reservationId)
            .orElseThrow(() -> new RuntimeException("Réservation introuvable"));

        if (!reservation.getUtilisateur().getId().equals(utilisateurId)) {
            throw new RuntimeException("Action non autorisée");
        }

        if (!reservation.getStatut().equalsIgnoreCase("EN_ATTENTE")) {
            throw new RuntimeException("Seules les réservations en attente peuvent être annulées.");
        }

        reservation.setStatut("ANNULEE");
        return reservationRepository.save(reservation);
    }
    public void marquerCommePayee(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow();
        reservation.setStatut("PAYEE");
        reservationRepository.save(reservation);
    }


}
