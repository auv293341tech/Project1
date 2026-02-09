package com.example.project1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ApplicantAdapter
        extends RecyclerView.Adapter<ApplicantAdapter.ApplicantViewHolder> {

    public interface OnAssignClickListener {
        void onAssign(Application application);
    }

    private final Context context;
    private final List<Application> applications;
    private final OnAssignClickListener listener;

    public ApplicantAdapter(
            Context context,
            List<Application> applications,
            OnAssignClickListener listener
    ) {
        this.context = context;
        this.applications = applications;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ApplicantViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_applicant, parent, false);
        return new ApplicantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ApplicantViewHolder holder, int position) {

        Application app = applications.get(position);

        holder.tvApplicantName.setText(
                app.getWorkerName() != null
                        ? app.getWorkerName()
                        : "Worker"
        );

        holder.tvApplicantSkill.setText(
                app.getWorkerSkill() != null
                        ? app.getWorkerSkill()
                        : "Skill not specified"
        );

        holder.btnAssign.setOnClickListener(v ->
                listener.onAssign(app)
        );
    }

    @Override
    public int getItemCount() {
        return applications.size();
    }

    static class ApplicantViewHolder extends RecyclerView.ViewHolder {

        TextView tvApplicantName, tvApplicantSkill;
        Button btnAssign;

        ApplicantViewHolder(@NonNull View itemView) {
            super(itemView);
            tvApplicantName = itemView.findViewById(R.id.tvApplicantName);
            tvApplicantSkill = itemView.findViewById(R.id.tvApplicantSkill);
            btnAssign = itemView.findViewById(R.id.btnAssign);
        }
    }
}
