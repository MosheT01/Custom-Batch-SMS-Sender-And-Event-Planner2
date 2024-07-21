package com.example.custombatchsmssenderandeventplanner.ui.events;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.custombatchsmssenderandeventplanner.MainActivity;
import com.example.custombatchsmssenderandeventplanner.R;
import com.example.custombatchsmssenderandeventplanner.databinding.EventsFragmentBinding;
import com.example.custombatchsmssenderandeventplanner.databinding.MessageFragmentBinding;
import com.example.custombatchsmssenderandeventplanner.event.Contact;
import com.example.custombatchsmssenderandeventplanner.event.Event;
import com.example.custombatchsmssenderandeventplanner.ui.event.ContactListItem;
import com.example.custombatchsmssenderandeventplanner.ui.event.ContactsAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Array;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventsFragment extends Fragment {

    private EventsFragmentBinding binding;
    FirebaseFirestore db;
    EventsAdapter adapter;
    List<Event> items;
    private void showCustomDialog() {
        // Create an AlertDialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
        // Inflate the custom layout
        LayoutInflater inflater2 = this.getLayoutInflater();
        View dialogView = inflater2.inflate(R.layout.new_event_form, null);
        builder.setView(dialogView);

        // Set up the dialog's button click event
        MaterialButton dialogButton = dialogView.findViewById(R.id.btnSubmit);

        // Create and show the dialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextInputLayout name = dialogView.findViewById(R.id.event_name);
                // Handle the button click event
                Event ev = new Event("", name.getEditText().getText().toString(), "", new Date(), new ArrayList<>(), "hello {name},\n" +
                        "your phone number is {phone}\n" +
                        "you are invited to {event name},\n" +
                        "at this location {location}\n" +
                        "at this date {date}\n" +
                        "at this time {time}\n" +
                        "\n" +
                        "Add to calendar {link}\n" +
                        "\n" +
                        "see you there!");
                db.collection("events")
                        .add(ev.toHashMap())
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                documentReference.get()
                                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                        items.add(new Event(documentReference.getId(), documentSnapshot.getString("name"), documentSnapshot.getString("location"), documentSnapshot.getDate("date"), (ArrayList<Contact>)documentSnapshot.get("contacts"), documentSnapshot.getString("message")));
                                                        adapter.notifyDataSetChanged();
                                                    }
                                                });
                                Log.d("TTTT", "onSuccess: " + documentReference);
                                Toast.makeText(getContext(), "Done", Toast.LENGTH_SHORT);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT);
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


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        EventsViewModel messageViewModel =
                new ViewModelProvider(this).get(EventsViewModel.class);
        db = FirebaseFirestore.getInstance();

        binding = EventsFragmentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.eventsList.setLayoutManager(new LinearLayoutManager(getContext()));

        items = new ArrayList<>();
        adapter = new EventsAdapter(items, getActivity());
        binding.eventsList.setAdapter(adapter);

        db.collection("events").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        items.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Event item = new Event(document.getId(), document.getString("name"), document.getString("location"), document.getDate("date"), new ArrayList<>(), document.getString("message"));
                            items.add(item);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        // Handle errors
                    }
                })
        ;

        binding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCustomDialog();
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}