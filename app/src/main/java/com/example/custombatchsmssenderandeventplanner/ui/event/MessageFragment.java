package com.example.custombatchsmssenderandeventplanner.ui.event;

import static android.content.Intent.getIntent;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
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

import com.example.custombatchsmssenderandeventplanner.databinding.MessageFragmentBinding;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import android.telephony.SmsManager;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MessageFragment extends Fragment {

    private MessageFragmentBinding binding;
    private TextView textViewDate;
    private Button buttonDatePicker;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MessageViewModel messageViewModel =
                new ViewModelProvider(requireActivity()).get(MessageViewModel.class);


        binding = MessageFragmentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

//        binding.contactsList.setLayoutManager(new LinearLayoutManager(getContext()));

//        List<ContactListItem> data = Arrays.asList(
//                new ContactListItem("Yoav Karpassi", "+972505716749"),
//                new ContactListItem("Yoav Karpassi 2", "+972505716749"),
//        new ContactListItem("Yoav Karpassi 3", "+972505716749")
//
//        );
//        ContactsAdapter adapter = new ContactsAdapter(data);
//        binding.contactsList.setAdapter(adapter);


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