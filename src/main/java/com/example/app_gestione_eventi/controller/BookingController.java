package com.example.app_gestione_eventi.controller;

import com.example.app_gestione_eventi.entity.User;
import com.example.app_gestione_eventi.service.BookingService;
import com.example.app_gestione_eventi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BookingController {

    private final BookingService bookingService;
    private final UserService userService;

    @PostMapping("/events/{eventId}/book")
    public ResponseEntity<?> bookEvent(@PathVariable Long eventId, Authentication authentication) {
        String email = authentication.getName();
        User user = userService.getUserByEmail(email).orElse(null);

        if (user == null) {
            return ResponseEntity.status(401).body("Utente non autenticato");
        }

        try {
            bookingService.bookEvent(user.getId(), eventId);
            return ResponseEntity.ok("Prenotazione effettuata");
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
}
