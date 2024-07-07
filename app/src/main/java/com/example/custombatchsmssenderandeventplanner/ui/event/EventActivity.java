package com.example.custombatchsmssenderandeventplanner.ui.event;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.custombatchsmssenderandeventplanner.R;
import com.example.custombatchsmssenderandeventplanner.event.Contact;
import com.example.custombatchsmssenderandeventplanner.event.Event;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class EventActivity extends AppCompatActivity {

    private MessageViewModel mViewModel;
    private FirebaseFirestore db;
    ContactsAdapter adapter;
    ArrayList<ContactListItem> contacts_list;
    String eventId;
    Context context;
    Event event;

    public String convertDateToDDMMYYYY(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        return formatter.format(date);
    }

    public String convertDateToHHMM(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        return formatter.format(date);
    }

    private void showCustomDialog() {
        // Create an AlertDialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Inflate the custom layout
        LayoutInflater inflater2 = this.getLayoutInflater();
        View dialogView = inflater2.inflate(R.layout.new_contact_form, null);
        builder.setView(dialogView);

        // Set up the dialog's button click event
        MaterialButton dialogButton = dialogView.findViewById(R.id.btn_add_contact);

        // Create and show the dialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextInputLayout name = dialogView.findViewById(R.id.contact_name);
                TextInputLayout phone = dialogView.findViewById(R.id.contact_phone);

                // Handle the button click event
                HashMap<String, Object> contact = new HashMap<>();
                contact.put("name", name.getEditText().getText().toString());
                contact.put("phone", phone.getEditText().getText().toString());


                db.document("events/" + eventId)
                        .update("contacts", FieldValue.arrayUnion(contact))
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                contacts_list.add(new ContactListItem(name.getEditText().getText().toString(), phone.getEditText().getText().toString()));
                                adapter.notifyDataSetChanged();
                                Toast.makeText(context, "Contact added!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, "Failed", Toast.LENGTH_SHORT);
                            }
                        });

//                TextInputEditText dialogInput = dialogView.findViewById(R.id.event_name);
//                    String inputText = dialogInput.getText().toString();
//                    Toast.makeText(getContext(), inputText, Toast.LENGTH_LONG);
//                    // Do something with the inputText
                // Close the dialog


                alertDialog.dismiss();
            }
        });
    }

    private void showDatePickerDialog() {
        // Get the current date
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and show it
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                        // Month is 0-based in DatePickerDialog
                        String formattedM = String.format("%02d", selectedMonth+1);
                        String formattedD = String.format("%02d", selectedDay);

                        String selectedDate = formattedD + "/" + formattedM + "/" + selectedYear;

                        event.setDate(convertToDate(selectedDate + " " + ((TextView)findViewById(R.id.text_time)).getText()));
                        ((TextView)findViewById(R.id.text_date)).setText("Date: " + selectedDate);
                    }
                },
                year, month, day);
        datePickerDialog.show();
    }

    public static Date convertToDate(String dateString) {
        // Define the date format
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy HH:mm");
        try {
            // Parse the date string to a Date object
            return formatter.parse(dateString);
        } catch (ParseException e) {
            // Handle the exception if parsing fails
            e.printStackTrace();
            return null;
        }
    }


    private void showTimePickerDialog() {
        // Get the current date
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);

        // Create a new instance of DatePickerDialog and show it
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String formattedH = String.format("%02d", selectedHour);
                        String formattedM = String.format("%02d", selectedMinute);

                        String selectedDate = formattedH + ":" + formattedM;
                        event.setDate(convertToDate(((TextView)findViewById(R.id.text_date)).getText() + " " + selectedDate));

                        ((TextView)findViewById(R.id.text_time)).setText("Time: " + selectedDate);
                    }
                },
                hour, minute, true);
        timePickerDialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_fragment);
        this.context = this;

        this.eventId = this.getIntent().getStringExtra("id");
        mViewModel = new ViewModelProvider(this).get(MessageViewModel.class);
        db = FirebaseFirestore.getInstance();
        contacts_list = new ArrayList<>();
        adapter = new ContactsAdapter(contacts_list);

        ((MaterialButton)findViewById(R.id.button_date)).setOnClickListener(v -> showDatePickerDialog());
        ((MaterialButton)findViewById(R.id.button_time)).setOnClickListener(v -> showTimePickerDialog());
        ((RecyclerView)findViewById(R.id.contacts_list)).setLayoutManager(new LinearLayoutManager(this));
        ((MaterialButton)findViewById(R.id.btn_open_contact_form)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCustomDialog();
            }
        });


        db.document("events/" + eventId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot document) {
                        event = new Event(document.getId(), document.getString("name"), document.getString("location"), document.getDate("date"), (ArrayList)document.get("contacts"), document.getString("message"));
                        mViewModel.setEventId(
                                new Event(document.getId(), document.getString("name"), "", document.getDate("date"), new ArrayList<>(), document.getString("message"))
                        );

                        Date dd = document.getDate("date");
                        String formattedDate = convertDateToDDMMYYYY(dd);
                        String formattedTime = convertDateToHHMM(dd);

                        ((TextInputLayout)findViewById(R.id.event_name)).getEditText().setText(document.getString("name"));
                        ((TextInputLayout)findViewById(R.id.event_name)).getEditText().addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                event.setName(charSequence.toString());
                            }

                            @Override
                            public void afterTextChanged(Editable editable) {

                            }
                        });
                        ((TextInputLayout)findViewById(R.id.event_location)).getEditText().setText(document.getString("location"));
                        ((TextInputLayout)findViewById(R.id.event_location)).getEditText().addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                event.setLocation(charSequence.toString());
                            }

                            @Override
                            public void afterTextChanged(Editable editable) {

                            }
                        });
                        ((TextView)findViewById(R.id.text_date)).setText("Date: " + formattedDate);
                        ((TextView)findViewById(R.id.text_time)).setText("Time:" + formattedTime);

                        ((EditText)findViewById(R.id.text_message)).setText(document.getString("message"));
                        ((EditText)findViewById(R.id.text_message)).addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                event.setMessage(charSequence.toString());
                            }

                            @Override
                            public void afterTextChanged(Editable editable) {

                            }
                        });
                        ArrayList<HashMap> arr =  (ArrayList)document.get("contacts");

                        arr.forEach(item -> {
                            contacts_list.add(new ContactListItem((String)item.get("name"), (String)item.get("phone")));
                        });

                        ((RecyclerView)findViewById(R.id.contacts_list)).setAdapter(adapter);
                        adapter.notifyDataSetChanged();

                    }
                });

        ((MaterialButton)findViewById(R.id.btn_save_event)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.document("events/" + eventId)
                        .update(event.toHashMap())
                        .addOnSuccessListener(new OnSuccessListener() {
                            @Override
                            public void onSuccess(Object o) {
                                Toast.makeText(context, "Saved!", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        ((MaterialButton)findViewById(R.id.btn_send)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.document("events/" + eventId)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot document) {
                                event = new Event(document.getId(), document.getString("name"), document.getString("location"), document.getDate("date"), (ArrayList)document.get("contacts"), document.getString("message"));

                                // send sms

                            }
                        });
            }
        });

    }
}