package com.example.custombatchsmssenderandeventplanner.ui.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.example.custombatchsmssenderandeventplanner.R;
import com.example.custombatchsmssenderandeventplanner.ui.events.EventsAdapter;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class SettingsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        Switch darkModeSwitch = view.findViewById(R.id.switch_dark_mode);
        Button clearDatabaseButton = view.findViewById(R.id.btn_clear_data);

        // Load the saved dark mode preference and set the switch state
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_prefs", getActivity().MODE_PRIVATE);
        boolean isDarkMode = sharedPreferences.getBoolean("dark_mode", false);
        darkModeSwitch.setChecked(isDarkMode);

        // Set the switch listener
        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Save the user's preference
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("dark_mode", isChecked);
            editor.apply();

            // Apply the theme
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });

        clearDatabaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearDatabase();
            }
        });

        return view;
    }

    private void clearDatabase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("events")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<String> eventIds = new ArrayList<>();
                    queryDocumentSnapshots.forEach(document -> eventIds.add(document.getId()));

                    EventsAdapter adapter = new EventsAdapter(new ArrayList<>(), getActivity());
                    for (int i = 0; i < eventIds.size(); i++) {
                        adapter.deleteEventById(eventIds.get(i), i);
                    }

                    Toast.makeText(getActivity(), "Database cleared.", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(getActivity(), "Failed to retrieve events.", Toast.LENGTH_SHORT).show());
    }
}
