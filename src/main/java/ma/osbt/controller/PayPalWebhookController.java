package ma.osbt.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ma.osbt.service.ReservationService;

@RestController
@RequestMapping("/webhook/paypal")
public class PayPalWebhookController {

    @Autowired
    private ReservationService reservationService;

    @PostMapping
    public ResponseEntity<String> handleWebhook(@RequestBody Map<String, Object> body) {
        String eventType = (String) body.get("event_type");

        if ("PAYMENT.SALE.COMPLETED".equals(eventType)) {
            Map<String, Object> resource = (Map<String, Object>) body.get("resource");
            String invoiceNumber = (String) resource.get("invoice_number"); // à mettre dans metadata
            reservationService.marquerCommePayee(Long.parseLong(invoiceNumber));
        }

        return ResponseEntity.ok("ok");
    }
}

