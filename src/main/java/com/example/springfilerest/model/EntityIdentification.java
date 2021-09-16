package com.example.springfilerest.model;

import lombok.Data;

import javax.persistence.*;

@MappedSuperclass
@Data
public class EntityIdentification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
}
