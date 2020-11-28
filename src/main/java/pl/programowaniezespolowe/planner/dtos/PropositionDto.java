package pl.programowaniezespolowe.planner.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PropositionDto {

    private CalendarEventDto calendarEvent;
    private String link;
    private String category;
    private Integer id;
    private Integer userId;


}
