package com.example.custombatchsmssenderandeventplanner.event;

import static android.content.ContentValues.TAG;

import android.net.Uri;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

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

    private String generateGoogleCalendarLink(String eventName, String eventLocation, Date eventDate) {
        try {
            // Set up the date format in UTC
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'", Locale.getDefault());
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            String startDate = dateFormat.format(eventDate);

            // Set the event duration (e.g., 1 hour)
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(eventDate);
            calendar.add(Calendar.HOUR_OF_DAY, 1); // Add 1 hour to the start time
            String endDate = dateFormat.format(calendar.getTime());

            // Generate the Google Calendar link
            String link = "https://calendar.google.com/calendar/render?action=TEMPLATE" +
                    "&text=" + Uri.encode(eventName) +
                    "&details=" + Uri.encode("Details about the event") +
                    "&location=" + Uri.encode(eventLocation) +
                    "&dates=" + startDate + "/" + endDate +
                    "&sf=true&output=xml";

            Log.d(TAG, "Generated Google Calendar link: " + link);
            return link;
        } catch (Exception e) {
            Log.e(TAG, "Error generating Google Calendar link", e);
            return null;
        }
    }

    public String formatMessage(Contact contact) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String dateTime = dateFormat.format(this.date);
        String googleCalendarLink = generateGoogleCalendarLink(this.name, this.location, this.date);

        if (googleCalendarLink == null) {
            googleCalendarLink = "No link available";
            Log.d(TAG, "LINK_DEBUG: Google Calendar link is null, setting to default message");
        }

        String formattedMessage = this.message
                .replace("{name}", contact.getName())
                .replace("{phone}", contact.getPhone())
                .replace("{event name}", this.name)
                .replace("{location}", this.location)
                .replace("{date}", dateTime.split(" ")[0])
                .replace("{time}", dateTime.split(" ")[1])
                .replace("{link}", googleCalendarLink);

        Log.d(TAG, "LINK_DEBUG: Formatted message: " + formattedMessage);
        return formattedMessage;
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
