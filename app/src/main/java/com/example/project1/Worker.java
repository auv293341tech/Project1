package com.example.project1;

public class Worker {

    private String uid;
    private String name;
    private String skill;
    private String profileImageUrl;

    // Location
    private Double latitude;
    private Double longitude;

    // UI-only field (not stored in Firestore)
    private double distance;

    // Optional (future use)
    private Double rating;

    // 🔥 REQUIRED empty constructor for Firestore
    public Worker() {}

    // ================= GETTERS =================

    public String getUid() {
        return uid;
    }

    public String getName() {
        return name != null ? name : "Unknown";
    }

    public String getSkill() {
        return skill != null ? skill : "";
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public double getDistance() {
        return distance;
    }

    public Double getRating() {
        return rating;
    }

    // ================= SETTERS =================

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}