package ma.osbt.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import ma.osbt.entitie.Reservation;
import ma.osbt.entitie.Utilisateur;
import ma.osbt.entitie.ProfessionnelSanteMentale;
import ma.osbt.service.PaymentService;
import ma.osbt.service.ReservationService;
import ma.osbt.service.implementation.StripePaymentService;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    @Autowired
    private   StripePaymentService stripePaymentService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    // Liste réservations pour un pro
    @GetMapping("/pro/{proId}")
    public List<Reservation> getReservationsPourPro(@PathVariable Long proId) {
        ProfessionnelSanteMentale pro = new ProfessionnelSanteMentale();
        pro.setId(proId);
        return reservationService.getReservationsPourPro(pro);
    }
 
    @PatchMapping("/statut/{id}")
    public ResponseEntity<?> modifierStatutReservation(@PathVariable Long id,
                                                       @RequestParam String statut,
                                                       @AuthenticationPrincipal ProfessionnelSanteMentale pro) {
        try {
            reservationService.validerOuRefuserReservation(id, statut, pro);
            return ResponseEntity.ok("Statut modifié");
        } catch (Exception e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }
    @GetMapping("/utilisateur/{utilisateurId}")
    public List<Reservation> getReservationsPourUtilisateur(@PathVariable Long utilisateurId) {
        return reservationService.getReservationsPourUtilisateur(utilisateurId);
    }

      @PutMapping("/annuler/{reservationId}")
    public Reservation annulerReservation(
            @PathVariable Long reservationId,
            @RequestParam Long utilisateurId) {

        return reservationService.annulerReservation(reservationId, utilisateurId);
    }
      @PostMapping
      public ResponseEntity<?> creerReservation(@RequestBody Reservation reservation,
                                                @AuthenticationPrincipal Utilisateur utilisateur) {
          if (reservation.getDateReservation() == null || reservation.getDateReservation().before(new java.util.Date())) {
              return ResponseEntity.badRequest().body("La date de réservation doit être dans le futur.");
          }

          reservation.setUtilisateur(utilisateur);
          reservation.setStatut("EN_ATTENTE");
          reservation.setPrix(null);
          Reservation saved = reservationService.save(reservation);
          return ResponseEntity.ok(saved);
      }
      @PostMapping("/api/reservations")
      public ResponseEntity<?> creerReservationAvecPaiement(
              @RequestBody Reservation reservation,
              @RequestParam String modePaiement,  // "stripe" ou "paypal"
              @AuthenticationPrincipal Utilisateur utilisateur) {

          if (reservation.getDateReservation() == null || reservation.getDateReservation().before(new Date())) {
              return ResponseEntity.badRequest().body("La date doit être dans le futur.");
          }

          Long montantEnCentimes = (long) (reservation.getProfessionnel().getPrixConsultation() * 100);

          PaymentService paymentService;
          if ("stripe".equalsIgnoreCase(modePaiement)) {
              paymentService = stripePaymentService;
          }
        /*   else if ("paypal".equalsIgnoreCase(modePaiement)) {
              paymentService = paypalPaymentService;
          } */else {
              return ResponseEntity.badRequest().body("Mode de paiement non supporté");
          }

          try {
              String paymentResponse = paymentService.createPaymentIntent(
                  montantEnCentimes,
                  "eur",
                  "https://tonapp.com/success",
                  "https://tonapp.com/cancel"
              );

              reservation.setUtilisateur(utilisateur);
              reservation.setStatut("EN_ATTENTE_PAIEMENT");
              reservation.setPrix(reservation.getProfessionnel().getPrixConsultation());
              Reservation saved = reservationService.save(reservation);

              return ResponseEntity.ok(Map.of(
                  "reservation", saved,
                  "paymentInfo", paymentResponse
              ));

          } catch (Exception e) {
              return ResponseEntity.status(500).body("Erreur paiement : " + e.getMessage());
          }
       

      }
}
