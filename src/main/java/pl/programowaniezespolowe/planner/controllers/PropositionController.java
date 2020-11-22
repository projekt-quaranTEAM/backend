package pl.programowaniezespolowe.planner.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.programowaniezespolowe.planner.activity.ActivityRepository;
import pl.programowaniezespolowe.planner.proposition.Proposition;
import pl.programowaniezespolowe.planner.proposition.PropositionRepository;

import java.util.List;

@RestController
public class PropositionController {

    @Autowired
    PropositionRepository propositionRepository;

    @GetMapping("/proposition")
    public List<Proposition> getPropositions() {
        return propositionRepository.findAll();
    }

}
