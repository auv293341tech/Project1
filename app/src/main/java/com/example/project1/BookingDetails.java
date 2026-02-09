package com.example.project1;

public class BookingDetails extends Booking {
    private String jobTitle;
    private String userName;

    public BookingDetails() {
        super();
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
