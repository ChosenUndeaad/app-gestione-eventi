package com.example.app_gestione_eventi.controller;

import com.example.app_gestione_eventi.entity.Event;
import com.example.app_gestione_eventi.entity.User;
import com.example.app_gestione_eventi.service.EventService;
import com.example.app_gestione_eventi.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EventController {

    private final EventService eventService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<Event>> getAllEvents() {
        return ResponseEntity.ok(eventService.getAllEvents());
    }

    @PostMapping
    public ResponseEntity<?> createEvent(@Valid @RequestBody Event event, Authentication authentication) {
        String email = authentication.getName();
        Optional<User> userOpt = userService.getUserByEmail(email);

        if (userOpt.isEmpty() || userOpt.get().getRole() != User.Role.ORGANIZZATORE) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Solo organizzatori possono creare eventi");
        }

        Event savedEvent = eventService.createEvent(event, userOpt.get());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedEvent);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEvent(@PathVariable Long id,
                                         @Valid @RequestBody Event eventDetails,
                                         Authentication authentication) {
        String email = authentication.getName();
        Optional<User> userOpt = userService.getUserByEmail(email);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Utente non autenticato");
        }

        try {
            Event updatedEvent = eventService.updateEvent(id, eventDetails, userOpt.get());
            return ResponseEntity.ok(updatedEvent);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Evento non trovato");
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Non autorizzato a modificare questo evento");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Errore interno");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEvent(@PathVariable Long id, Authentication authentication) {
        String email = authentication.getName();
        Optional<User> userOpt = userService.getUserByEmail(email);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Utente non autenticato");
        }

        try {
            eventService.deleteEvent(id, userOpt.get());
            return ResponseEntity.ok("Evento eliminato");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Evento non trovato");
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Non autorizzato a eliminare questo evento");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Errore interno");
        }
    }
}
