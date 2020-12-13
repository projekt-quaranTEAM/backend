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
import pl.programowaniezespolowe.planner.proposition.Proposition;
import pl.programowaniezespolowe.planner.proposition.PropositionRepository;
import pl.programowaniezespolowe.planner.proposition.PropositionUrl;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class PropositionController {

    @Autowired
    PropositionRepository propositionRepository;

    @Autowired
    ActivityRepository activityRepository;

    @CrossOrigin
    @GetMapping("/proposition")
    public List<PropositionDto> getPropositions() {
        List<Proposition> propositions = getThreeCategoriesAlgorithm();
        ArrayList<PropositionDto> mapedEvents = new ArrayList<>();
        for (Proposition proposition : propositions) {
            if(proposition.getStartdate() != null)
            mapedEvents.add(new PropositionDto(new CalendarEventDto(proposition.getId(),proposition.getName(),Instant.ofEpochMilli(proposition.getStartdate().getTime()),Instant.ofEpochMilli(proposition.getStartdate().getTime())),proposition.getLink(),proposition.getCategory(),proposition.getId(),proposition.getUserid()));
        }

        return mapedEvents;
    }

    //@CrossOrigin
    //@GetMapping("/proposition2")
    public List<Proposition> getThreeCategoriesAlgorithm() {
        List<Proposition> li = propositionRepository.findAll();
        List<Activity> ac = activityRepository.findAll();

        List<Activity> sortedActivities = ac.stream()
                .sorted(Comparator.comparing(Activity::getAmount).reversed())
                .collect(Collectors.toList());

        List<String> pros = new ArrayList<>();

        String one = "sport";
        String two = "games";
        String three = "music";

        String mostCommon = sortedActivities.get(0).getName();
        sortedActivities.remove(0);
        System.out.println(mostCommon);
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
