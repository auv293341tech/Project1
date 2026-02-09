package com.example.project1;

public class Posting {
    private String jobTitle;
    private String jobStatus;
    private String workerName;

    public Posting() {
        // Empty constructor required for Firestore
    }

    public Posting(String jobTitle, String jobStatus, String workerName) {
        this.jobTitle = jobTitle;
        this.jobStatus = jobStatus;
        this.workerName = workerName;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public String getJobStatus() {
        return jobStatus;
    }

    public String getWorkerName() {
        return workerName;
    }
}
