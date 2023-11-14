package com.nonames.participantsms.controller;

import com.nonames.participantsms.model.Participant;
import com.nonames.participantsms.repository.ParticipantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class ParticipantController {
    @Autowired
    private ParticipantRepository participantRepository;

    @PostMapping("/create-participant")
    public ResponseEntity<?> createParticipant(@RequestBody Participant participant) {
        if (participantRepository.existsById(participant.getId())) {
            String errorMessage = "Participant with UUID " + participant.getId() + " already exists";
            throw new ResponseStatusException(HttpStatus.CONFLICT, errorMessage);
        }

        Participant newParticipant = participantRepository.save(participant);
        return ResponseEntity.ok().body(newParticipant);
    }

    @GetMapping("/view-participants")
    public ResponseEntity<?> getAllParticipants() {
        List<Participant> participants = participantRepository.findAll();
        return ResponseEntity.ok().body(participants);
    }

    @GetMapping("/get-participant-by-id")
    public ResponseEntity<?> getParticipant(@RequestParam UUID id) {
        Optional<Participant> optParticipant = participantRepository.findById(id);
        if (optParticipant.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Participant participant = optParticipant.get();
        return ResponseEntity.ok().body(participant);
    }
}
