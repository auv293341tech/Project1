package com.example.project1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MyApplicationsAdapter
        extends RecyclerView.Adapter<MyApplicationsAdapter.MyApplicationsViewHolder> {

    private Context context;
    private List<Application> applications;

    public MyApplicationsAdapter(Context context, List<Application> applications) {
        this.context = context;
        this.applications = applications;
    }

    @NonNull
    @Override
    public MyApplicationsViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_my_application, parent, false);
        return new MyApplicationsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull MyApplicationsViewHolder holder, int position) {

        Application app = applications.get(position);

        // 🛠 Service Title
        holder.tvServiceTitle.setText(
                app.getServiceTitle() != null
                        ? app.getServiceTitle()
                        : "Service"
        );

        // 👤 Customer
        holder.tvCustomerName.setText(
                "Customer: " +
                        (app.getCustomerName() != null
                                ? app.getCustomerName()
                                : "N/A")
        );

        // 📍 Location
        holder.tvLocation.setText(
                "Location: " +
                        (app.getLocationName() != null
                                ? app.getLocationName()
                                : "N/A")
        );

        // ⏰ Scheduled Time
        if (app.getScheduledTime() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat(
                    "MMM d, yyyy h:mm a", Locale.getDefault());
            holder.tvSchedule.setText(
                    "Scheduled: " +
                            sdf.format(new Date(app.getScheduledTime()))
            );
        } else {
            holder.tvSchedule.setText("Scheduled: Not specified");
        }

        // 📌 Application Status
        holder.tvApplicationStatus.setText(
                "Application: " +
                        (app.getStatus() != null
                                ? app.getStatus().toUpperCase()
                                : "UNKNOWN")
        );

        // 📌 Job Status
        holder.tvJobStatus.setText(
                "Job: " +
                        (app.getJobStatus() != null
                                ? app.getJobStatus().toUpperCase()
                                : "UNKNOWN")
        );
    }

    @Override
    public int getItemCount() {
        return applications.size();
    }

    static class MyApplicationsViewHolder extends RecyclerView.ViewHolder {

        TextView tvServiceTitle;
        TextView tvCustomerName;
        TextView tvLocation;
        TextView tvSchedule;
        TextView tvApplicationStatus;
        TextView tvJobStatus;

        public MyApplicationsViewHolder(@NonNull View itemView) {
            super(itemView);

            tvServiceTitle = itemView.findViewById(R.id.tvServiceTitle);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvSchedule = itemView.findViewById(R.id.tvSchedule);
            tvApplicationStatus = itemView.findViewById(R.id.tvApplicationStatus);
            tvJobStatus = itemView.findViewById(R.id.tvJobStatus);
        }
    }
}
