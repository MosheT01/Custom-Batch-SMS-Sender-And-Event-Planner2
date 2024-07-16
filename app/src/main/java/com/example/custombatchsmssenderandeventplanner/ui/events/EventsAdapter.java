package com.example.custombatchsmssenderandeventplanner.ui.events;

import static java.security.AccessController.getContext;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.custombatchsmssenderandeventplanner.R;
import com.example.custombatchsmssenderandeventplanner.databinding.MessageFragmentBinding;
import com.example.custombatchsmssenderandeventplanner.event.Contact;
import com.example.custombatchsmssenderandeventplanner.event.Event;
import com.example.custombatchsmssenderandeventplanner.ui.event.EventActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.core.FirestoreClient;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.ViewHolder> {

    final List<Event> mData;
    Activity activity;

    public EventsAdapter(List<Event> data, Activity activity) {
        this.mData = data;
        this.activity = activity;
    }

    private void showCustomDialog(int position) {
        // Create an AlertDialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        // Inflate the custom layout
        LayoutInflater inflater2 = activity.getLayoutInflater();
        View dialogView = inflater2.inflate(R.layout.yes_no_dialog, null);
        builder.setView(dialogView);

        // Set up the dialog's button click event
        MaterialButton yesButton = dialogView.findViewById(R.id.btnYes);
        MaterialButton noButton = dialogView.findViewById(R.id.btnNo);

        // Create and show the dialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.document("events/" + mData.get(position).getId())
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                mData.remove(position);
                                notifyDataSetChanged();
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

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textName.setText(mData.get(position).getName());
        holder.textName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, EventActivity.class);
                intent.putExtra("id", mData.get(position).getId());
                activity.startActivity(intent);
//                NavController navController = Navigation.findNavController(activity, R.id.nav_host_fragment_content_main);
//                navController.navigate(R.id.nav_message);
            }
        });

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCustomDialog(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textName;
        MaterialButton btnDelete;

        ViewHolder(View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.text1);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
