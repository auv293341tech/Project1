package com.example.project1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.*;

import java.util.ArrayList;
import java.util.List;

public class WorkerHomeActivity extends AppCompatActivity {

    private static final String TAG = "WorkerHomeActivity";

    private TextView tvUserName;
    private View layoutEmptyState;
    private RecyclerView rvJobPostings;
    private JobPostingAdapter adapter;
    private List<JobPosting> jobPostings;

    private BottomNavigationView bottomNavigationView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Button btnGoPremium;
    private ImageView ivNotifications;

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private ListenerRegistration jobListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_home);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        tvUserName = findViewById(R.id.tvUserName);
        layoutEmptyState = findViewById(R.id.layoutEmptyState);
        rvJobPostings = findViewById(R.id.rvJobPostings);
        bottomNavigationView = findViewById(R.id.bottomNav);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        btnGoPremium = findViewById(R.id.btnGoPremium);
        ivNotifications = findViewById(R.id.ivNotifications);

        setupRecyclerView();

        BottomNavigationHelper.setupBottomNavigation(bottomNavigationView, "worker", this, R.id.nav_home);

        swipeRefreshLayout.setOnRefreshListener(this::loadWorker);

        btnGoPremium.setOnClickListener(v ->
                startActivity(new Intent(this, PremiumActivity.class)));

        ivNotifications.setOnClickListener(v -> {
            Intent intent = new Intent(WorkerHomeActivity.this, NotificationsActivity.class);
            startActivity(intent);
        });

        loadWorker();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh jobs when returning to the activity
        listenForOpenJobs();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (jobListener != null) jobListener.remove();
    }

    private void loadWorker() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) return;

        db.collection("users")
                .document(user.getUid())
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        String firstName = doc.getString("firstName");
                        if (firstName != null && !firstName.isEmpty()) {
                            tvUserName.setText(firstName + " 👋");
                        } else if (user.getEmail() != null) {
                            tvUserName.setText(user.getEmail().split("@")[0] + " 👋");
                        }
                    }
                    // Initial load of jobs
                    listenForOpenJobs();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to load worker", e);
                    listenForOpenJobs(); // Still try to load jobs
                });
    }

    private void listenForOpenJobs() {
        if (jobListener != null) jobListener.remove();

        String currentUserId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;
        if (currentUserId == null) return; // Not logged in

        // 1. Get the list of job IDs the worker has applied to
        db.collection("applications")
                .whereEqualTo("workerId", currentUserId)
                .get()
                .addOnSuccessListener(applicationSnapshot -> {
                    List<String> appliedJobIds = new ArrayList<>();
                    for (DocumentSnapshot appDoc : applicationSnapshot.getDocuments()) {
                        String jobId = appDoc.getString("jobId");
                        if (jobId != null) {
                            appliedJobIds.add(jobId);
                        }
                    }

                    // 2. Listen for open jobs and filter out the applied ones
                    jobListener = db.collection("jobPostings")
                            .whereEqualTo("status", "open")
                            .addSnapshotListener((jobSnapshot, error) -> {
                                swipeRefreshLayout.setRefreshing(false);
                                if (error != null) {
                                    Log.e(TAG, "Job listener failed", error);
                                    return;
                                }

                                jobPostings.clear();
                                if (jobSnapshot != null) {
                                    for (DocumentSnapshot jobDoc : jobSnapshot) {
                                        // 3. Filter check
                                        if (!appliedJobIds.contains(jobDoc.getId())) {
                                            JobPosting job = jobDoc.toObject(JobPosting.class);
                                            if (job != null) {
                                                job.setDocumentId(jobDoc.getId());
                                                jobPostings.add(job);
                                            }
                                        }
                                    }
                                }

                                adapter.notifyDataSetChanged();

                                if (jobPostings.isEmpty()) {
                                    rvJobPostings.setVisibility(View.GONE);
                                    layoutEmptyState.setVisibility(View.VISIBLE);
                                } else {
                                    rvJobPostings.setVisibility(View.VISIBLE);
                                    layoutEmptyState.setVisibility(View.GONE);
                                }
                                Log.d(TAG, "Displaying open jobs: " + jobPostings.size());
                            });
                })
                .addOnFailureListener(e -> {
                    swipeRefreshLayout.setRefreshing(false);
                    Log.e(TAG, "Failed to load applications to filter home screen", e);
                });
    }

    private void setupRecyclerView() {
        jobPostings = new ArrayList<>();
        adapter = new JobPostingAdapter(this, jobPostings);
        rvJobPostings.setLayoutManager(new LinearLayoutManager(this));
        rvJobPostings.setAdapter(adapter);
    }
}
