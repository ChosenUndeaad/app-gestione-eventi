package com.example.app_gestione_eventi.service;

import com.example.app_gestione_eventi.entity.Booking;
import com.example.app_gestione_eventi.entity.Event;
import com.example.app_gestione_eventi.entity.User;
import com.example.app_gestione_eventi.repository.BookingRepository;
import com.example.app_gestione_eventi.repository.EventRepository;
import com.example.app_gestione_eventi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    public Booking bookEvent(Long userId, Long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Evento non trovato"));

        if(event.getAvailableSeats() <= 0) {
            throw new RuntimeException("Non ci sono posti disponibili");
        }

        boolean alreadyBooked = bookingRepository.existsByUserIdAndEventId(userId, eventId);
        if(alreadyBooked) {
            throw new RuntimeException("Hai già prenotato questo evento");
        }

        event.setAvailableSeats(event.getAvailableSeats() - 1);
        eventRepository.save(event);

        Booking booking = Booking.builder()
                .user(user)
                .event(event)
                .createdAt(LocalDateTime.now())
                .build();

        return bookingRepository.save(booking);
    }
}
