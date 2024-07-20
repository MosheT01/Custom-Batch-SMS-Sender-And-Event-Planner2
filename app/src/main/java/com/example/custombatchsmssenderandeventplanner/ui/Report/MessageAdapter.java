package com.example.custombatchsmssenderandeventplanner.ui.Report;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.custombatchsmssenderandeventplanner.R;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private List<MessageDetails> messageDetailsList;

    public MessageAdapter(List<MessageDetails> messageDetailsList) {
        this.messageDetailsList = messageDetailsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MessageDetails messageDetails = messageDetailsList.get(position);
        holder.textViewContactName.setText(messageDetails.getContactName());
        holder.textViewPhoneNumber.setText(messageDetails.getPhoneNumber());
        holder.textViewEventInfo.setText(messageDetails.getEventInfo());
    }

    @Override
    public int getItemCount() {
        return messageDetailsList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewContactName, textViewPhoneNumber, textViewEventInfo;

        ViewHolder(View itemView) {
            super(itemView);
            textViewContactName = itemView.findViewById(R.id.textViewContactName);
            textViewPhoneNumber = itemView.findViewById(R.id.textViewPhoneNumber);
            textViewEventInfo = itemView.findViewById(R.id.textViewEventInfo);
        }
    }
}
