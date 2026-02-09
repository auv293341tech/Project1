package com.example.project1;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ApplicantsActivity extends AppCompatActivity {

    private RecyclerView rvApplicants;
    private ApplicantAdapter adapter;
    private List<Application> applicationList = new ArrayList<>();

    private FirebaseFirestore db;
    private String jobId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applicants);

        jobId = getIntent().getStringExtra("jobId");
        if (jobId == null) {
            Toast.makeText(this, "Invalid job", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        rvApplicants = findViewById(R.id.rvApplicants);
        rvApplicants.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ApplicantAdapter(
                this,
                applicationList,
                this::assignWorker
        );
        rvApplicants.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        loadApplicants();
    }

    private void loadApplicants() {
        db.collection("applications")
                .whereEqualTo("jobId", jobId)
                .whereEqualTo("status", "applied")
                .get()
                .addOnSuccessListener(snapshot -> {
                    applicationList.clear();
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        Application app = doc.toObject(Application.class);
                        app.setDocumentId(doc.getId());
                        
                        // Fetch worker's name from the 'users' collection
                        db.collection("users").document(app.getWorkerId()).get().addOnSuccessListener(userDoc -> {
                            if (userDoc.exists()) {
                                app.setWorkerName(userDoc.getString("name"));
                                adapter.notifyDataSetChanged(); // Update the UI as names are fetched
                            }
                        });
                        
                        applicationList.add(app);
                    }
                    adapter.notifyDataSetChanged(); // Initial notification
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this,
                                "Failed to load applicants",
                                Toast.LENGTH_SHORT).show());
    }

    private void assignWorker(Application application) {
        // 🔥 STEP 3 will handle this
        Toast.makeText(this,
                "Assign flow coming next",
                Toast.LENGTH_SHORT).show();
    }
}
