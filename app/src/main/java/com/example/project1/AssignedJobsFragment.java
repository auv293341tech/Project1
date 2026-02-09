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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AssignedJobsFragment extends Fragment {

    private static final String TAG = "AssignedJobsFragment";

    private RecyclerView rvAssignedJobs;
    private JobPostingAdapter adapter;
    private List<JobPosting> assignedJobs;
    private FirebaseFirestore db;
    private String currentUserId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_assigned_jobs, container, false);

        rvAssignedJobs = view.findViewById(R.id.rvAssignedJobs);
        rvAssignedJobs.setLayoutManager(new LinearLayoutManager(getContext()));

        assignedJobs = new ArrayList<>();
        adapter = new JobPostingAdapter(getContext(), assignedJobs);
        rvAssignedJobs.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        loadAssignedJobs();

        return view;
    }

    private void loadAssignedJobs() {
        db.collection("jobPostings")
                .whereEqualTo("assignedWorkerId", currentUserId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        assignedJobs.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            JobPosting job = document.toObject(JobPosting.class);
                            job.setDocumentId(document.getId());
                            assignedJobs.add(job);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }
}
