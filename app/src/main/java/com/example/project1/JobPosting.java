package com.example.project1;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.HashMap;

@IgnoreExtraProperties
public class JobPosting implements Serializable {

    // 🔹 Firestore document ID
    @DocumentId
    private String documentId;

    // 🔹 Service details
    private String serviceTitle;        // e.g. "Ceiling Fan Repair"
    private String serviceCategory;     // e.g. "Electrician"

    // 🔹 Customer details
    private String customerId;
    private String customerName;
    private String locationName;

    // 🔹 Location
    private GeoPoint location;

    // 🔹 Schedule & status
    private Long scheduledTime;
    private String status; // open, assigned, completed, cancelled

    // 🔹 Assignment
    private String assignedWorkerId;
    private Long assignedAt;

    // 🔹 Questionnaire answers
    private HashMap<String, Object> answers;

    // 🔹 Metadata
    private Long createdAt;

    // 🔹 REQUIRED empty constructor (Firestore)
    public JobPosting() {}

    // ===================== GETTERS =====================

    public String getDocumentId() {
        return documentId;
    }

    public String getServiceTitle() {
        return serviceTitle;
    }

    public String getServiceCategory() {
        return serviceCategory;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getLocationName() {
        return locationName;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public Long getScheduledTime() {
        return scheduledTime;
    }

    public String getStatus() {
        return status;
    }

    public String getAssignedWorkerId() {
        return assignedWorkerId;
    }

    public Long getAssignedAt() {
        return assignedAt;
    }

    public HashMap<String, Object> getAnswers() {
        return answers;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    // ===================== SETTERS =====================

    // ✅ FIX THAT SOLVES YOUR ERROR
    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public void setServiceTitle(String serviceTitle) {
        this.serviceTitle = serviceTitle;
    }

    public void setServiceCategory(String serviceCategory) {
        this.serviceCategory = serviceCategory;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
