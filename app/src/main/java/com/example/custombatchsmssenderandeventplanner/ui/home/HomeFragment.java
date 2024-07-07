package com.example.custombatchsmssenderandeventplanner.ui.home;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.custombatchsmssenderandeventplanner.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private static final int REQUEST_SMS_PERMISSION = 1;

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize views directly from binding
        EditText editTextPhoneNumber = binding.editTextPhoneNumber;
        EditText editTextMessage = binding.editTextMessage;
        Button buttonSendSMS = binding.buttonSendSMS;

        // Handle send SMS button click
        buttonSendSMS.setOnClickListener(v -> {
            // Check if permission is granted
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.SEND_SMS)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted, request it
                ActivityCompat.requestPermissions(requireActivity(),
                        new String[]{Manifest.permission.SEND_SMS},
                        REQUEST_SMS_PERMISSION);
            } else {
                // Permission already granted, send SMS
                sendSMS(editTextPhoneNumber.getText().toString(), editTextMessage.getText().toString());
            }
        });

        return root;
    }

    // Handle permission request result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_SMS_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, send SMS
                EditText editTextPhoneNumber = binding.editTextPhoneNumber;
                EditText editTextMessage = binding.editTextMessage;
                sendSMS(editTextPhoneNumber.getText().toString(), editTextMessage.getText().toString());
            } else {
                // Permission denied, show toast or handle failure
                Toast.makeText(requireContext(), "Permission denied. SMS cannot be sent.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Method to send SMS
    private void sendSMS(String phoneNumber, String message) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            // Show success message or handle success case
            Toast.makeText(requireContext(), "SMS sent successfully!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            // Show failure message or handle failure case
            Toast.makeText(requireContext(), "Failed to send SMS.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}