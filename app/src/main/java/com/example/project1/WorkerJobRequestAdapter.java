package com.example.project1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class WorkerJobRequestAdapter
        extends RecyclerView.Adapter<WorkerJobRequestAdapter.VH> {

    public interface JobActionListener {
        void onAction(JobPosting job);
    }

    private final List<JobPosting> jobs;
    private final JobActionListener acceptListener;
    private final JobActionListener rejectListener;

    public WorkerJobRequestAdapter(
            List<JobPosting> jobs,
            JobActionListener acceptListener,
            JobActionListener rejectListener
    ) {
        this.jobs = jobs;
        this.acceptListener = acceptListener;
        this.rejectListener = rejectListener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_worker_job_request, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        JobPosting job = jobs.get(pos);

        h.tvTitle.setText(job.getServiceTitle());
        h.tvLocation.setText(job.getLocationName());

        h.btnAccept.setOnClickListener(v ->
                acceptListener.onAction(job));

        h.btnReject.setOnClickListener(v ->
                rejectListener.onAction(job));
    }

    @Override
    public int getItemCount() {
        return jobs.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvTitle, tvLocation;
        Button btnAccept, btnReject;

        VH(@NonNull View v) {
            super(v);
            tvTitle = v.findViewById(R.id.tvJobTitle);
            tvLocation = v.findViewById(R.id.tvLocation);
            btnAccept = v.findViewById(R.id.btnAccept);
            btnReject = v.findViewById(R.id.btnReject);
        }
    }
}
