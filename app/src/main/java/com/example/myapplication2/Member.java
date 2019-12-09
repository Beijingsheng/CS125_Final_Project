package com.example.myapplication2;

public class Member {
    private String name;
    private String id;
    private String position;
    private String team;
    private String email;

    public Member(String n, String i, String p, String t, String e) {
        this.name = n;
        this.id = i;
        this.position = p;
        this.team = t;
        this.email = e;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getPosition() {
        return position;
    }

    public String getTeam() {
        return team;
    }

    public String getEmail() {
        return email;
    }
}
