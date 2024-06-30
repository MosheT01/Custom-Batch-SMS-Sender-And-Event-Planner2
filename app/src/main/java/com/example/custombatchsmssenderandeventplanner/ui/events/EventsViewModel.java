package com.example.custombatchsmssenderandeventplanner.ui.events;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


public class EventsViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public EventsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Hello {contact_name},\nYou have been invited to {event_name}\nDate: {event_date}\nTime:{event_time}\nLocation:{event_location}");
    }

    public LiveData<String> getText() {
        return mText;
    }
}