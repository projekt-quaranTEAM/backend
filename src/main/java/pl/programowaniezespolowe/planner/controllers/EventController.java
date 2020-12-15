package pl.programowaniezespolowe.planner.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.programowaniezespolowe.planner.activity.Activity;
import pl.programowaniezespolowe.planner.activity.ActivityRepository;
import pl.programowaniezespolowe.planner.dtos.CalendarEventDto;
import pl.programowaniezespolowe.planner.dtos.EventDto;
import pl.programowaniezespolowe.planner.dtos.PropositionDto;
import pl.programowaniezespolowe.planner.event.Event;
import pl.programowaniezespolowe.planner.event.EventRepository;
import pl.programowaniezespolowe.planner.proposition.Proposition;
import pl.programowaniezespolowe.planner.proposition.PropositionRepository;


import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
public class EventController {

    @Autowired
    EventRepository eventRepository;

    @Autowired
    ActivityRepository activityRepository;


    @CrossOrigin
    @GetMapping(path = "/events")
    public List<EventDto> getEvents() {
        List<Event> events = eventRepository.findAll();
        ArrayList<EventDto> mapedEvents = new ArrayList<>();
        for (Event event : events) {
            if(event.getStart() != null)
            mapedEvents.add(new EventDto(new CalendarEventDto(event.getId(), event.getTitle(), Instant.ofEpochMilli(event.getStart().getTime()), Instant.ofEpochMilli(event.getEnd().getTime())), event.getUserID(),event.getId(), event.getLink(), ""));
        }

        return mapedEvents;
    }


    @CrossOrigin
    @GetMapping(path = "/event/{id}")
    public Optional<Event> getEvent(@PathVariable String id) {
        int eventId = Integer.parseInt(id);
        return eventRepository.findById(eventId);
    }

    @CrossOrigin
    @PostMapping("/event/proposition")
    public ResponseEntity<?> saveProposition(@RequestBody PropositionDto event) {
        System.out.println(event);

        eventRepository.save(new Event(event.getUserID(), event.getCalendarEvent().getTitle(), Date.from(event.getCalendarEvent().getStart()), Date.from(event.getCalendarEvent().getEnd())));

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @CrossOrigin
    @PostMapping("/event")
    public ResponseEntity<?> createEvent(@RequestBody EventDto event) {
        System.out.println(event);
        System.out.println(event.getCategory());

        List<Activity> activities = activityRepository.findAll();

        Activity updateActivity;

        for(Activity a : activities) {
            if(a.getName().toLowerCase().equals(event.getCategory())) {
                updateActivity = a;
                updateActivity.setAmount(updateActivity.getAmount()+1);
                activityRepository.save(updateActivity);
            }
        }

        eventRepository.save(new Event(event.getUserID(), event.getCalendarEvent().getTitle(), Date.from(event.getCalendarEvent().getStart()), Date.from(event.getCalendarEvent().getEnd()), event.getLink()));

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @CrossOrigin
    @PutMapping("/event/{eventId}")
    public ResponseEntity<?> updateEvent(@RequestBody EventDto event, @PathVariable Integer eventId) {

        Optional<Event> updateEvent = eventRepository.findById(eventId);

        if(updateEvent.isPresent()) {

            updateEvent.get().setStart(Date.from(event.getCalendarEvent().getStart()));
            updateEvent.get().setEnd(Date.from(event.getCalendarEvent().getEnd()));
            updateEvent.get().setTitle(String.valueOf(event.getCalendarEvent().getTitle()));
            updateEvent.get().setUserID(event.getUserID());
            eventRepository.save(updateEvent.get());

            return new ResponseEntity<>(HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }

    @CrossOrigin
    @DeleteMapping("event/{id}")
    public List<Event> deleteEvent(@PathVariable String id) {
        int eventId = Integer.parseInt(id);
        eventRepository.deleteById(eventId);
        return eventRepository.findAll();
    }


}
