package com.example.app_gestione_eventi.repository;

import com.example.app_gestione_eventi.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    boolean existsByUserIdAndEventId(Long userId, Long eventId);
}