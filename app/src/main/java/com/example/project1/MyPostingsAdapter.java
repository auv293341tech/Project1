package com.example.project1;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MyPostingsAdapter
        extends RecyclerView.Adapter<MyPostingsAdapter.ViewHolder> {

    private final Context context;
    private final List<JobPosting> jobList;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public MyPostingsAdapter(Context context, List<JobPosting> jobList) {
        this.context = context;
        this.jobList = jobList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_my_posting, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder, int position) {

        JobPosting job = jobList.get(position);

        holder.tvJobTitle.setText(job.getServiceTitle());
        holder.tvCategory.setText(job.getServiceCategory());
        setJobStatus(holder, job.getStatus());

        // Set Job Description
        if (job.getAnswers() != null && !job.getAnswers().isEmpty()) {
            StringBuilder description = new StringBuilder();
            for (Object answer : job.getAnswers().values()) {
                description.append(answer.toString()).append(". ");
            }
            holder.tvJobDescription.setText(description.toString().trim());
        } else {
            holder.tvJobDescription.setText("No additional details provided.");
        }

        // Set Location
        String fullLocation = job.getLocationName();
        String city = fullLocation;
        if (fullLocation != null && fullLocation.contains(",")) {
            String[] parts = fullLocation.split(",");
            if (parts.length > 1) {
                city = parts[1].trim(); // Assuming city is the second part
            } else {
                city = parts[0].trim();
            }
        }
        holder.tvLocation.setText(city);


        // Set Schedule
        if (job.getScheduledTime() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM d, h:mm a", Locale.getDefault());
            holder.tvSchedule.setText(sdf.format(new Date(job.getScheduledTime())));
        } else {
            holder.tvSchedule.setText("Not scheduled");
        }

        // Set Time Posted
        if (job.getCreatedAt() != null) {
            holder.tvTimePosted.setText(getTimeAgo(job.getCreatedAt()));
        } else {
            holder.tvTimePosted.setText("");
        }

        // Set Applicants Count
        setApplicantsCount(holder, job.getDocumentId());

        // Set Category Icon
        setCategoryIcon(holder, job.getServiceCategory());

        // Go to job details screen on click
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

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivJobIcon, ivLocationIcon, ivScheduleIcon, ivTimePostedIcon, ivApplicantsIcon;
        TextView tvJobTitle, tvCategory, tvJobDescription, tvLocation, tvApplicantsCount, tvSchedule, tvTimePosted, tvJobStatus;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivJobIcon = itemView.findViewById(R.id.ivJobIcon);
            tvJobTitle = itemView.findViewById(R.id.tvJobTitle);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvJobDescription = itemView.findViewById(R.id.tvJobDescription);
            ivLocationIcon = itemView.findViewById(R.id.ivLocationIcon);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            ivApplicantsIcon = itemView.findViewById(R.id.ivApplicantsIcon);
            tvApplicantsCount = itemView.findViewById(R.id.tvApplicantsCount);
            ivScheduleIcon = itemView.findViewById(R.id.ivScheduleIcon);
            tvSchedule = itemView.findViewById(R.id.tvSchedule);
            ivTimePostedIcon = itemView.findViewById(R.id.ivTimePostedIcon);
            tvTimePosted = itemView.findViewById(R.id.tvTimePosted);
            tvJobStatus = itemView.findViewById(R.id.tvJobStatus);
        }
    }

    private void setApplicantsCount(ViewHolder holder, String jobId) {
        holder.tvApplicantsCount.setText("Loading...");
        db.collection("jobPostings").document(jobId).collection("applicants").get()
                .addOnSuccessListener(snapshot -> {
                    int count = snapshot.size();
                    holder.tvApplicantsCount.setText(count + (count == 1 ? " applicant" : " applicants"));
                })
                .addOnFailureListener(e -> holder.tvApplicantsCount.setText("0 applicants"));
    }

    private String getTimeAgo(long time) {
        long now = System.currentTimeMillis();
        long diff = now - time;

        long days = TimeUnit.MILLISECONDS.toDays(diff);
        if (days > 0) return days + "d ago";

        long hours = TimeUnit.MILLISECONDS.toHours(diff);
        if (hours > 0) return hours + "h ago";

        long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);
        return minutes + "m ago";
    }

    private void setCategoryIcon(ViewHolder holder, String category) {
        int iconRes;

        if (category == null) {
            category = "";
        }

        String cleanedCategory = category.toLowerCase().trim();

        switch (cleanedCategory) {
            case "welder":
                iconRes = R.drawable.ic_welder;
                break;
            case "plumber":
                iconRes = R.drawable.ic_plumber;
                break;
            case "painter":
                iconRes = R.drawable.ic_painter;
                break;
            case "event helper":
                iconRes = R.drawable.ic_helper;
                break;
            case "electrician":
                iconRes = R.drawable.ic_electrician;
                break;
            case "cleaner":
                iconRes = R.drawable.ic_cleaner;
                break;
            case "carpenter":
                iconRes = R.drawable.ic_carpenter;
                break;
            case "4w mechanic":
                iconRes = R.drawable.ic_car;
                break;
            case "2w mechanic":
                iconRes = R.drawable.ic_bike;
                break;
            case "ac repair":
                iconRes = R.drawable.ic_ac;
                break;
            default:
                iconRes = R.drawable.ic_profile_placeholder;
                break;
        }

        Glide.with(context)
                .load(iconRes)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(holder.ivJobIcon);
    }

    private void setJobStatus(ViewHolder holder, String status) {
        if (status == null) {
            status = "open"; // Default to open
        }

        int bgRes;
        int textColor;
        String text;

        switch (status.toLowerCase()) {
            case "open":
                text = "Active";
                bgRes = R.drawable.bg_status_active;
                textColor = R.color.green_700;
                break;
            case "accepted":
            case "assigned":
                text = "Assigned";
                bgRes = R.drawable.bg_status_assigned;
                textColor = R.color.orange_700;
                break;
            case "completed":
                text = "Completed";
                bgRes = R.drawable.bg_status_completed;
                textColor = R.color.blue_700;
                break;
            case "cancelled":
                text = "Cancelled";
                bgRes = R.drawable.bg_status_cancelled;
                textColor = R.color.red_700;
                break;
            default:
                text = "Unknown";
                bgRes = R.drawable.bg_grey_gradient;
                textColor = R.color.grey_700;
                break;
        }

        holder.tvJobStatus.setText(text);
        holder.tvJobStatus.setBackground(ContextCompat.getDrawable(context, bgRes));
        holder.tvJobStatus.setTextColor(ContextCompat.getColor(context, textColor));
    }
}
