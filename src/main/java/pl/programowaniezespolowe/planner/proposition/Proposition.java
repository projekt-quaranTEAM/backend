package pl.programowaniezespolowe.planner.proposition;

import javax.persistence.*;

@Entity
@Table(name = "proposition", schema = "public")
public class Proposition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "link")
    private String link;

    @Column(name = "userid")
    private int userid;

    @Column(name = "activityid")
    private int activityid;

    @Column(name = "category")
    private String category;

    public Proposition() {}

    public Proposition(int id, String name, String link, int userid, int activityid, String category) {
        this.id = id;
        this.name = name;
        this.link = link;
        this.userid = userid;
        this.activityid = activityid;
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public int getActivityid() {
        return activityid;
    }

    public void setActivityid(int activityid) {
        this.activityid = activityid;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
