package com.example.project1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyBookingsActivity extends AppCompatActivity {

    private static final String TAG = "MyBookingsActivity";

    private RecyclerView rvMyBookings;
    private BookingAdapter adapter;
    private BottomNavigationView bottomNavigationView;

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private String userRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bookings);

        rvMyBookings = findViewById(R.id.rvMyBookings);
        bottomNavigationView = findViewById(R.id.bottomNav);
        rvMyBookings.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        checkUserRole();
    }

    private void checkUserRole() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            finish(); // Not logged in
            return;
        }

        db.collection("users").document(currentUser.getUid()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        userRole = documentSnapshot.getString("role");
                        // Initialize adapter now that we have the role
                        adapter = new BookingAdapter(new ArrayList<>(), new ArrayList<>(), this, userRole);
                        rvMyBookings.setAdapter(adapter);
                        BottomNavigationHelper.setupBottomNavigation(bottomNavigationView, userRole, this, R.id.nav_my_bookings);
                        loadMyBookings();
                    } else {
                        Toast.makeText(this, "User role not found.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to get user role.", Toast.LENGTH_SHORT).show();
                });
    }

    public String getUserRole() {
        return userRole;
    }

    private void loadMyBookings() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null || userRole == null) return;

        String queryField = "recruiter".equals(userRole) ? "customerId" : "workerId";

        db.collection("bookings")
                .whereEqualTo(queryField, currentUser.getUid())
                .get()
                .addOnSuccessListener(bookingSnapshot -> {
                    if (bookingSnapshot.isEmpty()) {
                        Toast.makeText(this, "No bookings yet", Toast.LENGTH_SHORT).show();
                        adapter.setBookings(new ArrayList<>(), new ArrayList<>());
                        return;
                    }

                    List<Booking> tempBookings = new ArrayList<>();
                    List<String> jobIds = new ArrayList<>();

                    for (QueryDocumentSnapshot doc : bookingSnapshot) {
                        Booking booking = doc.toObject(Booking.class);
                        booking.setDocumentId(doc.getId());
                        if (booking.getJobId() != null && !booking.getJobId().isEmpty()) {
                            tempBookings.add(booking);
                            jobIds.add(booking.getJobId());
                        }
                    }

                    if (jobIds.isEmpty()) {
                        adapter.setBookings(new ArrayList<>(), new ArrayList<>());
                        return;
                    }

                    db.collection("jobPostings").whereIn(FieldPath.documentId(), jobIds).get()
                        .addOnSuccessListener(jobPostingSnapshot -> {
                            Map<String, JobPosting> jobPostingMap = new HashMap<>();
                            for (DocumentSnapshot doc : jobPostingSnapshot.getDocuments()) {
                                JobPosting jp = doc.toObject(JobPosting.class);
                                if (jp != null) {
                                    jobPostingMap.put(doc.getId(), jp);
                                }
                            }

                            List<Booking> finalBookingList = new ArrayList<>();
                            List<JobPosting> finalJobPostingList = new ArrayList<>();
                            for (Booking booking : tempBookings) {
                                JobPosting correspondingJob = jobPostingMap.get(booking.getJobId());
                                if (correspondingJob != null) {
                                    finalBookingList.add(booking);
                                    finalJobPostingList.add(correspondingJob);
                                }
                            }
                            
                            adapter.setBookings(finalBookingList, finalJobPostingList);
                        })
                        .addOnFailureListener(e -> {
                             Log.e(TAG, "Failed to load job postings for bookings", e);
                             Toast.makeText(this, "Failed to load booking details.", Toast.LENGTH_SHORT).show();
                        });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to load bookings", e);
                    Toast.makeText(this, "Failed to load bookings", Toast.LENGTH_SHORT).show();
                });
    }

    public void markBookingCompleted(Booking booking) {
        // This method is now handled inside BookingDetailsActivity
    }
}
