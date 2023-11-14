package com.nonames.participantsms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.nonames.participantsms.model.Participant;

import java.util.UUID;

public interface ParticipantRepository extends JpaRepository<Participant, UUID> {
}