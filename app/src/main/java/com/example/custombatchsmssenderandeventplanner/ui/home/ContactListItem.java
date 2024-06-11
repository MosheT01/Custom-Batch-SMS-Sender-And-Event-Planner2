package com.example.custombatchsmssenderandeventplanner.ui.home;

public class ContactListItem {
    private String primaryText;
    private String secondaryText;

    public ContactListItem(String primaryText, String secondaryText) {
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
