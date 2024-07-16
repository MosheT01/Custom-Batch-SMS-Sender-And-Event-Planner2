package com.example.custombatchsmssenderandeventplanner.ui.events;

import static java.security.AccessController.getContext;

import android.app.Activity;
import android.content.Intent;
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
import com.example.custombatchsmssenderandeventplanner.event.Event;
import com.example.custombatchsmssenderandeventplanner.ui.event.EventActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.core.FirestoreClient;

import java.util.List;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.ViewHolder> {

    final List<Event> mData;
    Activity activity;

    public EventsAdapter(List<Event> data, Activity activity) {
        this.mData = data;
        this.activity = activity;
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
