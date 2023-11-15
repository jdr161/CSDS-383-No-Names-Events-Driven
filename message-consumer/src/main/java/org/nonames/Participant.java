package org.nonames;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Participant {
    private UUID participantId;
    private String participantName;
    private String participantEmail;
}
