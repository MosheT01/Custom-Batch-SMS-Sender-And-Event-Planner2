package com.example.custombatchsmssenderandeventplanner.ui.home;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.custombatchsmssenderandeventplanner.databinding.MessageFragmentBinding;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import android.telephony.SmsManager;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;

private void checkForSmsPermission() {
    if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.SEND_SMS}, MY_PERMISSIONS_REQUEST_SEND_SMS);
    } else {
        // Permission already granted. You can send SMS here.
        sendSMS("+972503006771", "Hello, this is a test message!");
    }
}

@Override
public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (requestCode == MY_PERMISSIONS_REQUEST_SEND_SMS) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission granted. You can send SMS here.
            sendSMS("+972503006771", "Hello, this is a test message2!");
        } else {
            // Permission denied.
        }
    }
}

private void sendSMS(String phoneNumber, String message) {
    SmsManager smsManager = SmsManager.getDefault();
    smsManager.sendTextMessage(phoneNumber, null, message, null, null);
}

public class MessageFragment extends Fragment {

    private MessageFragmentBinding binding;
    private TextView textViewDate;
    private Button buttonDatePicker;

    private void showDatePickerDialog() {
        // Get the current date
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and show it
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                        // Month is 0-based in DatePickerDialog
                        String formattedM = String.format("%02d", selectedMonth+1);
                        String formattedD = String.format("%02d", selectedDay);

                        String selectedDate = formattedD + "/" + formattedM + "/" + selectedYear;
                        binding.textDate.setText("Date: " + selectedDate);
                    }
                },
                year, month, day);
        datePickerDialog.show();
    }

    private void showTimePickerDialog() {
        // Get the current date
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);

        // Create a new instance of DatePickerDialog and show it
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                getContext(),
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                        String formattedH = String.format("%02d", selectedHour);
                        String formattedM = String.format("%02d", selectedMinute);


                        String selectedDate = formattedH + ":" + formattedM;
                        binding.textTime.setText("Time: " + selectedDate);
                    }
                },
                hour, minute, true);
        timePickerDialog.show();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MessageViewModel messageViewModel =
                new ViewModelProvider(this).get(MessageViewModel.class);

        binding = MessageFragmentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.buttonDate.setOnClickListener(v -> showDatePickerDialog());
        binding.buttonTime.setOnClickListener(v -> showTimePickerDialog());

        binding.contactsList.setLayoutManager(new LinearLayoutManager(getContext()));

        List<ContactListItem> data = Arrays.asList(
                new ContactListItem("Yoav Karpassi", "+972505716749"),
                new ContactListItem("Yoav Karpassi 2", "+972505716749"),
        new ContactListItem("Yoav Karpassi 3", "+972505716749")

        );
        ContactsAdapter adapter = new ContactsAdapter(data);
        binding.contactsList.setAdapter(adapter);


//        final TextView textView = binding.textMsg;
//        messageViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}