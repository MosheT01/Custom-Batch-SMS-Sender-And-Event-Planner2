package com.example.custombatchsmssenderandeventplanner.event;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Event {

    private String id;
    private String name;
    private String message;
    private String location;
    private Date date;
    private ArrayList<Contact> contacts;

    public Event(String id, String name, String location, Date date, ArrayList<Contact> contacts, String message) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.contacts = contacts;
        this.message = message;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public Contact[] contacts() {
        return (Contact[])contacts.toArray();
    }

    public boolean addContact(String name, String phone_number) {
        return this.contacts.add(new Contact(name, phone_number));
    }

    public Contact removeContact(int index) {
        return this.contacts.remove(index);
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setContacts(ArrayList<Contact> contacts) {
        this.contacts = contacts;
    }

    public HashMap toHashMap() {
        HashMap _event = new HashMap<>();
        _event.put("name", this.name);
        _event.put("message", this.message);
        _event.put("date", this.date);
        _event.put("location", this.location);
        return _event;
    }
}
