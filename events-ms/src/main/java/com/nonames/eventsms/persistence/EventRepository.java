package com.nonames.eventsms.persistence;

import com.nonames.eventsms.models.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EventRepository extends JpaRepository<Event, UUID> {

}