package com.example.yogaadmin.models;

import java.io.Serializable;

public class Course implements Serializable {
    private int id;
    private String dayOfWeek;
    private String time;
    private String price;
    private String type;
    private String description;
    private String capacity;
    private String duration;

    // Constructors
    public Course(String dayOfWeek, String time, String price, String type,
                  String description, String capacity, String duration) {
        this.dayOfWeek = dayOfWeek;
        this.time = time;
        this.price = price;
        this.type = type;
        this.description = description;
        this.capacity = capacity;
        this.duration = duration;
    }

    public Course(int id, String dayOfWeek, String time, String price, String type,
                  String description, String capacity, String duration) {
        this(dayOfWeek, time, price, type, description, capacity, duration);
        this.id = id;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(String dayOfWeek) { this.dayOfWeek = dayOfWeek; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCapacity() { return capacity; }
    public void setCapacity(String capacity) { this.capacity = capacity; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }

    @Override
    public String toString() {
        return "Day: " + dayOfWeek +
                "\nTime: " + time +
                "\nPrice: " + price +
                "\nType: " + type +
                "\nDescription: " + description +
                "\nCapacity: " + capacity +
                "\nDuration: " + duration;
    }
}