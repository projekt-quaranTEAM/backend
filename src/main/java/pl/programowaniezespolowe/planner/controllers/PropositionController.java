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
import pl.programowaniezespolowe.planner.user.UserLastActivities;
import pl.programowaniezespolowe.planner.user.UserLastDateActivity;
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
            mapedEvents.add(new PropositionDto(new CalendarEventDto(proposition.getId(),proposition.getName(),Instant.ofEpochMilli(proposition.getStartdate().getTime()),Instant.ofEpochMilli(proposition.getStartdate().getTime())),proposition.getLink(),proposition.getCategory(),proposition.getId(),Integer.valueOf(userid)));
        }

        return mapedEvents;
    }

    //Algorithm
    public List<Proposition> getThreeCategoriesAlgorithm(int userid) {
        List<Proposition> li = propositionRepository.findAll();
        List<Activity> ac1 = activityRepository.findAll();
        List<Activity> ac2 = new ArrayList<>();

        System.out.println("Start activity last:");
        EventController.lastUsersActivities.forEach(System.out::println);

        System.out.println("Start activity date:");
        EventController.userLastDateActivities.forEach(System.out::println);

        //Get user activities count (2)
        for(Activity a : ac1) {
            if(a.getUserid() == Integer.valueOf(userid)) {
                ac2.add(a);
            }
        }
        List<Activity> ac = ac2;


        //Get all eveents added count (1)
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

        //Get date last event
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

        String one = "meeting";
        String two = "games";
        String three = "music";


        //Calculate weights sum
        //System.out.println("Calculating");
        for(Activity a : sortedActivities) {
            for(Map.Entry<String, Integer> en : weights.entrySet()) {
                if(en.getKey().equals(a.getName())) a.setAmount(en.getValue());
            }
        }

        System.out.println("Wagi startowe");
        for(Map.Entry<String, Integer> en : weights.entrySet()) {
            System.out.println(en.getKey() + " : " + en.getValue());
        }

//        Waga = ((1 + 2) / 3) * (4 / 5)
//
//        Edytuj - Usuń
//        Magdalena Karpacka 16 gru 2020 o 18:13
//                -ile uzytkownik ma wydarzen danej kategorii w kalendarzu
//                -licznik kliknięć
//                -trzy ostatnie kliknięcia kolejno 1,2,3
//                -data ostatniego klikniecia danej kategorii

        for(Activity a : sortedActivities) {
            //System.out.println(a.getName() + " | " + a.getAmount());
        }


        for(UserLastDateActivity us : EventController.userLastDateActivities) {
            for(Activity a : sortedActivities) {
                for(Map.Entry<String, Integer> en : weights.entrySet()) {
                    if(en.getKey().equals(a.getName()) && a.getName().equals(us.name)) {
                        en.setValue(en.getValue() + (int)(us.lastActivityDate.getTime()/100000000) /1000);
                    }
                }
            }
        }

        System.out.println("Wagi po dacie:");
        for(Map.Entry<String, Integer> en : weights.entrySet()) {
            System.out.println(en.getKey() + " : " + en.getValue());
        }

        for(UserLastActivities us1 : EventController.lastUsersActivities) {
            for(Activity a : sortedActivities) {
                for(Map.Entry<String, Integer> en : weights.entrySet()) {
                    if(en.getKey().equals(a.getName()) && us1.userId == Integer.valueOf(userid)) {
                        for(int i = 0; i < us1.activities.size(); i++) {
                            if(en.getValue().equals(us1.activities.get(i))) en.setValue(en.getValue() + i);
                            if(i >= 3) break;
                        }
                    }
                }
            }
        }

        System.out.println("Wagi po ostatnich aktywnosciach:");
        for(Map.Entry<String, Integer> en : weights.entrySet()) {
            System.out.println(en.getKey() + " : " + en.getValue());
        }

        //Przepisanie koncowych wag
        for(Map.Entry<String, Integer> en : weights.entrySet()) {
            for(Activity a : sortedActivities) {
                if(a.getName().equals(en.getKey())) {
                    a.setAmount(en.getValue());
                }
            }
        }

        System.out.println("Koncowe wagi posortowane:");
        sortedActivities = sortedActivities.stream()
                .sorted(Comparator.comparing(Activity::getAmount).reversed())
                .collect(Collectors.toList());

        sortedActivities.forEach(System.out::println);

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
    @GetMapping(path = "/proposition/update")
    public List<Proposition> getPropositionsFromWeb() {

        List<String> categories = new ArrayList<>();
        categories.add("sport");
        categories.add("meeting");
        categories.add("movies");
        categories.add("cooking");
        categories.add("music");
        categories.add("games");
        categories.add("books");
        categories.add("art");
        categories.add("technology");
        categories.add("housework");
        categories.add("community");
        categories.add("business");
        categories.add("health");
        categories.add("science");
        categories.add("travel");
        categories.add("charity");
        categories.add("spirituality");
        categories.add("family");
        categories.add("education");
        categories.add("holiday");
        categories.add("fashion");
        categories.add("auto");

        //List<String> li = Arrays.asList("Sport" ,"Meeting","Movies","Cooking","Music","Games","Books","Art","Technology","Housework","Community","Business","Health","Science","Travel","Charity","Spirituality","Family","Education","Holiday","Fashion", "Auto");



        PropositionUrl propositionUrl = null;
        for(int i = 0; i < categories.size(); i++) {
            try {

                propositionUrl = new PropositionUrl("https://www.eventbrite.com/d/online/" + categories.get(i) + "/", i + 1, categories.get(i));

                List<Proposition> pr = propositionUrl.getLi();
                for (Proposition p : pr) {
                    //System.out.println(p.getName() + " | " + p.getStartdate());
                }
                //Proposition test = pr.get(0);

                List<Proposition> databaseProposition = propositionRepository.findAll();

                boolean status = false;
                for (Proposition px1 : pr) {
                    for (Proposition px : databaseProposition) {
                        if (px.getName().equals(px1.getName())) status = true;
                    }
                    if (!status) propositionRepository.save(px1);
                    status = false;
                }
            }
            catch (Exception e) {
                System.out.println("Nie ma takiej kategorii: " + categories.get(i));
            }

        }


//        if(name.equals("cooking")) propositionUrl = new PropositionUrl("https://www.eventbrite.com/d/online/" + name + "/", 4, "cooking");
//        else if(name.equals("sport")) propositionUrl = new PropositionUrl("https://www.eventbrite.com/d/online/" + name + "/", 1, "sport");
//        else if(name.equals("music")) propositionUrl = new PropositionUrl("https://www.eventbrite.com/d/online/" + name + "/", 5, "music");
//        else if(name.equals("movies")) propositionUrl = new PropositionUrl("https://www.eventbrite.com/d/online/" + name + "/", 3, "movies");
//        else if(name.equals("games")) propositionUrl = new PropositionUrl("https://www.eventbrite.com/d/online/" + name + "/", 6, "games");
//        else if(name.equals("meeting")) propositionUrl = new PropositionUrl("https://www.eventbrite.com/d/online/" + name + "/", 2, "meeting");
//        else if(name.equals("books")) propositionUrl = new PropositionUrl("https://www.eventbrite.com/d/online/" + name + "/", 7, "books");
//        else if(name.equals("art")) propositionUrl = new PropositionUrl("https://www.eventbrite.com/d/online/" + name + "/", 8, "art");
//        else if(name.equals("technology")) propositionUrl = new PropositionUrl("https://www.eventbrite.com/d/online/" + name + "/", 9, "technology");
//        else if(name.equals("housework")) propositionUrl = new PropositionUrl("https://www.eventbrite.com/d/online/" + name + "/", 10, "housework");
//        else if(name.equals("community")) propositionUrl = new PropositionUrl("https://www.eventbrite.com/d/online/" + name + "/", 11, "community");
//        else if(name.equals("business")) propositionUrl = new PropositionUrl("https://www.eventbrite.com/d/online/" + name + "/", 12, "business");
//        else if(name.equals("health")) propositionUrl = new PropositionUrl("https://www.eventbrite.com/d/online/" + name + "/", 13, "health");
//        else if(name.equals("science")) propositionUrl = new PropositionUrl("https://www.eventbrite.com/d/online/" + name + "/", 14, "science");
//        else if(name.equals("travel")) propositionUrl = new PropositionUrl("https://www.eventbrite.com/d/online/" + name + "/", 15, "travel");
//        else if(name.equals("charity")) propositionUrl = new PropositionUrl("https://www.eventbrite.com/d/online/" + name + "/", 16, "charity");
//        else if(name.equals("spirituality")) propositionUrl = new PropositionUrl("https://www.eventbrite.com/d/online/" + name + "/", 17, "spirituality");
//        else if(name.equals("family")) propositionUrl = new PropositionUrl("https://www.eventbrite.com/d/online/" + name + "/", 18, "family");
//        else if(name.equals("education")) propositionUrl = new PropositionUrl("https://www.eventbrite.com/d/online/" + name + "/", 19, "education");
//        else if(name.equals("holiday")) propositionUrl = new PropositionUrl("https://www.eventbrite.com/d/online/" + name + "/", 20, "holiday");
//        else if(name.equals("fashion")) propositionUrl = new PropositionUrl("https://www.eventbrite.com/d/online/" + name + "/", 21, "fashion");
//        else if(name.equals("auto")) propositionUrl = new PropositionUrl("https://www.eventbrite.com/d/online/" + name + "/", 22, "auto");






        return propositionRepository.findAll();
    }

}
