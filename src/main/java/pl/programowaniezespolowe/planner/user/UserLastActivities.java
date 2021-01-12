package pl.programowaniezespolowe.planner.user;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class UserLastActivities {
    public int userId;
    public List<String> activities = new ArrayList<>();

    @Override
    public String toString() {
        return userId + " " + activities.toString();
    }
}
