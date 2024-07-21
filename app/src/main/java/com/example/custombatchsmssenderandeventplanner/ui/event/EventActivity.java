package com.example.custombatchsmssenderandeventplanner.ui.event;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Handler;
import android.os.Looper;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import com.example.custombatchsmssenderandeventplanner.MainActivity;
import com.example.custombatchsmssenderandeventplanner.R;
import com.example.custombatchsmssenderandeventplanner.event.Contact;
import com.example.custombatchsmssenderandeventplanner.event.Event;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

public class EventActivity extends AppCompatActivity {

    private static final int SMS_PERMISSION_REQUEST_CODE = 100;
    private static final String TAG = "EventActivity";
    private static final String CHANNEL_ID = "MessagesSentChannel";

    private MessageViewModel mViewModel;
    private FirebaseFirestore db;
    private ContactsAdapter adapter;
    private ArrayList<Contact> contactsList;
    private String eventId;
    private Context context;
    private Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_fragment);
        this.context = this;

        createNotificationChannel();

        this.eventId = this.getIntent().getStringExtra("id");
        mViewModel = new ViewModelProvider(this).get(MessageViewModel.class);
        db = FirebaseFirestore.getInstance();
        contactsList = new ArrayList<Contact>();

        // Request SMS and Phone State permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.SEND_SMS,
                    Manifest.permission.READ_PHONE_STATE
            }, SMS_PERMISSION_REQUEST_CODE);
        }

        ((MaterialButton) findViewById(R.id.button_date)).setOnClickListener(v -> showDatePickerDialog());
        ((MaterialButton) findViewById(R.id.button_time)).setOnClickListener(v -> showTimePickerDialog());
        ((RecyclerView) findViewById(R.id.contacts_list)).setLayoutManager(new LinearLayoutManager(this));
        ((MaterialButton) findViewById(R.id.btn_open_contact_form)).setOnClickListener(v -> showCustomDialog());

        db.document("events/" + eventId).get()
                .addOnSuccessListener(document -> {
                    event = new Event(document.getId(), document.getString("name"), document.getString("location"), document.getDate("date"), (ArrayList) document.get("contacts"), document.getString("message"));
                    adapter = new ContactsAdapter(contactsList, event, this);
                    mViewModel.setEventId(new Event(document.getId(), document.getString("name"), "", document.getDate("date"), new ArrayList<>(), document.getString("message")));

                    Date date = document.getDate("date");
                    String formattedDate = convertDateToDDMMYYYY(date);
                    String formattedTime = convertDateToHHMM(date);

                    setupEventDetails(document, formattedDate, formattedTime);

                    ArrayList<HashMap<String, String>> contacts = (ArrayList<HashMap<String, String>>) document.get("contacts");
                    for (HashMap<String, String> item : contacts) {
                        contactsList.add(new Contact(item.get("name"), item.get("phone")));
                    }

                    ((RecyclerView) findViewById(R.id.contacts_list)).setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                });

        ((MaterialButton) findViewById(R.id.btn_save_event)).setOnClickListener(v -> saveEvent());
        ((MaterialButton) findViewById(R.id.btn_send)).setOnClickListener(v -> sendMessages());
    }

    private void setupEventDetails(DocumentSnapshot document, String formattedDate, String formattedTime) {
        TextInputLayout eventNameInput = findViewById(R.id.event_name);
        TextInputLayout eventLocationInput = findViewById(R.id.event_location);
        TextView dateTextView = findViewById(R.id.text_date);
        TextView timeTextView = findViewById(R.id.text_time);
        EditText messageEditText = findViewById(R.id.text_message);

        eventNameInput.getEditText().setText(document.getString("name"));
        eventNameInput.getEditText().addTextChangedListener(new SimpleTextWatcher(text -> event.setName(text)));

        eventLocationInput.getEditText().setText(document.getString("location"));
        eventLocationInput.getEditText().addTextChangedListener(new SimpleTextWatcher(text -> event.setLocation(text)));

        dateTextView.setText("Date: " + formattedDate);
        timeTextView.setText("Time: " + formattedTime);

        messageEditText.setText(document.getString("message"));
        messageEditText.addTextChangedListener(new SimpleTextWatcher(text -> event.setMessage(text)));
    }

    public String convertDateToDDMMYYYY(Date date) {
        return new SimpleDateFormat("dd/MM/yyyy").format(date);
    }

    public String convertDateToHHMM(Date date) {
        return new SimpleDateFormat("HH:mm").format(date);
    }

    private void showCustomDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.new_contact_form, null);
        builder.setView(dialogView);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        dialogView.findViewById(R.id.btn_add_contact).setOnClickListener(v -> {
            TextInputLayout nameInput = dialogView.findViewById(R.id.contact_name);
            TextInputLayout phoneInput = dialogView.findViewById(R.id.contact_phone);

            String contactName = nameInput.getEditText().getText().toString();
            String contactPhone = phoneInput.getEditText().getText().toString();

            if (contactName.isEmpty() || contactPhone.isEmpty() || !isValidPhoneNumber(contactPhone)) {
              //  Toast.makeText(context, "Name or phone cannot be empty or invalid", Toast.LENGTH_SHORT).show();
               // return;
            }

            HashMap<String, Object> contact = new HashMap<>();
            contact.put("name", contactName);
            contact.put("phone", contactPhone);

            addContactToDatabase(contact, alertDialog);
        });
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber.matches("[+]?[0-9-]+");
    }

    private void addContactToDatabase(HashMap<String, Object> contact, AlertDialog alertDialog) {
        db.document("events/" + eventId)
                .update("contacts", FieldValue.arrayUnion(contact))
                .addOnSuccessListener(unused -> {
                    Log.d(TAG, "Contact added successfully: " + contact);
                    contactsList.add(new Contact(contact.get("name").toString(), contact.get("phone").toString()));
                    adapter.notifyDataSetChanged();
                    Toast.makeText(context, "Contact added!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to add contact", e);
                    Toast.makeText(context, "Failed to add contact", Toast.LENGTH_SHORT).show();
                });

        alertDialog.dismiss();
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String selectedDate = String.format("%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear);
                    updateEventDate(selectedDate, getTimeText());
                    setDateText(selectedDate);
                },
                year, month, day);
        datePickerDialog.show();
    }

    private void showTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (timePicker, selectedHour, selectedMinute) -> {
                    String selectedTime = String.format("%02d:%02d", selectedHour, selectedMinute);
                    updateEventDate(getDateText(), selectedTime);
                    setTimeText(selectedTime);
                },
                hour, minute, true);
        timePickerDialog.show();
    }

    private void updateEventDate(String date, String time) {
        String dateTime = date + " " + time;
        event.setDate(convertToDate(dateTime));
    }

    private String getDateText() {
        return ((TextView) findViewById(R.id.text_date)).getText().toString().replace("Date: ", "");
    }

    private void setTimeText(String time) {
        ((TextView) findViewById(R.id.text_time)).setText("Time: " + time);
    }

    private void setDateText(String date) {
        ((TextView) findViewById(R.id.text_date)).setText("Date: " + date);
    }

    private String getTimeText() {
        return ((TextView) findViewById(R.id.text_time)).getText().toString().replace("Time: ", "");
    }

    public static Date convertToDate(String dateString) {
        try {
            return new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(dateString);
        } catch (ParseException e) {
            Log.e(TAG, "Date parsing error", e);
            return null;
        }
    }

    private void saveContactsToDatabase() {
        // Create a list of contacts in the format expected by Firestore
        ArrayList<HashMap<String, String>> contactsToSave = new ArrayList<>();
        for (Contact contact : contactsList) {
            HashMap<String, String> contactMap = new HashMap<>();
            contactMap.put("name", contact.getName());
            contactMap.put("phone", contact.getPhone());
            contactsToSave.add(contactMap);
        }

        // Update the 'contacts' field of the event document
        db.document("events/" + eventId)
                .update("contacts", contactsToSave)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Contacts saved successfully!");
                    Toast.makeText(context, "Contacts saved successfully!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error saving contacts", e);
                    Toast.makeText(context, "Failed to save contacts.", Toast.LENGTH_SHORT).show();
                });
    }

    private void saveEvent() {
        Log.d(TAG, "Saving event: " + event.toHashMap());

        db.document("events/" + eventId)
                .set(event.toHashMap())
                .addOnSuccessListener(aVoid -> Toast.makeText(context, "Event saved successfully!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error saving event", e);
                    Toast.makeText(context, "Failed to save event", Toast.LENGTH_SHORT).show();
                });
        saveContactsToDatabase();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SMS_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                sendMessages();
            } else {
                Toast.makeText(this, "SMS or Phone State permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void sendMessages() {
        ExecutorService executor = Executors.newFixedThreadPool(contactsList.size());
        Handler mainHandler = new Handler(Looper.getMainLooper());

        // Use a thread-safe list to collect results
        List<String> results = Collections.synchronizedList(new ArrayList<>());

        for (Contact contact : contactsList) {
            executor.submit(() -> {
                try {
                    if (isValidContact(contact) && arePlaceholdersValid(event.getMessage(), contact)) {
                        String message = formatMessage(event.getMessage(), contact.getName(), contact.getPhone(), event.getName(), event.getLocation(), event.getDate());
                        sendSMS(contact.getPhone(), message);
                        Log.d(TAG, "LINK_DEBUG: SMS sent to: " + contact.getPhone());
                        results.add("SMS sent to: " + contact.getPhone());
                    } else {
                        Log.e(TAG, "LINK_DEBUG: Invalid contact or message for: " + contact.getPhone());
                        results.add("SMS sending failed (invalid contact or message) to: " + contact.getPhone());
                    }
                } catch (Exception e) {
                    Log.e(TAG, "LINK_DEBUG: SMS sending failed to: " + contact.getPhone(), e);
                    results.add("SMS sending failed to: " + contact.getPhone());
                }
            });
        }

        executor.shutdown();
        while (!executor.isTerminated()) {
            // Wait for all tasks to finish
        }

        // Show Toast messages and send notification on the main thread after all tasks are done
        mainHandler.post(() -> {
            for (String result : results) {
                Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
            }
            Toast.makeText(context, "Messages sent!", Toast.LENGTH_SHORT).show();
            sendNotification(results);
        });
    }

    private boolean isValidContact(Contact contact) {
        if (contact.getName().isEmpty()) {
            return false;
        }
        if (!contact.getPhone().matches("[+]?[0-9-]+")) {
            return false;
        }
        return true;
    }

    private boolean arePlaceholdersValid(String messageTemplate, Contact contact) {
        return !(messageTemplate.contains("{name}") && contact.getName().isEmpty() ||
                messageTemplate.contains("{phone}") && contact.getPhone().isEmpty() ||
                messageTemplate.contains("{event name}") && event.getName().isEmpty() ||
                messageTemplate.contains("{location}") && event.getLocation().isEmpty() ||
                messageTemplate.contains("{date}") && event.getDate() == null ||
                messageTemplate.contains("{time}") && event.getDate() == null);
    }

    private void sendSMS(String phoneNumber, String message) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            if (message.length() > 160) {
                ArrayList<String> messageParts = smsManager.divideMessage(message);
                smsManager.sendMultipartTextMessage(phoneNumber, null, messageParts, null, null);
            } else {
                smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String formatMessage(String messageTemplate, String contactName, String contactPhone, String eventName, String eventLocation, Date eventDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String dateTime = dateFormat.format(eventDate);
        String googleCalendarLink = generateGoogleCalendarLink(eventName, eventLocation, eventDate);

        if (googleCalendarLink == null) {
            googleCalendarLink = "No link available";
            Log.d(TAG, "LINK_DEBUG: Google Calendar link is null, setting to default message");
        }

        String formattedMessage = messageTemplate
                .replace("{name}", contactName)
                .replace("{phone}", contactPhone)
                .replace("{event name}", eventName)
                .replace("{location}", eventLocation)
                .replace("{date}", dateTime.split(" ")[0])
                .replace("{time}", dateTime.split(" ")[1])
                .replace("{link}", googleCalendarLink);

        Log.d(TAG, "LINK_DEBUG: Formatted message: " + formattedMessage);
        return formattedMessage;
    }
    private String generateGoogleCalendarLink(String eventName, String eventLocation, Date eventDate) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'", Locale.getDefault());
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            String startDate = dateFormat.format(eventDate);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(eventDate);
            calendar.add(Calendar.HOUR_OF_DAY, 1);
            String endDate = dateFormat.format(calendar.getTime());

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

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Messages Sent";
            String description = "Notification when messages are sent";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void sendNotification(List<String> results) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        intent.putExtra("navigateToReport", true);
        intent.putStringArrayListExtra("reportResults", new ArrayList<>(results));

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        boolean allMessagesSentSuccessfully = results.stream().allMatch(result -> result.startsWith("SMS sent to"));

        String notificationTitle = "Messages Sent";
        String notificationContent = allMessagesSentSuccessfully ? "All messages sent successfully."
                : "One or more messages failed. See the report for details.";

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(notificationTitle)
                .setContentText(notificationContent)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(notificationContent))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            return;
        }
        notificationManager.notify(1, builder.build());
    }
}

class SimpleTextWatcher implements TextWatcher {

    private final OnTextChanged onTextChanged;

    public SimpleTextWatcher(OnTextChanged onTextChanged) {
        this.onTextChanged = onTextChanged;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // No action needed
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        onTextChanged.onTextChanged(s.toString());
    }

    @Override
    public void afterTextChanged(Editable s) {
        // No action needed
    }

    interface OnTextChanged {
        void onTextChanged(String text);
    }
}
