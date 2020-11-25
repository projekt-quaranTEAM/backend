package pl.programowaniezespolowe.planner.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.programowaniezespolowe.planner.activity.ActivityRepository;
import pl.programowaniezespolowe.planner.proposition.Proposition;
import pl.programowaniezespolowe.planner.proposition.PropositionRepository;
import pl.programowaniezespolowe.planner.proposition.PropositionUrl;

import java.util.ArrayList;
import java.util.List;

@RestController
public class PropositionController {

    @Autowired
    PropositionRepository propositionRepository;

    @GetMapping("/proposition")
    public List<Proposition> getPropositions() {
        return propositionRepository.findAll();
    }

    @GetMapping("/proposition/{name}")
    public List<Proposition> getPropositionsByCategory(@PathVariable String name) {
        List<Proposition> li = propositionRepository.findAll();
        List<Proposition> pros = new ArrayList<>();
        for(Proposition p : li) {
            if(p.getCategory().equals(name)) pros.add(p);
        }
        return pros;
    }

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
