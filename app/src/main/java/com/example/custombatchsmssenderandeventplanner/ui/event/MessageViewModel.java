package com.example.custombatchsmssenderandeventplanner.ui.event;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.custombatchsmssenderandeventplanner.event.Event;

import java.util.Calendar;



public class MessageViewModel extends ViewModel {

    private final MutableLiveData<Event> mText;

    public MessageViewModel() {
        mText = new MutableLiveData<>();
    }

    public void setEventId(Event event) {
        mText.setValue(event);
    }

    public LiveData<Event> getEvent() {
        return mText;
    }
}