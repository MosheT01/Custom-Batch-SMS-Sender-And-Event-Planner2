package com.example.custombatchsmssenderandeventplanner.ui.event;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.custombatchsmssenderandeventplanner.R;

import java.util.List;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {

    final List<ContactListItem> mData;

    public ContactsAdapter(List<ContactListItem> data) {
        data.forEach(d -> {
            Log.d("DATA", d.getPrimaryText() + " " + d.getSecondaryText());
        });
        this.mData = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.two_line_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textName.setText(mData.get(position).getPrimaryText());
        holder.textPhone.setText(mData.get(position).getSecondaryText());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textName;
        TextView textPhone;

        ViewHolder(View itemView) {
            super(itemView);
            textName = itemView.findViewById(android.R.id.text1);
            textPhone = itemView.findViewById(android.R.id.text2);
        }
    }
}
