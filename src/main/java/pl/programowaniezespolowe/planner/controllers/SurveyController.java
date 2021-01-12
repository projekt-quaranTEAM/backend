package pl.programowaniezespolowe.planner.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.programowaniezespolowe.planner.activity.Activity;
import pl.programowaniezespolowe.planner.activity.ActivityRepository;
import pl.programowaniezespolowe.planner.user.User;
import pl.programowaniezespolowe.planner.user.UserRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
public class SurveyController {
    @Autowired
    ActivityRepository activityRepository;

    @Autowired
    UserRepository userRepository;

    List<String> li = Arrays.asList("Sport" ,"Meeting","Movies","Cooking","Music","Games","Books","Art","Technology",
                                    "Housework","Community","Business", "Health","Science","Travel","Charity",
                                    "Spirituality","Family","Education","Holiday","Fashion", "Auto");

    @PostMapping("/register/survey")
    public ResponseEntity<?> survey(@RequestBody Map<String, String> body) {
        Activity activity1 = new Activity();
        for(String activity : li) {
            if(body.get(activity) != null) {
               activity1.setName(activity);
               activity1.setAmount(Integer.parseInt(body.get(activity)));

                List<User> users = userRepository.findAll();
                for(User u : users) {
                    if(u.getEmail().equals(body.get("email"))) {
                        activity1.setUserid(u.getId());
                    }
                }


            }
            activityRepository.save(activity1);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

}
