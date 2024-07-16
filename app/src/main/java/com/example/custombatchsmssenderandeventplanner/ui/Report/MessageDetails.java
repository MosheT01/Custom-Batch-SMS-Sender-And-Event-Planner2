package com.example.custombatchsmssenderandeventplanner.ui.Report;

public class MessageDetails {
    private String phoneNumber;
    private String message;
    private boolean isSuccess;

    public MessageDetails(String phoneNumber, String message, boolean isSuccess) {
        this.phoneNumber = phoneNumber;
        this.message = message;
        this.isSuccess = isSuccess;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSuccess() {
        return isSuccess;
    }
}
