package com.example.custombatchsmssenderandeventplanner.ui.Report;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.custombatchsmssenderandeventplanner.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportFragment extends Fragment {

    private static final String TAG = "ReportFragment";
    private FirebaseFirestore db;
    private ExpandableListView expandableListView;
    private ExpandableListAdapter expandableListAdapter;
    private List<String> eventList;
    private Map<String, List<Map<String, Object>>> eventContactsMap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report, container, false);
        expandableListView = view.findViewById(R.id.expandableListView);

        // Initialize data structures
        eventList = new ArrayList<>();
        eventContactsMap = new HashMap<>();

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

                            if (eventInfo == null) {
                                Log.e(TAG, "Event info is missing in document: " + document.getId());
                                continue;
                            }

                            // Add event to list
                            eventList.add(eventInfo);

                            // Get contacts list
                            ArrayList<HashMap<String, Object>> contacts = (ArrayList<HashMap<String, Object>>) document.get("contacts");
                            Log.d(TAG, "Fetched contacts: " + contacts);

                            // Ensure contacts list is not null and convert to List<Map<String, Object>>
                            List<Map<String, Object>> contactList = new ArrayList<>();
                            if (contacts != null) {
                                contactList.addAll(contacts);
                            }

                            eventContactsMap.put(eventInfo, contactList);
                        } else {
                            Log.d(TAG, "Document not found.");
                        }
                    }

                    // Set up the expandable list adapter
                    expandableListAdapter = new ExpandableListAdapter(eventList, eventContactsMap);
                    expandableListView.setAdapter(expandableListAdapter);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching data: ", e);
                });
    }

    private class ExpandableListAdapter extends BaseExpandableListAdapter {
        private List<String> eventList;
        private Map<String, List<Map<String, Object>>> eventContactsMap;

        public ExpandableListAdapter(List<String> eventList, Map<String, List<Map<String, Object>>> eventContactsMap) {
            this.eventList = eventList;
            this.eventContactsMap = eventContactsMap;
        }

        @Override
        public int getGroupCount() {
            return eventList.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return eventContactsMap.get(eventList.get(groupPosition)).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return eventList.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return eventContactsMap.get(eventList.get(groupPosition)).get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            String eventName = (String) getGroup(groupPosition);
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_expandable_list_item_1, parent, false);
            }
            TextView textView = convertView.findViewById(android.R.id.text1);
            textView.setText(eventName);
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            Map<String, Object> contact = (Map<String, Object>) getChild(groupPosition, childPosition);
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact_row, parent, false);
            }

            TextView textViewContactName = convertView.findViewById(R.id.textViewContactName);
            TextView textViewPhoneNumber = convertView.findViewById(R.id.textViewPhoneNumber);
            TextView textViewStatus = convertView.findViewById(R.id.textViewStatus);
            LinearLayout layoutFailure = convertView.findViewById(R.id.layoutFailure);
            TextView textViewFailureReason = convertView.findViewById(R.id.textViewFailureReason);
            Button buttonResend = convertView.findViewById(R.id.buttonResend);

            String contactName = (String) contact.get("name");
            String phoneNumber = (String) contact.get("phone");
            Boolean messageSent = false;
            String failureReason = "";

            if (contact.containsKey("messageSent")) {
                Object messageSentObj = contact.get("messageSent");
                if (messageSentObj instanceof Boolean) {
                    messageSent = (Boolean) messageSentObj;
                } else if (messageSentObj instanceof String) {
                    messageSent = Boolean.parseBoolean((String) messageSentObj);
                }
            }

            if (contact.containsKey("failureReason")) {
                failureReason = (String) contact.get("failureReason");
            }

            textViewContactName.setText(contactName);
            textViewPhoneNumber.setText(phoneNumber);

            if (messageSent != null && messageSent) {
                textViewStatus.setText("Sent successfully");
                layoutFailure.setVisibility(View.GONE);
            } else {
                textViewStatus.setText("Not sent");
                layoutFailure.setVisibility(View.VISIBLE);
                textViewFailureReason.setText(failureReason);

                buttonResend.setOnClickListener(v -> {
                    // Implement resend logic here
                    resendMessage(contact);
                });
            }

            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        private void resendMessage(Map<String, Object> contact) {
            // Implement the logic to resend the message here
            Log.d(TAG, "Resending message to: " + contact.get("phone"));
            // After resending, you might want to update the UI to reflect the new status
        }
    }
}
