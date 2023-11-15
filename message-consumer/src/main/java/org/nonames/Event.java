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
public class Event {
    private UUID id;
    private String date;
    private String time;
    private String title;
    private String description;
    private String hostEmail;
}
