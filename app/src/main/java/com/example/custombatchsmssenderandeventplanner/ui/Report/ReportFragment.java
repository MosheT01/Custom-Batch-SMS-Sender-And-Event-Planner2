package com.example.custombatchsmssenderandeventplanner.ui.Report;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.custombatchsmssenderandeventplanner.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;

public class ReportFragment extends Fragment {

    private static final String TAG = "ReportFragment";
    private FirebaseFirestore db;
    private LinearLayout containerLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report, container, false);
        containerLayout = view.findViewById(R.id.containerLayout);

        // Fetch all event data
        fetchAllEventData();

        return view;
    }

    private void fetchAllEventData() {
        db.collection("events").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        if (document.exists()) {
                            String eventInfo = document.getString("name");
                            Log.d(TAG, "Fetched event info: " + eventInfo);

                            // Check if eventInfo is null
                            if (eventInfo == null) {
                                Log.e(TAG, "Event info is missing in document: " + document.getId());
                                continue; // Skip this document
                            }

                            // Get contacts list
                            ArrayList<HashMap<String, String>> contacts = (ArrayList<HashMap<String, String>>) document.get("contacts");
                            Log.d(TAG, "Fetched contacts: " + contacts);

                            // Ensure contacts list is not null
                            if (contacts != null) {
                                for (HashMap<String, String> contact : contacts) {
                                    String contactName = contact.get("name");
                                    String phoneNumber = contact.get("phone");
                                    String messageSentString = contact.get("messageSent");
                                    Boolean messageSent = "true".equalsIgnoreCase(messageSentString);

                                    // Check if contactName and phoneNumber are not null
                                    if (contactName != null && phoneNumber != null) {
                                        addRowToTable(contactName, phoneNumber, eventInfo, messageSent);
                                    } else {
                                        // Handle missing contact data
                                        Log.e(TAG, "Missing contact data in document: " + document.getId());
                                    }
                                }
                            } else {
                                // Handle empty contacts list
                                Log.d(TAG, "No contacts found in document: " + document.getId());
                            }
                        } else {
                            // Handle document not found
                            Log.d(TAG, "Document not found.");
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle Firestore errors
                    Log.e(TAG, "Error fetching data: ", e);
                });
    }

    private void addRowToTable(String contactName, String phoneNumber, String eventInfo, Boolean messageSent) {
        // Inflate the LinearLayout layout
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(getContext())
                .inflate(R.layout.item_contact_row, null, false);

        // Bind data to the LinearLayout views
        TextView textViewContactName = linearLayout.findViewById(R.id.textViewContactName);
        TextView textViewPhoneNumber = linearLayout.findViewById(R.id.textViewPhoneNumber);
        TextView textViewEventInfo = linearLayout.findViewById(R.id.textViewEventInfo);
        TextView textViewStatus = linearLayout.findViewById(R.id.textViewStatus);
//        Button buttonResend = linearLayout.findViewById(R.id.buttonResend);

        // Set text to TextViews
        textViewContactName.setText(contactName);
        textViewPhoneNumber.setText(phoneNumber);
        textViewEventInfo.setText(eventInfo);

        // Set status or show resend button based on messageSent flag
        textViewStatus.setText("Sent successfully");
        textViewStatus.setVisibility(View.VISIBLE);
//        buttonResend.setVisibility(View.GONE);
//        if (messageSent) {
//            textViewStatus.setText("Sent successfully");
//            textViewStatus.setVisibility(View.VISIBLE);
//            buttonResend.setVisibility(View.GONE);
//        } else {
//            textViewStatus.setText("UNSent");
//            textViewStatus.setVisibility(View.VISIBLE);
//            buttonResend.setVisibility(View.VISIBLE);
//            buttonResend.setOnClickListener(v -> resendMessage(contactName, phoneNumber, eventInfo, textViewStatus, buttonResend));
//        }

        // Set layout parameters to add spacing
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        int rowMargin = getResources().getDimensionPixelSize(R.dimen.row_margin);
        int columnMargin = getResources().getDimensionPixelSize(R.dimen.column_margin);
        layoutParams.setMargins(columnMargin, rowMargin, columnMargin, rowMargin);
        linearLayout.setLayoutParams(layoutParams);

        // Add the LinearLayout to the container layout
        containerLayout.addView(linearLayout);
    }

    private void resendMessage(String contactName, String phoneNumber, String eventInfo, TextView textViewStatus, Button buttonResend) {
        // Logic to resend the message
        boolean success = contactName != null && phoneNumber != null;

        if (success) {
            textViewStatus.setText("Sent successfully");
            textViewStatus.setVisibility(View.VISIBLE);
            buttonResend.setVisibility(View.GONE);
            // Optionally update Firestore to indicate the message was sent successfully
            // db.collection("events").document(eventId).update("contacts", updatedContacts);
        } else {
            Toast.makeText(getContext(), "Failed to resend message", Toast.LENGTH_SHORT).show();
        }
    }
}





//boolean success;
//        if(contactName == NULL || phoneNumber == NULL){
//success = false;
//        }else{
//success = true;
//        }
//        // Set status or show resend button based on messageSent flag
//        if(success){
//        textViewStatus.setText("Sent successfully");
//            textViewStatus.setVisibility(View.VISIBLE);
//            buttonResend.setVisibility(View.GONE);
//        } else{
//                textViewStatus.setText("UNSent");
//            textViewStatus.setVisibility(View.VISIBLE);
//            buttonResend.setVisibility(View.GONE);
//        }