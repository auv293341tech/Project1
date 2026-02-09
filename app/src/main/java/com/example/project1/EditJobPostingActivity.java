package com.example.project1;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

public class EditJobPostingActivity extends AppCompatActivity {

    private EditText etJobTitle, etLocationName;
    private Button btnUpdateJob;

    private FirebaseFirestore db;
    private String jobId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_job_posting);

        db = FirebaseFirestore.getInstance();

        etJobTitle = findViewById(R.id.etJobTitle);
        etLocationName = findViewById(R.id.etLocationName);
        btnUpdateJob = findViewById(R.id.btnUpdateJob);

        jobId = getIntent().getStringExtra("jobId");

        if (jobId == null) {
            Toast.makeText(this, "Invalid job", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadJobDetails();

        btnUpdateJob.setOnClickListener(v -> updateJob());
    }

    // ================= LOAD JOB =================
    private void loadJobDetails() {
        db.collection("jobPostings")
                .document(jobId)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (!snapshot.exists()) {
                        Toast.makeText(this, "Job not found", Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }

                    JobPosting posting = snapshot.toObject(JobPosting.class);
                    if (posting == null) return;

                    // ✅ FIXED: serviceTitle instead of title
                    etJobTitle.setText(posting.getServiceTitle());
                    etLocationName.setText(posting.getLocationName());
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this,
                                "Failed to load job",
                                Toast.LENGTH_SHORT).show());
    }

    // ================= UPDATE JOB =================
    private void updateJob() {

        String serviceTitle = etJobTitle.getText().toString().trim();
        String locationName = etLocationName.getText().toString().trim();

        if (serviceTitle.isEmpty()) {
            etJobTitle.setError("Required");
            return;
        }

        db.collection("jobPostings")
                .document(jobId)
                .update(
                        "serviceTitle", serviceTitle,
                        "locationName", locationName
                )
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this,
                            "Job updated successfully",
                            Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this,
                                "Failed to update job",
                                Toast.LENGTH_SHORT).show());
    }
}
