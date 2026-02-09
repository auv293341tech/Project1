package com.example.project1;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class WorkerListActivity extends AppCompatActivity {

    private static final String TAG = "WorkerListActivity";

    private RecyclerView rvWorkers;
    private WorkerAdapter adapter;
    private List<Worker> workerList = new ArrayList<>();

    private TextView tvSkillTitle, tvAssignedInfo;
    private BottomNavigationView bottomNav;
    private Button btnGoToMyPostings;

    private FirebaseFirestore db;
    private String jobId;

    private boolean jobAlreadyAssigned = false;
    private String assignedWorkerId = null;
    private String assignedWorkerName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_list);

        jobId = getIntent().getStringExtra("jobId");
        if (jobId == null) {
            Toast.makeText(this, "Invalid job", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        rvWorkers = findViewById(R.id.rvWorkers);
        tvSkillTitle = findViewById(R.id.tvSkillTitle);
        tvAssignedInfo = findViewById(R.id.tvAssignedInfo);
        bottomNav = findViewById(R.id.bottomNav);
        btnGoToMyPostings = findViewById(R.id.btnGoToMyPostings);

        rvWorkers.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();

        btnGoToMyPostings.setOnClickListener(v -> {
            Intent intent = new Intent(WorkerListActivity.this, MyPostingsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });

        // Use the helper for navigation
        // Note: No item is selected as this is not a main tab screen
        BottomNavigationHelper.setupBottomNavigation(bottomNav, "recruiter", this, 0);

        loadJobDetails();
    }

    // ================= LOAD JOB =================
    private void loadJobDetails() {

        db.collection("jobPostings")
                .document(jobId)
                .get()
                .addOnSuccessListener(doc -> {

                    if (!doc.exists()) {
                        Toast.makeText(this, "Job not found", Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }

                    String skill = doc.getString("serviceCategory");
                    GeoPoint location = doc.getGeoPoint("location");
                    String status = doc.getString("status");

                    if (skill == null) {
                        Toast.makeText(this, "Job category not found", Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }

                    assignedWorkerId = doc.getString("assignedWorkerId");
                    assignedWorkerName = doc.getString("assignedWorkerName");

                    jobAlreadyAssigned =
                            assignedWorkerId != null &&
                                    status != null &&
                                    !status.equalsIgnoreCase("open");

                    tvSkillTitle.setText("Nearby " + skill + "s");

                    if (jobAlreadyAssigned && assignedWorkerName != null) {
                        tvAssignedInfo.setText("Assigned to: " + assignedWorkerName);
                    } else {
                        tvAssignedInfo.setText("Job not assigned yet");
                    }

                    if (location == null) {
                        Toast.makeText(this, "Job location missing", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    loadWorkers(
                            location.getLatitude(),
                            location.getLongitude(),
                            skill.toLowerCase()
                    );
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Job load failed", e);
                    Toast.makeText(this, "Failed to load job", Toast.LENGTH_SHORT).show();
                });
    }

    // ================= LOAD WORKERS =================
    private void loadWorkers(double jobLat, double jobLng, String skill) {

        db.collection("users")
                .whereEqualTo("role", "worker")
                .whereEqualTo("skill", skill)
                .get()
                .addOnSuccessListener(snapshot -> {

                    workerList.clear();

                    for (QueryDocumentSnapshot doc : snapshot) {

                        Worker worker = doc.toObject(Worker.class);
                        worker.setUid(doc.getId());

                        if (worker.getLatitude() == null || worker.getLongitude() == null)
                            continue;

                        float[] result = new float[1];
                        Location.distanceBetween(
                                jobLat,
                                jobLng,
                                worker.getLatitude(),
                                worker.getLongitude(),
                                result
                        );

                        double km = result[0] / 1000.0;
                        if (km > 15) continue;

                        worker.setDistance(km);
                        workerList.add(worker);
                    }

                    Collections.sort(workerList, new Comparator<Worker>() {
                        @Override
                        public int compare(Worker w1, Worker w2) {
                            return Double.compare(w1.getDistance(), w2.getDistance());
                        }
                    });

                    adapter = new WorkerAdapter(
                            this,
                            workerList,
                            jobAlreadyAssigned,
                            this::assignWorker
                    );

                    rvWorkers.setAdapter(adapter);

                    if (workerList.isEmpty()) {
                        Toast.makeText(
                                this,
                                "No workers available within 15 km",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Worker load failed", e);
                    Toast.makeText(this, "Failed to load workers", Toast.LENGTH_SHORT).show();
                });
    }

    // ================= ASSIGN WORKER =================
    private void assignWorker(Worker worker) {

        if (jobAlreadyAssigned) {
            Toast.makeText(this, "Job already assigned", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("jobPostings")
                .document(jobId)
                .update(
                        "assignedWorkerId", worker.getUid(),
                        "assignedWorkerName", worker.getName(),
                        "status", "assigned"
                )
                .addOnSuccessListener(unused -> {
                    Toast.makeText(
                            this,
                            "Worker assigned. Waiting for acceptance.",
                            Toast.LENGTH_SHORT
                    ).show();
                    Intent intent = new Intent(WorkerListActivity.this, MyPostingsActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Assignment failed", Toast.LENGTH_SHORT).show());
    }
}
