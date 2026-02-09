package com.example.project1;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class WorkerJobDetailsActivity extends AppCompatActivity {

    private TextView tvJobTitle, tvJobDescription, tvDateTime, tvRecruiterName;
    private Button btnAcceptJob;

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private JobPosting jobPosting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_job_details);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        jobPosting = (JobPosting) getIntent().getSerializableExtra("jobPosting");

        tvJobTitle = findViewById(R.id.tvJobTitle);
        tvJobDescription = findViewById(R.id.tvJobDescription);
        tvDateTime = findViewById(R.id.tvDateTime);
        tvRecruiterName = findViewById(R.id.tvRecruiterName);
        btnAcceptJob = findViewById(R.id.btnAcceptJob);

        if (jobPosting != null) {
            tvJobTitle.setText(jobPosting.getServiceTitle());
            tvJobDescription.setText(jobPosting.getLocationName());
            tvRecruiterName.setText("Posted by: " + jobPosting.getCustomerName());

            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault());
            String formattedDate = sdf.format(new Date(jobPosting.getCreatedAt()));
            tvDateTime.setText(formattedDate);
        }

        btnAcceptJob.setOnClickListener(v -> acceptJob());
    }

    private void acceptJob() {
        if (jobPosting == null || auth.getCurrentUser() == null) {
            Toast.makeText(this, "Error: Not logged in or job not found.", Toast.LENGTH_SHORT).show();
            return;
        }

        btnAcceptJob.setEnabled(false); // Disable button to prevent multiple clicks

        DocumentReference jobRef = db.collection("job_postings").document(jobPosting.getDocumentId());
        String workerId = auth.getCurrentUser().getUid();

        db.runTransaction((Transaction.Function<Void>) transaction -> {
            JobPosting currentJob = transaction.get(jobRef).toObject(JobPosting.class);

            if (currentJob != null && "open".equals(currentJob.getStatus())) {
                transaction.update(jobRef, "status", "accepted");
                transaction.update(jobRef, "acceptedBy", workerId);
                return null;
            } else {
                throw new FirebaseFirestoreException("Job is no longer available.", FirebaseFirestoreException.Code.ABORTED);
            }
        }).addOnSuccessListener(aVoid -> {
            Toast.makeText(WorkerJobDetailsActivity.this, "Job accepted!", Toast.LENGTH_SHORT).show();
            finish();
        }).addOnFailureListener(e -> {
            Toast.makeText(WorkerJobDetailsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            btnAcceptJob.setEnabled(true); // Re-enable button on failure
            finish(); // Go back as the job is not available
        });
    }
}
