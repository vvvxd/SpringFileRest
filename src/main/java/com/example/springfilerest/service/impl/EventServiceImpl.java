package com.example.springfilerest.service.impl;


import com.example.springfilerest.model.Event;
import com.example.springfilerest.model.User;
import com.example.springfilerest.repository.EventRepository;
import com.example.springfilerest.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    @Autowired
    public EventServiceImpl(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public void save(Event event) {
        eventRepository.save(event);
    }

    @Override
    public Event findById(Long id, User user) {
        return eventRepository.findByIdAndUser(id, user);
    }

}
