package com.example.custombatchsmssenderandeventplanner.ui.events;

public class EventListItem {
    private String primaryText;
    private String secondaryText;

    public EventListItem(String primaryText, String secondaryText) {
        this.primaryText = primaryText;
        this.secondaryText = secondaryText;
    }

    public String getPrimaryText() {
        return primaryText;
    }

    public String getSecondaryText() {
        return secondaryText;
    }
}
