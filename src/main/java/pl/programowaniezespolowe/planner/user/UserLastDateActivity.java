package pl.programowaniezespolowe.planner.user;

import java.util.Date;

public class UserLastDateActivity {
    public int userId;
    public Date lastActivityDate;
    public String name;

    @Override
    public String toString() {
        if(lastActivityDate != null) {
            return userId + " " + (int)(lastActivityDate.getTime() / 100000000) /1000 + " " + name;
        }
        return "";
    }
}
