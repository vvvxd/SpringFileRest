package com.example.springfilerest.repository;


import com.example.springfilerest.model.Event;
import com.example.springfilerest.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {
    Optional<Event> findById(Long eventId);

    Event findByIdAndUser(Long id, User user);
}
