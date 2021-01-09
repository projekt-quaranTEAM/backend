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
import pl.programowaniezespolowe.planner.user.User;
import pl.programowaniezespolowe.planner.user.UserRepository;


import java.time.Instant;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;

@RestController
public class EventController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    EventRepository eventRepository;

    @Autowired
    ActivityRepository activityRepository;

    static Map<Integer, Map<String, Date>> lastActivities = new HashMap<Integer, Map<String, Date>>();

    public static Map<Integer, Map<String, Date>> getLastActivities() {
        return lastActivities;
    }

    public boolean checkIsUserLogged(String userid) {
        List<User> users = userRepository.findAll();
        for(User u : users) {
            if(u.getId() == Integer.valueOf(userid)) {
                if (u.isLogged()) {
                    return true;
                }
            }
        }
        return false;
    }



    @CrossOrigin
    @GetMapping(path = "/{userid}/events")
    public List<EventDto> getEvents(@PathVariable String userid) {
        List<Event> events = eventRepository.findAll();

        boolean canReturn = checkIsUserLogged(userid);

        ArrayList<EventDto> mapedEvents = new ArrayList<>();
        for (Event event : events) {
            if(event.getStart() != null)
            {
                if(event.getUserID().toString().equals(userid)) {
                    mapedEvents.add(new EventDto(new CalendarEventDto(event.getId(), event.getTitle(), Instant.ofEpochMilli(event.getStart().getTime()), Instant.ofEpochMilli(event.getEnd().getTime())), event.getUserID(),event.getId(), event.getLink(), ""));

                }
            }
        }
        if(canReturn) {
            return mapedEvents;
        }
        return null;
    }


    @CrossOrigin
    @GetMapping(path = "/{userid}/event/{id}")
    public Optional<Event> getEvent(@PathVariable String userid, @PathVariable String id) {

        boolean canReturn = checkIsUserLogged(userid);

        int eventId = Integer.parseInt(id);
        if(canReturn) return eventRepository.findById(eventId);
        else return null;
    }

    @CrossOrigin
    @PostMapping("/{userid}/event/proposition")
    public ResponseEntity<?> saveProposition(@PathVariable String userid, @RequestBody PropositionDto event) {
        System.out.println(event);
        boolean canReturn = checkIsUserLogged(userid);

        if(canReturn) eventRepository.save(new Event(Integer.valueOf(userid), event.getCalendarEvent().getTitle(), Date.from(event.getCalendarEvent().getStart()), Date.from(event.getCalendarEvent().getEnd())));

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @CrossOrigin
    @PostMapping("/{userid}/event")
    public ResponseEntity<?> createEvent(@PathVariable String userid, @RequestBody EventDto event) {
        System.out.println(event);
        System.out.println(event.getCategory());
        boolean canReturn = checkIsUserLogged(userid);

        if(canReturn) {
            List<Activity> activities = activityRepository.findAll();

            Activity updateActivity;
            //int id = 1;

            for (Activity a : activities) {
                if (a.getName().toLowerCase().equals(event.getCategory())) {
                    updateActivity = a;
                    updateActivity.setAmount(updateActivity.getAmount() + 1);

                    //Last events user
                    boolean isInside = false;
                    for (Map.Entry<Integer, Map<String, Date>> en : lastActivities.entrySet()) {
                        if (en.getKey().equals(userid)) {
                            isInside = true;
                        }
                    }

                    if (!isInside) {
                        lastActivities.put(Integer.valueOf(userid), new HashMap<String, Date>());
                    }

                    for (Map.Entry<Integer, Map<String, Date>> en : lastActivities.entrySet()) {
                        if (en.getKey().equals(userid)) {
                            Map<String, Date> li = en.getValue();
                            li.put(a.getName(), Calendar.getInstance().getTime());
                            if (li.size() > 3) li.remove(0);
                        }
                    }

                    activityRepository.save(updateActivity);
                }
            }

            eventRepository.save(new Event(event.getUserID(), event.getCalendarEvent().getTitle(), Date.from(event.getCalendarEvent().getStart()), Date.from(event.getCalendarEvent().getEnd()), event.getLink()));
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @CrossOrigin
    @PutMapping("/{userid}/event/{eventId}")
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
    @DeleteMapping("/{userid}/event/{id}")
    public List<Event> deleteEvent(@PathVariable String userid, @PathVariable String id) {
        int eventId = Integer.parseInt(id);
        boolean canReturn = checkIsUserLogged(userid);
        List<Event> events = eventRepository.findAll();

        eventRepository.deleteById(eventId);
        ArrayList<Event> mapedEvents = new ArrayList<>();
        for (Event event : events) {
            if(event.getStart() != null)
            {
                if(event.getUserID().toString().equals(userid)) {
                    mapedEvents.add(event);
                }
            }
        }
        if(canReturn) {
            return mapedEvents;
        }
        return null;
    }


}
