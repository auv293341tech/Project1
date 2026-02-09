package com.example.project1;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Review {
    private String recruiterId;
    private float rating;
    private String text;
    private Date timestamp;

    public Review() {
        // Default constructor required for calls to DataSnapshot.getValue(Review.class)
    }

    public Review(String recruiterId, float rating, String text) {
        this.recruiterId = recruiterId;
        this.rating = rating;
        this.text = text;
    }

    public String getRecruiterId() {
        return recruiterId;
    }

    public float getRating() {
        return rating;
    }

    public String getText() {
        return text;
    }

    @ServerTimestamp
    public Date getTimestamp() {
        return timestamp;
    }
}
