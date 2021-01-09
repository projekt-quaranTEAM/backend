package pl.programowaniezespolowe.planner.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pl.programowaniezespolowe.planner.activity.Activity;
import pl.programowaniezespolowe.planner.activity.ActivityRepository;
import pl.programowaniezespolowe.planner.dtos.CalendarEventDto;
import pl.programowaniezespolowe.planner.dtos.PropositionDto;
import pl.programowaniezespolowe.planner.event.Event;
import pl.programowaniezespolowe.planner.event.EventRepository;
import pl.programowaniezespolowe.planner.proposition.Proposition;
import pl.programowaniezespolowe.planner.proposition.PropositionRepository;
import pl.programowaniezespolowe.planner.proposition.PropositionUrl;
import pl.programowaniezespolowe.planner.user.User;
import pl.programowaniezespolowe.planner.user.UserRepository;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static pl.programowaniezespolowe.planner.controllers.EventController.getLastActivities;

@RestController
public class PropositionController {

    @Autowired
    PropositionRepository propositionRepository;

    @Autowired
    ActivityRepository activityRepository;

    @Autowired
    EventRepository eventRepository;

    @Autowired
    UserRepository userRepository;

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
    @GetMapping("/{userid}/proposition")
    public List<PropositionDto> getPropositions(@PathVariable String userid) {
        List<Proposition> propositions = getThreeCategoriesAlgorithm(Integer.valueOf(userid));
        ArrayList<PropositionDto> mapedEvents = new ArrayList<>();
        for (Proposition proposition : propositions) {
            if(proposition.getStartdate() != null)
            mapedEvents.add(new PropositionDto(new CalendarEventDto(proposition.getId(),proposition.getName(),Instant.ofEpochMilli(proposition.getStartdate().getTime()),Instant.ofEpochMilli(proposition.getStartdate().getTime())),proposition.getLink(),proposition.getCategory(),proposition.getId(),proposition.getUserid()));
        }

        return mapedEvents;
    }

    //Algorithm
    public List<Proposition> getThreeCategoriesAlgorithm(int id) {
        List<Proposition> li = propositionRepository.findAll();
        List<Activity> ac = activityRepository.findAll();
        List<Event> ev = eventRepository.findAll();

        List<Activity> sortedActivities = ac.stream()
                .sorted(Comparator.comparing(Activity::getAmount).reversed())
                .collect(Collectors.toList());

        Map<String, Integer> weights = new HashMap<>();

        for(Activity a : sortedActivities)
        {
            weights.put(a.getName(), a.getAmount());
        }
        //System.out.println(weights.toString());
        for(Event e : ev) {
            if(e.getLink() != null) {
                for(Proposition p : li) {
                    if(p.getName().equals(e.getTitle())) {
                        for(Map.Entry<String, Integer> en : weights.entrySet()) {
                            if(en.getKey().toLowerCase().equals(p.getCategory())) {
                                en.setValue(en.getValue() + 1);
                            }
                        }
                    }
                }
            }
        }
        //System.out.println(weights.toString());
        Map<Integer, Map<String, Date>> last = getLastActivities();
        //System.out.println("Mapka:");
        for(Map.Entry<Integer, Map<String, Date>> en : last.entrySet()) {
            //System.out.println(en.getKey());
            for(Map.Entry<String, Date> en1 : en.getValue().entrySet()) {
                //System.out.println(en1.getKey() + " } " + en1.getValue().getTime());
            }
        }

        List<String> pros = new ArrayList<>();

        String one = "sport";
        String two = "games";
        String three = "music";

        int oneInitialC = 3;
        int twoInitialC = 2;
        int threeInitialC = 1;

        int oneLastC = 1;
        int twoLastC = 2;
        int threeLastC = 3;

        //Calculate weights sum
        //System.out.println("Calculating");
        for(Activity a : sortedActivities) {
            for(Map.Entry<String, Integer> en : weights.entrySet()) {
                if(en.getKey().equals(a.getName())) a.setAmount(en.getValue());
            }
        }
        //Calculate weights divide initial survey
        for(Activity a : sortedActivities) {
            if(a.getName().toLowerCase().equals(one)) a.setAmount(a.getAmount() / oneInitialC);
            if(a.getName().toLowerCase().equals(two)) a.setAmount(a.getAmount() / twoInitialC);
            if(a.getName().toLowerCase().equals(three)) a.setAmount(a.getAmount() / threeInitialC);
        }

        for(Activity a : sortedActivities) {
            //System.out.println(a.getName() + " | " + a.getAmount());
        }
        //Calculate weights multiplication date last click and name
        for(Activity a : sortedActivities) {
            for (Map.Entry<Integer, Map<String, Date>> en : last.entrySet()) {
                if(id != en.getKey()) break;
                //System.out.println(en.getKey());
                int c = 1;
                for (Map.Entry<String, Date> en1 : en.getValue().entrySet()) {
                    //System.out.println(en1.getKey() + " } " + en1.getValue().getTime());
                    if (en1.getKey().toLowerCase().equals(a.getName().toLowerCase())) {
                        double k = en1.getValue().getTime()/100000000;
                        a.setAmount(a.getAmount()*Integer.valueOf((int) k)*c);
                    }
                    c++;
                }
            }
        }

        sortedActivities = sortedActivities.stream()
                .sorted(Comparator.comparing(Activity::getAmount).reversed())
                .collect(Collectors.toList());

        //System.out.println("Koncowe wagi:");
        //sortedActivities.forEach(System.out::println);

        String mostCommon = sortedActivities.get(0).getName();
        sortedActivities.remove(0);
        //System.out.println(mostCommon);
        List<Proposition> sortedByAlgorithm = new ArrayList<>();
        for(Proposition p : li) {
            if(p.getCategory().equals(one)) sortedByAlgorithm.add(p);
        }
        for(Proposition p : li) {
            if(p.getCategory().equals(two)) sortedByAlgorithm.add(p);
        }
        for(Proposition p : li) {
            if(p.getCategory().equals(three)) sortedByAlgorithm.add(p);
        }
        sortedByAlgorithm.add(new Proposition("SPECIAL FOR YOU", new Date()));

        for(Proposition p : li) {
            if(p.getCategory().equals(mostCommon.toLowerCase())) sortedByAlgorithm.add(p);
        }
        for(Proposition p : li) {
            if(!p.getCategory().equals(mostCommon.toLowerCase())) sortedByAlgorithm.add(p);
        }

        return sortedByAlgorithm;
    }


