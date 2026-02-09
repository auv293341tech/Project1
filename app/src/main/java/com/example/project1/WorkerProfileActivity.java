package com.example.project1;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class WorkerProfileActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private DocumentReference workerRef;

    private ImageView ivWorkerProfile;
    private TextView tvWorkerName, tvWorkerSkill;
    private RatingBar ratingBar;
    private Button btnContact, btnHire;
    private RecyclerView rvReviews;

    private ReviewAdapter reviewAdapter;
    private List<Review> reviewList;

    private String workerUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_profile);

        db = FirebaseFirestore.getInstance();

        // Views
        ivWorkerProfile = findViewById(R.id.ivWorkerProfile);
        tvWorkerName = findViewById(R.id.tvWorkerName);
        tvWorkerSkill = findViewById(R.id.tvWorkerSkill);
        ratingBar = findViewById(R.id.ratingBar);
        btnContact = findViewById(R.id.btnContact);
        btnHire = findViewById(R.id.btnHire);
        rvReviews = findViewById(R.id.rvReviews);

        workerUid = getIntent().getStringExtra("workerUid");
        if (workerUid == null) {
            Toast.makeText(this, "Invalid worker", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        workerRef = db.collection("users").document(workerUid);

        setupRecyclerView();
        loadWorkerDetails();
        loadReviews();

        // ---------------- BUTTON ACTIONS ----------------

        btnContact.setOnClickListener(v ->
                Toast.makeText(
                        this,
                        "Contact feature coming soon",
                        Toast.LENGTH_SHORT
                ).show()
        );

        btnHire.setOnClickListener(v ->
                Toast.makeText(
                        this,
                        "Go to My Postings → View Applicants to assign this worker",
                        Toast.LENGTH_LONG
                ).show()
        );
    }

    // ---------------- REVIEWS LIST ----------------

    private void setupRecyclerView() {
        reviewList = new ArrayList<>();
        reviewAdapter = new ReviewAdapter(reviewList);
        rvReviews.setLayoutManager(new LinearLayoutManager(this));
        rvReviews.setAdapter(reviewAdapter);
    }

    // ---------------- WORKER DETAILS ----------------

    private void loadWorkerDetails() {
        workerRef.get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) {
                        Toast.makeText(this, "Worker not found", Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }

                    tvWorkerName.setText(doc.getString("name"));
                    tvWorkerSkill.setText(doc.getString("skill"));

                    Double rating = doc.getDouble("rating");
                    if (rating != null) {
                        ratingBar.setRating(rating.floatValue());
                    } else {
                        ratingBar.setRating(0f);
                    }

                    String imageUrl = doc.getString("profileImageUrl");
                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        Glide.with(this)
                                .load(imageUrl)
                                .placeholder(R.drawable.ic_account)
                                .error(R.drawable.ic_account)
                                .into(ivWorkerProfile);
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(
                                this,
                                "Failed to load worker details",
                                Toast.LENGTH_SHORT
                        ).show()
                );
    }

    // ---------------- LOAD REVIEWS ----------------

    private void loadReviews() {
        workerRef.collection("reviews")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(snapshot -> {
                    reviewList.clear();
                    reviewList.addAll(snapshot.toObjects(Review.class));
                    reviewAdapter.notifyDataSetChanged();
                });
    }
}
