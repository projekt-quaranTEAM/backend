package pl.programowaniezespolowe.planner.user;

import javax.persistence.*;

@Entity
@Table(name = "user", schema = "public")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "surname")
    private String surname;

    @Column(name = "groupid")
    private int groupid;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "permission")
    private String permission;

    public User() {
    }

    public User(String name, String surname, int groupid, String email, String password, String permission) {
        this.name = name;
        this.surname = surname;
        this.groupid = groupid;
        this.email = email;
        this.password = password;
        this.permission = permission;
    }

    public User(int id, String name, String surname, int groupid, String email, String password, String permission) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.groupid = groupid;
        this.email = email;
        this.password = password;
        this.permission = permission;
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

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public int getGroupid() {
        return groupid;
    }

    public void setGroupid(int groupid) {
        this.groupid = groupid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }
}