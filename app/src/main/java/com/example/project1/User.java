package com.example.project1;

public class User {
    private String name;
    private String skill;

    // No-argument constructor required for Firestore
    public User() {}

    public User(String name, String skill) {
        this.name = name;
        this.skill = skill;
    }

    public String getName() {
        return name;
    }

    public String getSkill() {
        return skill;
    }
}