    @CrossOrigin
    @GetMapping("/proposition/{name}")
    public List<Proposition> getPropositionsByCategory(@PathVariable String name) {
        List<Proposition> li = propositionRepository.findAll();
        List<Proposition> pros = new ArrayList<>();
        for(Proposition p : li) {
            if(p.getCategory().equals(name)) pros.add(p);
        }
        return pros;
    }

    @CrossOrigin
    @GetMapping(path = "/proposition/update/{name}")
    public List<Proposition> getPropositionsFromWeb(@PathVariable String name) {

        PropositionUrl propositionUrl = null;
        if(name.equals("cook")) propositionUrl = new PropositionUrl("https://www.eventbrite.com/d/online/" + name + "/", 4, "cooking");
        else if(name.equals("sport")) propositionUrl = new PropositionUrl("https://www.eventbrite.com/d/online/" + name + "/", 1, "sport");
        else if(name.equals("music")) propositionUrl = new PropositionUrl("https://www.eventbrite.com/d/online/" + name + "/", 5, "music");
        else if(name.equals("movies")) propositionUrl = new PropositionUrl("https://www.eventbrite.com/d/online/" + name + "/", 3, "cooking");
        else if(name.equals("games")) propositionUrl = new PropositionUrl("https://www.eventbrite.com/d/online/" + name + "/", 6, "games");
        else if(name.equals("meeting")) propositionUrl = new PropositionUrl("https://www.eventbrite.com/d/online/" + name + "/", 2, "meeting");

        List<Proposition> pr = propositionUrl.getLi();
//        for(Proposition p : pr) {
//            System.out.println(p.getName() + " | " +  p.getStartdate());
//        }
        Proposition test = pr.get(0);

        List<Proposition> databaseProposition = propositionRepository.findAll();

        boolean status = false;
        for(Proposition px1 : pr) {
            for (Proposition px : databaseProposition) {
                if (px.getName().equals(px1.getName())) status = true;
            }
            if (!status) propositionRepository.save(px1);
            status = false;
        }

        return propositionRepository.findAll();
    }

}
