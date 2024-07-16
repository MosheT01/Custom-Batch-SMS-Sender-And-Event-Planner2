
package com.example.custombatchsmssenderandeventplanner.ui.Report;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.custombatchsmssenderandeventplanner.R;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private List<MessageDetails> messageDetailsList;
    private OnRetryClickListener retryClickListener;

    public MessageAdapter(List<MessageDetails> messageDetailsList, OnRetryClickListener retryClickListener) {
        this.messageDetailsList = messageDetailsList;
        this.retryClickListener = retryClickListener;
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
        holder.textViewPhoneNumber.setText(messageDetails.getPhoneNumber());
        holder.textViewMessage.setText(messageDetails.getMessage());
        holder.textViewStatus.setText(messageDetails.isSuccess() ? "\tSent" : "Failed");

        holder.buttonRetry.setVisibility(messageDetails.isSuccess() ? View.GONE : View.VISIBLE);
        holder.buttonRetry.setOnClickListener(v -> retryClickListener.onRetryClick(messageDetails));
    }

    @Override
    public int getItemCount() {
        return messageDetailsList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewPhoneNumber, textViewMessage, textViewStatus;
        Button buttonRetry;

        ViewHolder(View itemView) {
            super(itemView);
            textViewPhoneNumber = itemView.findViewById(R.id.textViewPhoneNumber);
            textViewMessage = itemView.findViewById(R.id.textViewMessage);
            textViewStatus = itemView.findViewById(R.id.textViewStatus);
            buttonRetry = itemView.findViewById(R.id.buttonRetry);
        }
    }

    interface OnRetryClickListener {
        void onRetryClick(MessageDetails messageDetails);
    }
}
