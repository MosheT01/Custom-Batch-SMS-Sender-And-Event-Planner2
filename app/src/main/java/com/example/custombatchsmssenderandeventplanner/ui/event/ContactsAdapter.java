package com.example.custombatchsmssenderandeventplanner.ui.event;

import static androidx.core.content.ContextCompat.startActivity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.custombatchsmssenderandeventplanner.R;
import com.example.custombatchsmssenderandeventplanner.event.Contact;
import com.example.custombatchsmssenderandeventplanner.event.Event;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {

    final List<Contact> mData;
    Activity activity;
    Event event;

    public ContactsAdapter(List<Contact> data, Event event, Activity activity) {
        this.mData = data;
        this.activity = activity;
        this.event = event;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textName.setText(mData.get(position).getName());
        holder.textPhone.setText(mData.get(position).getPhone());

        holder.btnDelete.setOnClickListener(e -> showCustomDialog(position));

        holder.btnPreview.setOnClickListener(e -> showPreviewDialog(position));

        holder.btnCall.setOnClickListener(e -> {
            String phoneToCall = mData.get(position).getPhone();
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneToCall));
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CALL_PHONE}, 1);
            } else {
                startActivity(activity, intent, null);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textName, textPhone;
        MaterialButton btnDelete, btnPreview, btnCall;

        ViewHolder(View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.text1);
            textPhone = itemView.findViewById(R.id.text2);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnPreview = itemView.findViewById(R.id.btnPreview);
            btnCall = itemView.findViewById(R.id.btnCall);
        }
    }

    private void showCustomDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.yes_no_dialog, null);
        builder.setView(dialogView);

        MaterialButton yesButton = dialogView.findViewById(R.id.btnYes);
        MaterialButton noButton = dialogView.findViewById(R.id.btnNo);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        noButton.setOnClickListener(view -> alertDialog.dismiss());

        yesButton.setOnClickListener(v -> {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.document("events/" + event.getId())
                    .update("contacts", FieldValue.arrayRemove(mData.get(position).toHashMap()))
                    .addOnSuccessListener(unused -> {
                        mData.remove(position);
                        notifyDataSetChanged();
                    });
            alertDialog.dismiss();
        });
    }

    private void showPreviewDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_preview, null);
        builder.setView(dialogView);

        MaterialButton closeButton = dialogView.findViewById(R.id.btnClose);
        TextView text1 = dialogView.findViewById(R.id.text1);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        closeButton.setOnClickListener(view -> alertDialog.dismiss());

        text1.setText(event.formatMessage(mData.get(position)));
    }
}
