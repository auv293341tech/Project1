package com.example.project1;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ScheduleSlotActivity extends AppCompatActivity {

    private DatePicker datePicker;
    private TimePicker timePicker;
    private Button btnFindWorkers;

    private String skill, serviceId, serviceTitle;
    private HashMap<String, Object> answers;

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_slot);

        datePicker = findViewById(R.id.datePicker);
        timePicker = findViewById(R.id.timePicker);
        btnFindWorkers = findViewById(R.id.btnFindWorkers);

        timePicker.setIs24HourView(false);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        skill = getIntent().getStringExtra("skill");
        serviceId = getIntent().getStringExtra("serviceId");
        serviceTitle = getIntent().getStringExtra("serviceTitle");
        answers = (HashMap<String, Object>) getIntent()
                .getSerializableExtra("answers");

        btnFindWorkers.setOnClickListener(v -> createJobPosting());
    }

    private void createJobPosting() {

        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "Please login again", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = auth.getUid();

        Calendar calendar = Calendar.getInstance();
        calendar.set(
                datePicker.getYear(),
                datePicker.getMonth(),
                datePicker.getDayOfMonth(),
                timePicker.getHour(),
                timePicker.getMinute()
        );

        long scheduledTimeMillis = calendar.getTimeInMillis();

        db.collection("users").document(uid).get()
                .addOnSuccessListener(userDoc -> {

                    if (!userDoc.exists()) {
                        Toast.makeText(this,
                                "User profile not found. Please set location.",
                                Toast.LENGTH_LONG).show();
                        return;
                    }

                    String customerName = userDoc.getString("name");
                    String address = userDoc.getString("address");

                    Double lat = userDoc.getDouble("latitude");
                    Double lng = userDoc.getDouble("longitude");

                    if (lat == null || lng == null || address == null) {
                        Toast.makeText(this,
                                "Please set your location first",
                                Toast.LENGTH_LONG).show();
                        return;
                    }

                    GeoPoint locationGeoPoint = new GeoPoint(lat, lng);

                    Map<String, Object> job = new HashMap<>();

                    job.put("serviceTitle", serviceTitle);
                    job.put("serviceCategory", skill);
                    job.put("serviceId", serviceId);
                    job.put("customerId", uid);
                    job.put("customerName",
                            customerName != null ? customerName : "Customer");

                    job.put("locationName", address);
                    job.put("location", locationGeoPoint);

                    job.put("scheduledTime", scheduledTimeMillis);
                    job.put("status", "open");

                    job.put("answers", answers);
                    job.put("createdAt", System.currentTimeMillis());

                    db.collection("jobPostings")
                            .add(job)
                            .addOnSuccessListener(docRef -> {

                                String jobId = docRef.getId();

                                Toast.makeText(this,
                                        "Service request created",
                                        Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(
                                        ScheduleSlotActivity.this,
                                        WorkerListActivity.class
                                );
                                intent.putExtra("jobId", jobId);
                                startActivity(intent);
                                finish();
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(this,
                                            "Failed to create job",
                                            Toast.LENGTH_SHORT).show());
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this,
                                "Failed to fetch user profile",
                                Toast.LENGTH_SHORT).show());
    }
}
