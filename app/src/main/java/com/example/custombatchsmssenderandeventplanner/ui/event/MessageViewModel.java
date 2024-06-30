package com.example.custombatchsmssenderandeventplanner.ui.event;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;



public class MessageViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public MessageViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Hello {contact_name},\nYou have been invited to {event_name}\nDate: {event_date}\nTime:{event_time}\nLocation:{event_location}");
    }

    public LiveData<String> getText() {
        return mText;
    }
}