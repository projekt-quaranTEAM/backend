package pl.programowaniezespolowe.planner.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
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

    int userID = 3;
    @Autowired
    ActivityRepository activityRepository;

    @Autowired
    UserRepository userRepository;

    List<String> li = Arrays.asList("Sport" ,"Meeting","Movies","Cooking","Music","Games","Books","Art","Technology",
                                    "Housework","Community","Business", "Health","Science","Travel","Charity",
                                    "Spirituality","Family","Education","Holiday","Fashion", "Auto");
    @CrossOrigin
    @PostMapping("/register/survey")
    public ResponseEntity<?> survey(@RequestBody Map<String, String> body) {


        List<User> users = userRepository.findAll();

        for(User u : users) {
            if(u.getEmail().equals(body.get("email"))) {
                this.userID = u.getId();
            }
        }

        for(String activity : li) {
            Activity activity1 = new Activity();
            if(body.get(activity.toLowerCase()) != null) {
                activity1.setName(activity);
                activity1.setAmount(Integer.parseInt(body.get(activity.toLowerCase())));
                activity1.setUserid(this.userID);
                activityRepository.save(activity1);
            }
        }
        
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
