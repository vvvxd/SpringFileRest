package com.example.springfilerest.service;


import com.example.springfilerest.model.Event;
import com.example.springfilerest.model.User;

public interface EventService {

    void save(Event event);

    Event findById(Long id, User user);

}
