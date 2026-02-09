package com.example.project1;

public class ServiceCategory {
    private String name;
    private int drawableId;

    public ServiceCategory(String name, int drawableId) {
        this.name = name;
        this.drawableId = drawableId;
    }

    public String getName() {
        return name;
    }

    public int getDrawableId() {
        return drawableId;
    }
}
