package com.example.project1;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BookingAdapter
        extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {

    private List<Booking> bookingList;
    private List<JobPosting> jobPostingList;
    private final MyBookingsActivity activity;
    private final String userRole;
    private FirebaseFirestore db;

    public BookingAdapter(List<Booking> bookingList, List<JobPosting> jobPostingList, MyBookingsActivity activity, String userRole) {
        this.bookingList = bookingList;
        this.jobPostingList = jobPostingList;
        this.activity = activity;
        this.userRole = userRole;
        this.db = FirebaseFirestore.getInstance();
    }

    public void setBookings(List<Booking> bookings, List<JobPosting> jobPostings) {
        this.bookingList = bookings;
        this.jobPostingList = jobPostings;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_booking, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull BookingViewHolder holder, int position) {

        Booking booking = bookingList.get(position);
        JobPosting jobPosting = jobPostingList.get(position);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(activity, BookingDetailsActivity.class);
            intent.putExtra("bookingId", booking.getDocumentId());
            activity.startActivity(intent);
        });

        holder.tvServiceTitle.setText(jobPosting.getServiceTitle());
        holder.tvLocation.setText("Location: " + jobPosting.getLocationName());

        // Role-specific UI changes
        if ("worker".equals(userRole)) {
            holder.tvCustomerName.setText("Customer: " + jobPosting.getCustomerName());
            holder.ivMapIcon.setVisibility(View.VISIBLE);
            holder.ivCallIcon.setVisibility(View.VISIBLE);
            setupWorkerActions(holder, jobPosting);
        } else { // Recruiter view (customer)
            holder.tvCustomerName.setText("Worker: " + booking.getWorkerName());
            holder.ivMapIcon.setVisibility(View.GONE); // Hide for customers
            holder.ivCallIcon.setVisibility(View.VISIBLE);
            setupRecruiterActions(holder, booking, jobPosting);
        }

        if (jobPosting.getScheduledTime() != 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy h:mm a", Locale.getDefault());
            holder.tvSchedule.setText("Scheduled: " + sdf.format(new Date(jobPosting.getScheduledTime())));
        } else {
            holder.tvSchedule.setText("Scheduled: Not specified");
        }

        String status = booking.getStatus();
        holder.tvStatus.setText("Status: " + status.toUpperCase());

        boolean isRecruiter = "recruiter".equals(userRole);
        boolean isStarted = "started".equalsIgnoreCase(status);

        if (isRecruiter && isStarted) {
            holder.btnComplete.setVisibility(View.VISIBLE);
            holder.btnComplete.setOnClickListener(v -> activity.markBookingCompleted(booking));
        } else {
            holder.btnComplete.setVisibility(View.GONE);
        }
    }

    private void setupWorkerActions(BookingViewHolder holder, JobPosting jobPosting) {
        holder.ivMapIcon.setOnClickListener(v -> {
            GeoPoint location = jobPosting.getLocation();
            if (location != null) {
                Uri gmmIntentUri = Uri.parse("geo:" + location.getLatitude() + "," + location.getLongitude() + "?q=" + jobPosting.getLocationName());
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(activity.getPackageManager()) != null) {
                    activity.startActivity(mapIntent);
                }
            }
        });

        holder.ivCallIcon.setOnClickListener(v -> callUser(jobPosting.getCustomerId()));
    }

    private void setupRecruiterActions(BookingViewHolder holder, Booking booking, JobPosting jobPosting) {
        // No map icon action for recruiters
        holder.ivCallIcon.setOnClickListener(v -> callUser(booking.getWorkerId()));
    }

    private void callUser(String userId) {
        db.collection("users").document(userId).get()
            .addOnSuccessListener(doc -> {
                if (doc.exists() && doc.getString("phone") != null) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + doc.getString("phone")));
                    activity.startActivity(intent);
                } else {
                    Toast.makeText(activity, "User phone number not available.", Toast.LENGTH_SHORT).show();
                }
            });
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    static class BookingViewHolder extends RecyclerView.ViewHolder {

        TextView tvServiceTitle, tvCustomerName, tvLocation, tvSchedule, tvStatus;
        Button btnComplete;
        ImageView ivMapIcon, ivCallIcon;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvServiceTitle = itemView.findViewById(R.id.tvServiceTitle);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvSchedule = itemView.findViewById(R.id.tvSchedule);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnComplete = itemView.findViewById(R.id.btnComplete);
            ivMapIcon = itemView.findViewById(R.id.ivMapIcon);
            ivCallIcon = itemView.findViewById(R.id.ivCallIcon);
        }
    }
}
