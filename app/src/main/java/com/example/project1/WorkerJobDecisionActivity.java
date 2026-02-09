package com.example.project1;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

public class WorkerJobDecisionActivity extends AppCompatActivity {

    private TextView tvJobTitle, tvJobAddress;
    private Button btnAccept, btnReject;

    private FirebaseFirestore db;
    private String jobId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_job_decision);

        jobId = getIntent().getStringExtra("jobId");
        if (jobId == null) {
            finish();
            return;
        }

        db = FirebaseFirestore.getInstance();

        tvJobTitle = findViewById(R.id.tvJobTitle);
        tvJobAddress = findViewById(R.id.tvJobAddress);
        btnAccept = findViewById(R.id.btnAccept);
        btnReject = findViewById(R.id.btnReject);

        loadJob();

        btnAccept.setOnClickListener(v -> acceptJob());
        btnReject.setOnClickListener(v -> rejectJob());
    }

    private void loadJob() {
        db.collection("jobPostings")
                .document(jobId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) return;

                    tvJobTitle.setText(doc.getString("serviceCategory"));
                    tvJobAddress.setText(doc.getString("address"));
                });
    }

    private void acceptJob() {
        db.collection("jobPostings")
                .document(jobId)
                .update("status", "accepted")
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Job accepted", Toast.LENGTH_SHORT).show();
                    finish();
                });
    }

    private void rejectJob() {
        db.collection("jobPostings")
                .document(jobId)
                .update(
                        "status", "open",
                        "assignedWorkerId", null,
                        "assignedWorkerName", null
                )
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Job rejected", Toast.LENGTH_SHORT).show();
                    finish();
                });
    }
}