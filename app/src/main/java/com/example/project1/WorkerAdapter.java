package com.example.project1;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Locale;

public class WorkerAdapter
        extends RecyclerView.Adapter<WorkerAdapter.WorkerViewHolder> {

    public interface OnAssignClickListener {
        void onAssign(Worker worker);
    }

    private final Context context;
    private final List<Worker> workers;
    private final boolean jobAlreadyAssigned;
    private final OnAssignClickListener listener;

    public WorkerAdapter(
            Context context,
            List<Worker> workers,
            boolean jobAlreadyAssigned,
            OnAssignClickListener listener
    ) {
        this.context = context;
        this.workers = workers;
        this.jobAlreadyAssigned = jobAlreadyAssigned;
        this.listener = listener;
    }

    @NonNull
    @Override
    public WorkerViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_worker, parent, false);
        return new WorkerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull WorkerViewHolder holder,
            int position
    ) {
        Worker worker = workers.get(position);

        // ===== BASIC INFO =====
        holder.tvName.setText(worker.getName());
        holder.tvSkill.setText(worker.getSkill());

        holder.tvDistance.setText(
                String.format(
                        Locale.getDefault(),
                        "%.1f km away",
                        worker.getDistance()
                )
        );

        // ===== PROFILE IMAGE =====
        if (worker.getProfileImageUrl() != null &&
                !worker.getProfileImageUrl().isEmpty()) {

            Glide.with(context)
                    .load(worker.getProfileImageUrl())
                    .placeholder(R.drawable.ic_account)
                    .error(R.drawable.ic_account)
                    .into(holder.ivProfile);
        } else {
            holder.ivProfile.setImageResource(R.drawable.ic_account);
        }

        // ===== ASSIGN BUTTON LOGIC =====
        if (jobAlreadyAssigned) {

            holder.btnAssign.setEnabled(false);
            holder.btnAssign.setText("Assigned");
            holder.btnAssign.setAlpha(0.5f);

        } else {

            holder.btnAssign.setEnabled(true);
            holder.btnAssign.setText("Assign");
            holder.btnAssign.setAlpha(1f);

            holder.btnAssign.setOnClickListener(v -> {
                v.setEnabled(false); // prevent double tap
                listener.onAssign(worker);
            });
        }

        // ===== OPEN WORKER PROFILE =====
        holder.itemView.setOnClickListener(v -> {
            Intent intent =
                    new Intent(context, WorkerProfileActivity.class);
            intent.putExtra("workerUid", worker.getUid());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return workers.size();
    }

    // ================= VIEW HOLDER =================
    static class WorkerViewHolder extends RecyclerView.ViewHolder {

        ImageView ivProfile;
        TextView tvName, tvSkill, tvDistance;
        Button btnAssign;

        WorkerViewHolder(@NonNull View itemView) {
            super(itemView);

            ivProfile = itemView.findViewById(R.id.ivProfile);
            tvName = itemView.findViewById(R.id.tvWorkerName);
            tvSkill = itemView.findViewById(R.id.tvWorkerSkill);
            tvDistance = itemView.findViewById(R.id.tvWorkerDistance);
            btnAssign = itemView.findViewById(R.id.btnAssign);
        }
    }
}
