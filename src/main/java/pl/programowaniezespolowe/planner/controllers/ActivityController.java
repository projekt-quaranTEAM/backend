package pl.programowaniezespolowe.planner.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.programowaniezespolowe.planner.activity.Activity;
import pl.programowaniezespolowe.planner.activity.ActivityRepository;
import pl.programowaniezespolowe.planner.user.User;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.stream.Collectors;

@RestController
public class ActivityController {

    private Queue<String> lastActivities = new ArrayBlockingQueue<String>(3);

    @Autowired
    ActivityRepository activityRepository;

    //Get activities by recent usage
    @GetMapping(path = "/activity")
    public List<Activity> getAllActivities() {
        return getActivitiesByMostUsage();
    }

    //Increase counter after user add to planner
    @PutMapping(path = "/activityUpdate/{name}")
    public List<Activity> increaseAmount(@PathVariable String name) {

        List<Activity> activities = activityRepository.findAll();
        for(Activity a : activities) {
            if(a.getName().equals(name)) {

                Activity activity = a;

                if(lastActivities.contains(a.getName())) {
                    activity.setAmount(activity.getAmount() + 1);
                }
                else activity.setAmount(activity.getAmount() + 2);
                activityRepository.save(activity);

                if(lastActivities.size() == 0) {
                    lastActivities.add(a.getName());
                }
                else if(lastActivities.size() != 0) {
                    if(lastActivities.size() == 3) {
                        lastActivities.poll();
                        lastActivities.add(a.getName());
                    }
                    else lastActivities.add(a.getName());
                }
            }
        }

        activities = getActivitiesByMostUsage();

        return activities;
    }

    //Reset amount if categories boring
    @PutMapping(path = "/activityReset/{name}")
    public List<Activity> resetAmount(@PathVariable String name) {
        //String name = body.get("name");
        List<Activity> activities = activityRepository.findAll();
        for(Activity a : activities) {
            if(a.getName().equals(name)) {
                Activity activity = a;
                activity.setAmount(0);
                activityRepository.save(activity);
            }
        }
        return activityRepository.findAll();
    }

    //Search For more propositions
    public void getPropositionsFromWeb() {

        //For Events
        //https://www.eventbrite.com/

        //For culinary


        //For mo

    }


    //Algorithm
    public List<Activity> getActivitiesByMostUsage() {
        List<Activity> activities = activityRepository.findAll();
        activities = activities.stream().sorted(Comparator.comparing(Activity::getAmount).reversed()).collect(Collectors.toList());

        Random r = new Random();
        int addCategory = 0;
        while(true) {
            addCategory = r.nextInt(activities.size());
            if(addCategory != 0 && addCategory != 1 && addCategory != 2 && addCategory != 3) break;
        }

        Activity a = activities.get(addCategory);
        activities = activities.stream().sorted(Comparator.comparing(Activity::getAmount).reversed()).limit(4).collect(Collectors.toList());
        activities.add(a);
        return activities;
    }


}