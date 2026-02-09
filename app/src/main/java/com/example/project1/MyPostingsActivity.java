package com.example.project1;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MyPostingsActivity extends AppCompatActivity {

    private RecyclerView rvMyPostings;
    private MyPostingsAdapter adapter;
    private List<JobPosting> jobList;
    private TextView tvActivePostingsCount;

    private FirebaseFirestore db;
    private String customerId;

    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_postings);

        // ---------- Views ----------
        rvMyPostings = findViewById(R.id.rvMyPostings);
        bottomNav = findViewById(R.id.bottomNav);
        tvActivePostingsCount = findViewById(R.id.tvActivePostingsCount);

        rvMyPostings.setLayoutManager(new LinearLayoutManager(this));

        // ---------- Firebase ----------
        db = FirebaseFirestore.getInstance();
        customerId = FirebaseAuth.getInstance().getUid();

        // ---------- Adapter ----------
        jobList = new ArrayList<>();
        adapter = new MyPostingsAdapter(this, jobList);
        rvMyPostings.setAdapter(adapter);

        BottomNavigationHelper.setupBottomNavigation(bottomNav, "recruiter", this, R.id.nav_my_postings);
        loadMyPostings();
    }

    // ================= LOAD DATA =================
    private void loadMyPostings() {
        if (customerId == null) return;

        db.collection("jobPostings")
                .whereEqualTo("customerId", customerId)
                .get()
                .addOnSuccessListener(snapshot -> {
                    jobList.clear();
                    int activePostings = 0;

                    for (JobPosting doc : snapshot.toObjects(JobPosting.class)) {
                        jobList.add(doc);
                        String status = doc.getStatus();
                        if (status != null && !status.equals("Completed") && !status.equals("Cancelled")) {
                            activePostings++;
                        }
                    }
                    tvActivePostingsCount.setText(String.valueOf(activePostings));
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(
                                this,
                                "Failed to load postings",
                                Toast.LENGTH_SHORT
                        ).show());
    }
}
