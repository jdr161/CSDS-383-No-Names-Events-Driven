package com.nonames.eventsms.business;

import com.nonames.eventsms.models.Event;
import com.nonames.eventsms.models.Participant;
import com.nonames.eventsms.persistence.EventRepository;
import com.nonames.eventsms.persistence.ParticipantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/api")
public class Controller {
    @Autowired
    EventRepository eventRepository;

    @Autowired
    ParticipantRepository participantRepository;

    @Value("${participant-api-url}")
    String participantApiUrl;

    @GetMapping("/view-events")
    public ResponseEntity<List<Event>> getAllEvents() {
        List<Event> events = eventRepository.findAll();
        return ResponseEntity.ok(events);
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/create-event")
    public ResponseEntity<Event> addEvent(@RequestBody Event event) {
        if (eventRepository.existsById(event.getId()))
            throw new ResponseStatusException(CONFLICT, "Event UUID already exists");

        Event createdEvent = eventRepository.save(event);
        return ResponseEntity.ok(createdEvent);
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @PutMapping("/register-participant")
    public ResponseEntity<Event> registerParticipant(@RequestParam UUID participantId, @RequestParam UUID eventId) {
        Optional<Event> event = eventRepository.findById(eventId);
        
        if (event.isEmpty())
            throw new ResponseStatusException(NOT_FOUND, "Event UUID not found");
        if (fetchParticipantData(participantId) == null) {
            throw new ResponseStatusException(NOT_FOUND, "Participant UUID not found");
        }

        fetchAndSaveParticipant(participantId);
        Optional<Participant> participant = participantRepository.findById(participantId);
        event.get().getParticipants().add(participant.get());
        eventRepository.save(event.get());

        return ResponseEntity.ok(event.get());
    }

    private Participant fetchAndSaveParticipant(UUID participantId){
        Participant participant = fetchParticipantData(participantId);
        Participant savedParticipant = participantRepository.save(participant);
        return savedParticipant;
    }

    private Participant fetchParticipantData(UUID participantId){
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Participant> response = restTemplate.getForEntity(participantApiUrl + "/get-participant-by-id?id=" + participantId.toString(), Participant.class);
        return response.getBody();
    }
}
