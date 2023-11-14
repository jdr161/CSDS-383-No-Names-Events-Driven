package com.nonames.participantsms.model;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "participants")
public class Participant {
    @Id
    private UUID id;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    public Participant() {

    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}