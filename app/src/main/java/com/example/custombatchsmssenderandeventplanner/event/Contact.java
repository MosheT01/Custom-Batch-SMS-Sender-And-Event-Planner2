package com.example.custombatchsmssenderandeventplanner.event;

public class Contact {
    private String _name;
    private String _phone_number;

    Contact(String name, String phone_number) {
        this._name = name;
        this._phone_number = phone_number;
    }

    public String name() {
        return this._name;
    }

    public String phone_number() {
        return this._phone_number;
    }
}
