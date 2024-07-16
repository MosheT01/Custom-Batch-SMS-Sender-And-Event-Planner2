package com.example.custombatchsmssenderandeventplanner.ui.Report;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.custombatchsmssenderandeventplanner.R;
import com.example.custombatchsmssenderandeventplanner.databinding.FragmentReportBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class ReportFragment extends Fragment implements MessageAdapter.OnRetryClickListener {
    private static final int REQUEST_SMS_PERMISSION = 1;
    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String PHONE_NUMBER = "phoneNumber";
    private static final String MESSAGE = "message";
    private static final String MESSAGE_DETAILS = "messageDetails";

    private FragmentReportBinding binding;
    private List<MessageDetails> messageDetailsList = new ArrayList<>();
    private MessageAdapter messageAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentReportBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        RecyclerView recyclerViewMessages = binding.recyclerViewMessages;
        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(requireContext()));
        messageAdapter = new MessageAdapter(messageDetailsList, this);
        recyclerViewMessages.setAdapter(messageAdapter);

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        loadMessageDetails(sharedPreferences);

        String phoneNumber = sharedPreferences.getString(PHONE_NUMBER, "");
        String message = sharedPreferences.getString(MESSAGE, "");

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.SEND_SMS},
                    REQUEST_SMS_PERMISSION);
        } else {
            sendSMS(phoneNumber, message);
        }

        return root;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_SMS_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
                String phoneNumber = sharedPreferences.getString(PHONE_NUMBER, "");
                String message = sharedPreferences.getString(MESSAGE, "");
                sendSMS(phoneNumber, message);
            } else {
                Toast.makeText(requireContext(), "Permission denied. SMS cannot be sent.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void sendSMS(String phoneNumber, String message) {
        boolean isSuccess = false;
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            isSuccess = true;
            Toast.makeText(requireContext(), "SMS sent successfully!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Failed to send SMS.", Toast.LENGTH_SHORT).show();
        } finally {
            MessageDetails newMessage = new MessageDetails(phoneNumber, message, isSuccess);
            messageDetailsList.add(newMessage);
            saveMessageDetails(requireContext().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE));
            messageAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onRetryClick(MessageDetails messageDetails) {
        sendSMS(messageDetails.getPhoneNumber(), messageDetails.getMessage());
        // Navigate to the Home page
        NavController navController = NavHostFragment.findNavController(this);
        navController.navigate(R.id.action_reportFragment_to_homeFragment);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void loadMessageDetails(SharedPreferences sharedPreferences) {
        String serializedMessages = sharedPreferences.getString(MESSAGE_DETAILS, "");
        if (!serializedMessages.isEmpty()) {
            StringTokenizer tokenizer = new StringTokenizer(serializedMessages, ";");
            while (tokenizer.hasMoreTokens()) {
                String[] details = tokenizer.nextToken().split(",");
                if (details.length == 3) {
                    String phoneNumber = details[0];
                    String message = details[1];
                    boolean isSuccess = Boolean.parseBoolean(details[2]);
                    messageDetailsList.add(new MessageDetails(phoneNumber, message, isSuccess));
                }
            }
        }
    }

    private void saveMessageDetails(SharedPreferences sharedPreferences) {
        StringBuilder serializedMessages = new StringBuilder();
        for (MessageDetails messageDetails : messageDetailsList) {
            serializedMessages.append(messageDetails.getPhoneNumber())
                    .append(",")
                    .append(messageDetails.getMessage())
                    .append(",")
                    .append(messageDetails.isSuccess())
                    .append(";");
        }
        sharedPreferences.edit().putString(MESSAGE_DETAILS, serializedMessages.toString()).apply();
    }
}

