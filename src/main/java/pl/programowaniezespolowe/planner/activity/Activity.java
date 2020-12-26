package pl.programowaniezespolowe.planner.activity;


import pl.programowaniezespolowe.planner.proposition.Proposition;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "activity", schema = "public")
public class Activity {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "id")
        private int id;

        @Column(name = "name")
        private String name;

        @Column(name = "amount")
        private int amount;

        @Column(name = "userid")
        private int userid;

        @OneToMany
        @JoinTable(
            name="proposition", schema = "public",
            joinColumns = @JoinColumn(name="activityid"),
            inverseJoinColumns = @JoinColumn( name="id")
        )
        private List<Proposition> propositions = new ArrayList<>();

        public Activity() {}

    public Activity(int id, String name, int amount, int userid, List<Proposition> propositions) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.userid = userid;
        this.propositions = propositions;
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

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public List<Proposition> getPropositions() {
        return propositions;
    }

    public void setPropositions(List<Proposition> propositions) {
        this.propositions = propositions;
    }

    @Override
    public String toString() {
        return this.getId() + " | " + this.getAmount() + " | " + this.getName();
    }
}
