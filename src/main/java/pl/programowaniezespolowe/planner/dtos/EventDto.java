package pl.programowaniezespolowe.planner.dtos;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventDto {

    private CalendarEventDto calendarEvent;
    private Integer userID;
    private Integer eventID;
}
