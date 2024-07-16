package com.example.custombatchsmssenderandeventplanner.event;

import java.util.Date;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class Event {
    private String id;
    private String name;
    private String location;
    private Date date;
    private List<Contact> contacts;
    private String message;

    public Event(String id, String name, String location, Date date, List<Contact> contacts, String message) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.date = date;
        this.contacts = contacts;
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public Date getDate() {
        return date;
    }

    public List<Contact> getContacts() {
        return contacts;
    }

    public String getMessage() {
        return message;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, Object> toHashMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("location", location);
        map.put("date", date);
        map.put("contacts", contacts);
        map.put("message", message);
        return map;
    }
}
