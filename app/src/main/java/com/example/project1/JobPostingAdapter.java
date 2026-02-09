package com.example.project1;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class JobPostingAdapter
        extends RecyclerView.Adapter<JobPostingAdapter.JobViewHolder>
        implements Filterable {

    private final Context context;
    private final List<JobPosting> jobList;
    private final List<JobPosting> jobListFull;

    public JobPostingAdapter(Context context, List<JobPosting> jobList) {
        this.context = context;
        this.jobList = jobList;
        this.jobListFull = new ArrayList<>(jobList);
    }

    public void setJobs(List<JobPosting> jobs) {
        jobList.clear();
        jobList.addAll(jobs);
        jobListFull.clear();
        jobListFull.addAll(jobs);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public JobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_job_posting, parent, false);
        return new JobViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JobViewHolder holder, int position) {
        JobPosting job = jobList.get(position);

        holder.tvJobTitle.setText(job.getServiceTitle() != null ? job.getServiceTitle() : "Service");

        holder.tvCategory.setText(
                job.getServiceCategory() != null
                        ? "Category: " + job.getServiceCategory()
                        : "Category: N/A"
        );

        if (job.getScheduledTime() != null) {
            SimpleDateFormat sdf =
                    new SimpleDateFormat("MMM d, yyyy h:mm a", Locale.getDefault());
            holder.tvTimestamp.setText(
                    sdf.format(new Date(job.getScheduledTime()))
            );
        } else {
            holder.tvTimestamp.setText("Time not scheduled");
        }

        holder.tvStatus.setText(
                job.getStatus() != null
                        ? "Status: " + job.getStatus().toUpperCase()
                        : "Status: N/A"
        );

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, JobDetailsActivity.class);
            intent.putExtra("jobId", job.getDocumentId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return jobList.size();
    }

    @Override
    public Filter getFilter() {
        return jobFilter;
    }

    private final Filter jobFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<JobPosting> filtered = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filtered.addAll(jobListFull);
            } else {
                String pattern = constraint.toString().toLowerCase().trim();
                for (JobPosting job : jobListFull) {
                    if ((job.getServiceTitle() != null &&
                            job.getServiceTitle().toLowerCase().contains(pattern)) ||
                            (job.getLocationName() != null &&
                                    job.getLocationName().toLowerCase().contains(pattern))) {
                        filtered.add(job);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filtered;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            jobList.clear();
            jobList.addAll((List<JobPosting>) results.values);
            notifyDataSetChanged();
        }
    };

    static class JobViewHolder extends RecyclerView.ViewHolder {
        TextView tvJobTitle, tvCategory, tvTimestamp, tvStatus;

        JobViewHolder(@NonNull View itemView) {
            super(itemView);
            tvJobTitle = itemView.findViewById(R.id.tvJobTitle);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}
