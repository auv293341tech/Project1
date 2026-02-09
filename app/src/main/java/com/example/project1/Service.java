package com.example.project1;

import java.io.Serializable;

public class Service implements Serializable {

    private String serviceId;
    private String title;
    private String description;

    // 🔥 REQUIRED for Firestore deserialization
    public Service() {
    }

    public Service(String serviceId, String title, String description) {
        this.serviceId = serviceId;
        this.title = title;
        this.description = description;
    }

    public String getServiceId() {
        return serviceId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
