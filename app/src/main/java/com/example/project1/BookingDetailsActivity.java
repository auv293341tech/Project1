package com.example.project1;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class BookingDetailsActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private TextView tvServiceTitle, tvCustomerName, tvWorkerName, tvLocation, tvSchedule, tvStatus;
    private LinearLayout workerActionsLayout, recruiterActionsLayout;
    private Button btnStartWork, btnMarkCompleted, btnCall, btnCancelBooking, btnRecruiterComplete;
    private CircleImageView ivWorkerProfile;

    private String bookingId, customerId, workerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_details);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Initialize views
        tvServiceTitle = findViewById(R.id.tvServiceTitle);
        tvCustomerName = findViewById(R.id.tvCustomerName);
        tvWorkerName = findViewById(R.id.tvWorkerName);
        tvLocation = findViewById(R.id.tvLocation);
        tvSchedule = findViewById(R.id.tvSchedule);
        tvStatus = findViewById(R.id.tvStatus);
        workerActionsLayout = findViewById(R.id.worker_actions_layout);
        recruiterActionsLayout = findViewById(R.id.recruiter_actions_layout);
        btnStartWork = findViewById(R.id.btnStartWork);
        btnMarkCompleted = findViewById(R.id.btnMarkCompleted);
        btnCall = findViewById(R.id.btnCall);
        btnCancelBooking = findViewById(R.id.btnCancelBooking);
        btnRecruiterComplete = findViewById(R.id.btnRecruiterComplete);
        ivWorkerProfile = findViewById(R.id.ivWorkerProfile);

        bookingId = getIntent().getStringExtra("bookingId");

        if (bookingId != null && !bookingId.isEmpty()) {
            loadBookingDetails(bookingId);
        } else {
            Toast.makeText(this, "Booking ID is missing.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadBookingDetails(String bookingId) {
        db.collection("bookings").document(bookingId).get()
            .addOnSuccessListener(bookingDoc -> {
                if (bookingDoc.exists()) {
                    this.customerId = bookingDoc.getString("customerId");
                    this.workerId = bookingDoc.getString("workerId");
                    String jobId = bookingDoc.getString("jobId");

                    if (jobId != null && !jobId.isEmpty()) {
                        db.collection("jobPostings").document(jobId).get()
                            .addOnSuccessListener(jobDoc -> {
                                if (jobDoc.exists()) {
                                    db.collection("users").document(auth.getUid()).get()
                                        .addOnSuccessListener(userDoc -> {
                                            String userRole = userDoc.getString("role");
                                            updateUi(jobDoc, bookingDoc, userRole);
                                        });
                                }
                            });
                    }
                }
            });
    }

    private void updateUi(DocumentSnapshot jobDoc, DocumentSnapshot bookingDoc, String userRole) {
        tvServiceTitle.setText(jobDoc.getString("serviceTitle"));
        tvCustomerName.setText("Customer: " + jobDoc.getString("customerName"));
        tvWorkerName.setText("Worker: " + bookingDoc.getString("workerName"));
        tvLocation.setText("Location: " + jobDoc.getString("locationName"));

        Long scheduledTime = jobDoc.getLong("scheduledTime");
        if (scheduledTime != null && scheduledTime > 0) {
            tvSchedule.setText("Scheduled: " + new SimpleDateFormat("MMM d, yyyy h:mm a", Locale.getDefault()).format(new Date(scheduledTime)));
        } else {
            tvSchedule.setText("Scheduled: Not specified");
        }
        tvStatus.setText("Status: " + bookingDoc.getString("status").toUpperCase());

        if ("recruiter".equals(userRole)) {
            setupRecruiterView(bookingDoc.getString("status"));
        } else {
            setupWorkerView(bookingDoc.getString("status"));
        }
    }

    private void setupWorkerView(String status) {
        recruiterActionsLayout.setVisibility(View.GONE);
        workerActionsLayout.setVisibility(View.VISIBLE);

        boolean isAccepted = "accepted".equalsIgnoreCase(status);
        boolean isStarted = "started".equalsIgnoreCase(status);

        btnStartWork.setVisibility(isAccepted ? View.VISIBLE : View.GONE);
        btnMarkCompleted.setVisibility(isStarted ? View.VISIBLE : View.GONE);
        btnCancelBooking.setEnabled(isAccepted || isStarted);

        btnStartWork.setOnClickListener(v -> updateBookingStatus("started", false));
        btnMarkCompleted.setOnClickListener(v -> updateBookingStatus("completed", false));
        btnCall.setOnClickListener(v -> callUser(customerId));
        btnCancelBooking.setOnClickListener(v -> showCancelDialog());
    }

    private void setupRecruiterView(String status) {
        workerActionsLayout.setVisibility(View.GONE);

        db.collection("users").document(workerId).get().addOnSuccessListener(workerDoc -> {
            if (workerDoc.exists()) {
                String profileUrl = workerDoc.getString("profileImageUrl");
                if (profileUrl != null && !profileUrl.isEmpty()) {
                    Glide.with(this).load(profileUrl).into(ivWorkerProfile);
                }
            }
        });

        boolean isAccepted = "accepted".equalsIgnoreCase(status);
        boolean isStarted = "started".equalsIgnoreCase(status);

        recruiterActionsLayout.setVisibility(isStarted ? View.VISIBLE : View.GONE);
        btnCancelBooking.setEnabled(isAccepted || isStarted);

        btnCancelBooking.setOnClickListener(v -> showCancelDialog());
        btnRecruiterComplete.setOnClickListener(v -> updateBookingStatus("completed", true));

        btnCall.setOnClickListener(v -> callUser(workerId));
    }

    private void showReviewDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_review, null);
        builder.setView(dialogView);

        final RatingBar ratingBar = dialogView.findViewById(R.id.dialog_ratingBar);
        final EditText etReview = dialogView.findViewById(R.id.dialog_etReview);

        builder.setTitle("Leave a Review (Optional)")
            .setPositiveButton("Submit", (dialog, id) -> {
                float rating = ratingBar.getRating();
                String reviewText = etReview.getText().toString().trim();
                if (rating > 0) {
                    submitReview(rating, reviewText);
                } else {
                    finish();
                }
            })
            .setNegativeButton("Skip", (dialog, id) -> {
                dialog.dismiss();
                finish(); // Go back after skipping
            });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void submitReview(float rating, String reviewText) {
        Map<String, Object> review = new HashMap<>();
        review.put("rating", rating);
        review.put("reviewText", reviewText);
        review.put("bookingId", bookingId);
        review.put("workerId", workerId);
        review.put("customerId", customerId);
        review.put("timestamp", new Date());

        db.collection("reviews").add(review)
            .addOnSuccessListener(documentReference -> {
                Toast.makeText(this, "Review submitted.", Toast.LENGTH_SHORT).show();
                finish(); // Go back after submitting
            })
            .addOnFailureListener(e -> {
                Toast.makeText(this, "Failed to submit review.", Toast.LENGTH_SHORT).show();
                finish(); // Go back anyway
            });
    }

    private void callUser(String userId) {
        db.collection("users").document(userId).get().addOnSuccessListener(doc -> {
            if (doc.exists() && doc.getString("phone") != null) {
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + doc.getString("phone"))));
            } else {
                Toast.makeText(this, "Phone number not available.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showCancelDialog() {
        new AlertDialog.Builder(this)
            .setTitle("Cancel Booking")
            .setMessage("Are you sure?")
            .setPositiveButton("Yes", (dialog, which) -> updateBookingStatus("cancelled", false))
            .setNegativeButton("No", null)
            .show();
    }

    private void updateBookingStatus(String newStatus, boolean showReviewDialog) {
        db.collection("bookings").document(bookingId).update("status", newStatus)
            .addOnSuccessListener(aVoid -> {
                Toast.makeText(this, "Booking " + newStatus, Toast.LENGTH_SHORT).show();
                if (showReviewDialog) {
                    showReviewDialog();
                } else {
                    recreate(); // Refresh activity to show correct state
                }
            })
            .addOnFailureListener(e -> Toast.makeText(this, "Failed to update status.", Toast.LENGTH_SHORT).show());
    }
}
