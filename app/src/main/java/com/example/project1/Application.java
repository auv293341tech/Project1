package com.example.project1;

import java.io.Serializable;

public class Application implements Serializable {

    private String documentId;

    // Core references
    private String jobId;
    private String workerId;
    private String recruiterId;

    // 🔹 Job snapshot (worker-side UI)
    private String serviceTitle;
    private String locationName;
    private String customerName;
    private Long scheduledTime;
    private String jobStatus;

    // 🔹 Worker snapshot (customer-side UI)
    private String workerName;
    private String workerSkill;

    // Application status
    private String status; // applied, accepted, rejected
    private boolean statusSeenByWorker;
    private Long appliedAt;

    // 🔹 Required empty constructor
    public Application() {}

    // ================= GETTERS =================

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getJobId() {
        return jobId;
    }

    public String getWorkerId() {
        return workerId;
    }

    public String getRecruiterId() {
        return recruiterId;
    }

    // --- Job snapshot ---
    public String getServiceTitle() {
        return serviceTitle;
    }

    public String getLocationName() {
        return locationName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public Long getScheduledTime() {
        return scheduledTime;
    }

    public String getJobStatus() {
        return jobStatus;
    }

    // --- Worker snapshot ---
    public String getWorkerName() {
        return workerName;
    }

    public String getWorkerSkill() {
        return workerSkill;
    }

    // --- Application ---
    public String getStatus() {
        return status;
    }

    public boolean isStatusSeenByWorker() {
        return statusSeenByWorker;
    }

    public Long getAppliedAt() {
        return appliedAt;
    }

    // ================= SETTERS =================

    // Job snapshot setters
    public void setServiceTitle(String serviceTitle) {
        this.serviceTitle = serviceTitle;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setScheduledTime(Long scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public void setJobStatus(String jobStatus) {
        this.jobStatus = jobStatus;
    }

    // Worker snapshot setters
    public void setWorkerName(String workerName) {
        this.workerName = workerName;
    }

    public void setWorkerSkill(String workerSkill) {
        this.workerSkill = workerSkill;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public void setWorkerId(String workerId) {
        this.workerId = workerId;
    }

    public void setRecruiterId(String recruiterId) {
        this.recruiterId = recruiterId;
    }

    public void setAppliedAt(Long appliedAt) {
        this.appliedAt = appliedAt;
    }

    public void setStatusSeenByWorker(boolean seen) {
        this.statusSeenByWorker = seen;
    }


    // Application setters
    public void setStatus(String status) {
        this.status = status;
    }
}
