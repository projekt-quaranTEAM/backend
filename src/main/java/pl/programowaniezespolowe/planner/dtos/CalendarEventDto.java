package pl.programowaniezespolowe.planner.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CalendarEventDto {

    private int id;
    private String title;
    private Instant start;
    private Instant end;


    // powinien zawierac primary i secondary color - na razie niech zostanie to ustawiane na froncie
//    private String[] color;

    //opcjonalne - nie obslugujemy tego na razie na backu - na razie na froncie bedzie dodawane recznie
    //okresla akcje dostepne dla eventu np edycje lub dodawanie notatki
    //w przyszlosci mozna zmienic na enum
    // a11yLabel: 'Edit' lub a11yLabel: 'Note'
//    private String[] actions;

    //opcjonalne - nie obslugujemy tego na razie
//    private Integer draggable;

    //opcjonalne - nie obslugujemy tego na razie
//    private Integer beforeStart;

    //opcjonalne - nie obslugujemy tego na razie
//    private Integer afterEnd;
}

//back musi obsluzyc odebranie i wyslanie obiektu calendarevent w Event
//calendar event ma byc zwracany/wysylany z:
// title, start, end (end jest opcjonalne)
