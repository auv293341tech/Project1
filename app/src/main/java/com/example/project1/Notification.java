package com.example.project1;

public class Notification {
    private String title;
    private String body;
    private String clickAction;
    private String jobId;
    private long timestamp;

    public Notification() {}

    public Notification(String title, String body, String clickAction, String jobId, long timestamp) {
        this.title = title;
        this.body = body;
        this.clickAction = clickAction;
        this.jobId = jobId;
        this.timestamp = timestamp;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getClickAction() {
        return clickAction;
    }

    public void setClickAction(String clickAction) {
        this.clickAction = clickAction;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
