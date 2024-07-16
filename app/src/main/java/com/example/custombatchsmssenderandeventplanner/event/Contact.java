package com.example.custombatchsmssenderandeventplanner.event;

import java.util.HashMap;
import java.util.Map;

public class Contact {
    private String name;
    private String phone;
    private Map<String, String> customFields;

    public Contact(String name, String phone) {
        this.name = name;
        this.phone = phone;
        this.customFields = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void addCustomField(String key, String value) {
        customFields.put(key, value);
    }

    public String getCustomField(String key) {
        return customFields.get(key);
    }

    public Map<String, String> getCustomFields() {
        return customFields;
    }
}
