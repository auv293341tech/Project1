package com.example.project1;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AppliedJobsFragment extends Fragment {

    private static final String TAG = "AppliedJobsFragment";

    private RecyclerView rvAppliedJobs;
    private JobPostingAdapter adapter;
    private List<JobPosting> appliedJobs;
    private FirebaseFirestore db;
    private String currentUserId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_applied_jobs, container, false);

        rvAppliedJobs = view.findViewById(R.id.rvAppliedJobs);
        rvAppliedJobs.setLayoutManager(new LinearLayoutManager(getContext()));

        appliedJobs = new ArrayList<>();
        adapter = new JobPostingAdapter(getContext(), appliedJobs);
        rvAppliedJobs.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        loadAppliedJobs();

        return view;
    }

    private void loadAppliedJobs() {
        db.collection("applications")
                .whereEqualTo("workerId", currentUserId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    appliedJobs.clear();
                    for (DocumentSnapshot applicationDoc : queryDocumentSnapshots.getDocuments()) {
                        String jobId = applicationDoc.getString("jobId");
                        if (jobId != null) {
                            db.collection("jobPostings").document(jobId).get().addOnSuccessListener(jobDoc -> {
                                if (jobDoc.exists()) {
                                    JobPosting job = jobDoc.toObject(JobPosting.class);
                                    job.setDocumentId(jobDoc.getId());
                                    appliedJobs.add(job);
                                    adapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, "Error getting documents: ", e);
                });
    }
}
