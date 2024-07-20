package com.example.custombatchsmssenderandeventplanner.ui.Report;

public class MessageDetails {
    private String contactName;
    private String phoneNumber;
    private String eventInfo;
    private boolean isSent;

    public MessageDetails(String contactName, String phoneNumber, String eventInfo, boolean isSent) {
        this.contactName = contactName;
        this.phoneNumber = phoneNumber;
        this.eventInfo = eventInfo;
        this.isSent = isSent;
    }

    public String getContactName() {
        return contactName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEventInfo() {
        return eventInfo;
    }

    public boolean isSent() {
        return isSent;
    }
}
