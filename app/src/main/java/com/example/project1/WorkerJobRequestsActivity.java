package com.example.project1;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class WorkerJobRequestsActivity extends AppCompatActivity {

    private RecyclerView rvRequests;
    private WorkerJobRequestAdapter adapter;
    private final List<JobPosting> jobs = new ArrayList<>();

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_job_requests);

        rvRequests = findViewById(R.id.rvJobRequests);
        rvRequests.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        adapter = new WorkerJobRequestAdapter(
                jobs,
                this::acceptJob,
                this::rejectJob
        );
        rvRequests.setAdapter(adapter);

        loadRequests();
    }

    private void loadRequests() {
        String workerId = auth.getUid();
        if (workerId == null) return;

        db.collection("jobPostings")
                .whereEqualTo("assignedWorkerId", workerId)
                .whereEqualTo("status", "assigned")
                .get()
                .addOnSuccessListener(snapshot -> {
                    jobs.clear();
                    for (QueryDocumentSnapshot doc : snapshot) {
                        JobPosting job = doc.toObject(JobPosting.class);
                        job.setDocumentId(doc.getId());
                        jobs.add(job);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this,
                                "Failed to load job requests",
                                Toast.LENGTH_SHORT).show());
    }

    private void acceptJob(JobPosting job) {

        db.collection("jobPostings")
                .document(job.getDocumentId())
                .update("status", "confirmed")
                .addOnSuccessListener(unused -> {

                    Booking booking = new Booking(
                            job.getDocumentId(),
                            job.getCustomerId(),
                            auth.getUid(),
                            job.getServiceTitle(),
                            job.getLocationName(),
                            job.getCustomerName(),
                            job.getScheduledTime(),
                            "confirmed"
                    );

                    db.collection("bookings").add(booking);

                    Toast.makeText(this,
                            "Job accepted",
                            Toast.LENGTH_SHORT).show();

                    loadRequests();
                });
    }

    private void rejectJob(JobPosting job) {

        db.collection("jobPostings")
                .document(job.getDocumentId())
                .update(
                        "assignedWorkerId", null,
                        "status", "open"
                )
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this,
                            "Job rejected",
                            Toast.LENGTH_SHORT).show();
                    loadRequests();
                });
    }
}
