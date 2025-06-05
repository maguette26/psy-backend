package ma.osbt.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import ma.osbt.entitie.Reservation;
import ma.osbt.entitie.ProfessionnelSanteMentale;
import ma.osbt.service.ReservationService;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

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
 
    @PatchMapping("/{id}/statut")
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
}
