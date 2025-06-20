package com.example.app_gestione_eventi.service;

import com.example.app_gestione_eventi.entity.Event;
import com.example.app_gestione_eventi.entity.User;
import com.example.app_gestione_eventi.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public Event createEvent(Event event, User organizer) {
        event.setOrganizer(organizer);
        return eventRepository.save(event);
    }

    public Optional<Event> getEventById(Long id) {
        return eventRepository.findById(id);
    }

    public Event updateEvent(Long id, Event eventDetails, User user) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Evento non trovato"));

        if (!event.getOrganizer().getId().equals(user.getId())) {
            throw new SecurityException("Non autorizzato");
        }

        event.setTitle(eventDetails.getTitle());
        event.setDescription(eventDetails.getDescription());
        event.setDate(eventDetails.getDate());
        event.setLocation(eventDetails.getLocation());
        event.setAvailableSeats(eventDetails.getAvailableSeats());

        return eventRepository.save(event);
    }

    public void deleteEvent(Long id, User user) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Evento non trovato"));

        if (!event.getOrganizer().getId().equals(user.getId())) {
            throw new SecurityException("Non autorizzato");
        }

        eventRepository.delete(event);
    }
}
