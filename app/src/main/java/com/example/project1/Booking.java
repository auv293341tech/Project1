package com.example.project1;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;
import java.util.Date;

public class Booking implements Serializable {

    @DocumentId
    private String documentId;

    private String jobId;
    private String workerId;
    private String customerId;
    private String workerName;
    private String customerName;
    private String serviceTitle;
    private String locationName;
    private String status; // accepted, started, completed, cancelled
    private Long scheduledTime;


    @ServerTimestamp
    private Date createdAt;
    private Date startedAt;
    private Date completedAt;

    public Booking() {
        // Required for Firestore
    }

    public Booking(String jobId, String customerId, String workerId, String serviceTitle, String locationName, String customerName, Long scheduledTime, String status) {
        this.jobId = jobId;
        this.customerId = customerId;
        this.workerId = workerId;
        this.serviceTitle = serviceTitle;
        this.locationName = locationName;
        this.customerName = customerName;
        this.scheduledTime = scheduledTime;
        this.status = status;
    }

    // Getters
    public String getDocumentId() { return documentId; }
    public String getJobId() { return jobId; }
    public String getWorkerId() { return workerId; }
    public String getCustomerId() { return customerId; }
    public String getWorkerName() { return workerName; }
    public String getCustomerName() { return customerName; }
    public String getServiceTitle() { return serviceTitle; }
    public String getLocationName() { return locationName; }
    public String getStatus() { return status; }
    public Long getScheduledTime() { return scheduledTime; }
    public Date getCreatedAt() { return createdAt; }
    public Date getStartedAt() { return startedAt; }
    public Date getCompletedAt() { return completedAt; }

    // Setters
    public void setDocumentId(String documentId) { this.documentId = documentId; }
    public void setJobId(String jobId) { this.jobId = jobId; }
    public void setWorkerId(String workerId) { this.workerId = workerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public void setWorkerName(String workerName) { this.workerName = workerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public void setServiceTitle(String serviceTitle) { this.serviceTitle = serviceTitle; }
    public void setLocationName(String locationName) { this.locationName = locationName; }
    public void setStatus(String status) { this.status = status; }
    public void setScheduledTime(Long scheduledTime) { this.scheduledTime = scheduledTime; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public void setStartedAt(Date startedAt) { this.startedAt = startedAt; }
    public void setCompletedAt(Date completedAt) { this.completedAt = completedAt; }
}
