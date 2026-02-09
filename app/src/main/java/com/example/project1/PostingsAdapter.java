package com.example.project1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PostingsAdapter extends RecyclerView.Adapter<PostingsAdapter.ViewHolder> {

    private ArrayList<Posting> postingsList;

    public PostingsAdapter(ArrayList<Posting> postingsList) {
        this.postingsList = postingsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_posting, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Posting posting = postingsList.get(position);
        holder.tvJobTitle.setText(posting.getJobTitle());
        holder.tvJobStatus.setText("Status: " + posting.getJobStatus());
        holder.tvWorkerName.setText("Worker: " + posting.getWorkerName());
    }

    @Override
    public int getItemCount() {
        return postingsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvJobTitle, tvJobStatus, tvWorkerName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvJobTitle = itemView.findViewById(R.id.tvJobTitle);
            tvJobStatus = itemView.findViewById(R.id.tvJobStatus);
            tvWorkerName = itemView.findViewById(R.id.tvWorkerName);
        }
    }
}
