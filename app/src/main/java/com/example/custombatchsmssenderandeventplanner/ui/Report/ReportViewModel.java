package com.example.custombatchsmssenderandeventplanner.ui.Report;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.List;

public class ReportViewModel extends ViewModel {

    private final MutableLiveData<List<MessageDetails>> messageDetails;

    public ReportViewModel() {
        messageDetails = new MutableLiveData<>();
    }

    public LiveData<List<MessageDetails>> getMessageDetails() {
        return messageDetails;
    }

    public void setMessageDetails(List<MessageDetails> details) {
        messageDetails.setValue(details);
    }
}
