package com.nonames.eventsms.persistence;

import com.nonames.eventsms.models.Participant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ParticipantRepository extends JpaRepository<Participant, UUID> {

}