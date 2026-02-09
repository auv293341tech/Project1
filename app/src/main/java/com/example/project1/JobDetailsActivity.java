package com.example.project1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class JobDetailsActivity extends AppCompatActivity {

    private TextView tvJobTitle, tvRecruiterName, tvLocation,
            tvTimestamp, tvSkill, tvStatus, tvAnswers, tvAcceptedBy;

    private Button btnApply, btnEdit, btnCancel, btnAccept, btnReject, btnViewApplicants;
    private LinearLayout workerActions;

    private FirebaseFirestore db;
    private String jobId;
    private String currentUserId;
    private DocumentSnapshot jobDocument; // To hold job data

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_details);

        db = FirebaseFirestore.getInstance();
        currentUserId = FirebaseAuth.getInstance().getUid();

        jobId = getIntent().getStringExtra("jobId");
        if (jobId == null) {
            Toast.makeText(this, "Invalid job", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        loadJobDetails();
    }

    private void initViews() {
        tvJobTitle = findViewById(R.id.tvJobTitle);
        tvRecruiterName = findViewById(R.id.tvRecruiterName);
        tvLocation = findViewById(R.id.tvLocation);
        tvTimestamp = findViewById(R.id.tvTimestamp);
        tvSkill = findViewById(R.id.tvSkill);
        tvStatus = findViewById(R.id.tvStatus);
        tvAnswers = findViewById(R.id.tvAnswers);
        tvAcceptedBy = findViewById(R.id.tvAcceptedBy);

        btnApply = findViewById(R.id.btnApply);
        btnEdit = findViewById(R.id.btnEdit);
        btnCancel = findViewById(R.id.btnCancel);
        btnAccept = findViewById(R.id.btnAccept);
        btnReject = findViewById(R.id.btnReject);
        workerActions = findViewById(R.id.workerActions);
        btnViewApplicants = findViewById(R.id.btnViewApplicants);
    }

    private void loadJobDetails() {
        db.collection("jobPostings")
                .document(jobId)
                .get()
                .addOnSuccessListener(this::bindData)
                .addOnFailureListener(e ->
                        Toast.makeText(this,
                                "Failed to load job",
                                Toast.LENGTH_SHORT).show());
    }

    private void bindData(DocumentSnapshot doc) {

        if (!doc.exists()) {
            Toast.makeText(this, "Job not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        this.jobDocument = doc; // Store the document

        String customerId = doc.getString("customerId");
        String assignedWorkerId = doc.getString("assignedWorkerId");
        String status = doc.getString("status");

        // ===== BASIC DETAILS =====
        tvJobTitle.setText(doc.getString("serviceTitle"));
        tvRecruiterName.setText("Customer: " + doc.getString("customerName"));
        tvLocation.setText("Location: " + doc.getString("locationName"));
        tvSkill.setText("Category: " + doc.getString("serviceCategory"));
        // Status will be set based on user role and application status

        Long scheduledTime = doc.getLong("scheduledTime");
        if (scheduledTime != null) {
            SimpleDateFormat sdf =
                    new SimpleDateFormat("MMM d, yyyy h:mm a",
                            Locale.getDefault());
            tvTimestamp.setText("Scheduled: "
                    + sdf.format(new Date(scheduledTime)));
        } else {
            tvTimestamp.setText("Scheduled: Not specified");
        }

        // ===== ANSWERS =====
        Map<String, Object> answers =
                (Map<String, Object>) doc.get("answers");

        if (answers != null && !answers.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, Object> entry : answers.entrySet()) {
                sb.append("• ")
                        .append(entry.getKey()
                                .replace("_", " ")
                                .toUpperCase())
                        .append("\n")
                        .append(entry.getValue())
                        .append("\n\n");
            }
            tvAnswers.setText(sb.toString());
        } else {
            tvAnswers.setText("No problem details provided.");
        }

        // ===== ASSIGNED WORKER =====
        String assignedWorkerName = doc.getString("assignedWorkerName");
        if (assignedWorkerName != null) {
            tvAcceptedBy.setVisibility(View.VISIBLE);
            tvAcceptedBy.setText("Assigned to: " + assignedWorkerName);
        }

        // ===== BUTTON VISIBILITY AND ACTIONS =====
        boolean isCustomer =
                currentUserId != null &&
                        currentUserId.equals(customerId);

        boolean isAssignedWorker =
                currentUserId != null &&
                        currentUserId.equals(assignedWorkerId);

        if (isCustomer) {
            // CUSTOMER VIEW
            tvStatus.setText("Status: " + status.toUpperCase());
            btnApply.setVisibility(View.GONE);
            workerActions.setVisibility(View.GONE);
            btnEdit.setVisibility(View.VISIBLE);

            if ("open".equalsIgnoreCase(status)) {
                btnCancel.setVisibility(View.VISIBLE);
                btnViewApplicants.setVisibility(View.VISIBLE);
                btnViewApplicants.setText("View Applicants");
                btnViewApplicants.setOnClickListener(v -> {
                    Intent intent = new Intent(JobDetailsActivity.this, ApplicantsActivity.class);
                    intent.putExtra("jobId", jobId);
                    startActivity(intent);
                });
            } else if ("accepted".equalsIgnoreCase(status)) {
                btnCancel.setVisibility(View.GONE);
                btnViewApplicants.setVisibility(View.VISIBLE);
                btnViewApplicants.setText("See Booking");
                btnViewApplicants.setOnClickListener(v -> {
                    Intent intent = new Intent(JobDetailsActivity.this, BookingDetailsActivity.class);
                    intent.putExtra("bookingId", jobId);
                    startActivity(intent);
                });
            } else {
                btnCancel.setVisibility(View.GONE);
                btnViewApplicants.setVisibility(View.GONE);
            }

        } else {
            // WORKER VIEW
            btnEdit.setVisibility(View.GONE);
            btnCancel.setVisibility(View.GONE);
            workerActions.setVisibility(View.GONE);
            btnApply.setVisibility(View.GONE);
            btnViewApplicants.setVisibility(View.GONE);

            if (isAssignedWorker && "assigned".equalsIgnoreCase(status)) {
                tvStatus.setText("Status: ASSIGNED");
                workerActions.setVisibility(View.VISIBLE);
            } else {
                 // Check if worker has applied
                db.collection("applications")
                    .whereEqualTo("jobId", jobId)
                    .whereEqualTo("workerId", currentUserId)
                    .limit(1)
                    .get()
                    .addOnSuccessListener(snapshot -> {
                        if (!snapshot.isEmpty()) {
                            // Already applied
                            tvStatus.setText("Status: APPLIED");
                            btnApply.setVisibility(View.VISIBLE);
                            btnApply.setEnabled(false);
                            btnApply.setText("Applied");
                        } else {
                            // Not applied, check if job is open
                            if ("open".equalsIgnoreCase(status)) {
                                 tvStatus.setText("Status: OPEN");
                                 btnApply.setVisibility(View.VISIBLE);
                            } else {
                                 tvStatus.setText("Status: " + status.toUpperCase());
                            }
                        }
                    });
            }
        }

        // ===== OTHER ACTIONS =====
        btnApply.setOnClickListener(v -> applyForJob());
        btnAccept.setOnClickListener(v -> acceptJob());
        btnReject.setOnClickListener(v -> rejectJob());

        btnEdit.setOnClickListener(v ->
                Toast.makeText(this,
                        "Edit job coming next",
                        Toast.LENGTH_SHORT).show());

        btnCancel.setOnClickListener(v ->
                Toast.makeText(this,
                        "Cancel job coming next",
                        Toast.LENGTH_SHORT).show());

    }

    private void applyForJob() {

        if (currentUserId == null) {
            Toast.makeText(this, "Login required", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("applications")
                .whereEqualTo("jobId", jobId)
                .whereEqualTo("workerId", currentUserId)
                .get()
                .addOnSuccessListener(snapshot -> {

                    if (!snapshot.isEmpty()) {
                        Toast.makeText(this,
                                "You already applied for this job",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Map<String, Object> application = new java.util.HashMap<>();
                    application.put("jobId", jobId);
                    application.put("workerId", currentUserId);
                    application.put("workerName",
                            FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                    application.put("workerSkill", tvSkill.getText().toString());
                    application.put("customerId", null); // filled later
                    application.put("status", "applied");
                    application.put("appliedAt", System.currentTimeMillis());

                    db.collection("applications")
                            .add(application)
                            .addOnSuccessListener(ref -> {
                                Toast.makeText(this,
                                        "Applied successfully",
                                        Toast.LENGTH_SHORT).show();
                                btnApply.setEnabled(false);
                                btnApply.setText("Applied");
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(this,
                                            "Failed to apply",
                                            Toast.LENGTH_SHORT).show());
                });
    }

    private void acceptJob() {
        if (jobDocument == null) {
            Toast.makeText(this, "Job data not loaded yet.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a new Booking object
        Booking booking = new Booking();
        booking.setJobId(jobDocument.getId());
        booking.setWorkerId(jobDocument.getString("assignedWorkerId"));
        booking.setCustomerId(jobDocument.getString("customerId"));
        booking.setWorkerName(jobDocument.getString("assignedWorkerName"));
        booking.setCustomerName(jobDocument.getString("customerName"));
        booking.setServiceTitle(jobDocument.getString("serviceTitle"));
        booking.setStatus("accepted");

        // Use a batch write to perform multiple operations atomically
        WriteBatch batch = db.batch();

        // 1. Update the job posting status to "accepted"
        DocumentReference jobRef = db.collection("jobPostings").document(jobId);
        batch.update(jobRef, "status", "accepted");

        // 2. Create a new document in the "bookings" collection
        DocumentReference bookingRef = db.collection("bookings").document(jobId);
        batch.set(bookingRef, booking);

        // Commit the batch
        batch.commit()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Job accepted and booking created", Toast.LENGTH_SHORT).show();
                    loadJobDetails(); // Refresh the UI to reflect changes
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to accept job", Toast.LENGTH_SHORT).show();
                    Log.e("JobDetailsActivity", "Error accepting job", e);
                });
    }

    private void rejectJob() {
        Map<String, Object> updates = new HashMap<>();
        updates.put("status", "open");
        updates.put("assignedWorkerId", FieldValue.delete());
        updates.put("assignedWorkerName", FieldValue.delete());
        updates.put("assignedAt", FieldValue.delete());

        db.collection("jobPostings").document(jobId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Job rejected", Toast.LENGTH_SHORT).show();
                    loadJobDetails(); // Refresh UI
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to reject job", Toast.LENGTH_SHORT).show());
    }
}
