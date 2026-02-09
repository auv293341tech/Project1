package com.example.project1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ReviewPostingActivity extends AppCompatActivity {

    private TextView tvServiceTitle, tvDate, tvTime, tvAnswers, tvStepTitle, serviceTitle, serviceCategory;
    private Button btnFindWorkers;
    private ImageView ivServiceIcon, servicePng;
    private LinearLayout headerLayout, reviewIconLayout;
    private View step1, step2, step3, step4;
    private CardView reviewIconCard;

    private String skill, serviceId, serviceTitleExtra, timeSlot;
    private HashMap<String, Object> answers;
    private long date;

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_review_posting);

        skill = getIntent().getStringExtra("skill");
        serviceId = getIntent().getStringExtra("serviceId");
        serviceTitleExtra = getIntent().getStringExtra("serviceTitle");
        answers = (HashMap<String, Object>) getIntent().getSerializableExtra("answers");
        date = getIntent().getLongExtra("date", 0);
        timeSlot = getIntent().getStringExtra("timeSlot");

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        tvServiceTitle = findViewById(R.id.tvServiceTitle);
        tvStepTitle = findViewById(R.id.tvStepTitle);
        tvDate = findViewById(R.id.tvDate);
        tvTime = findViewById(R.id.tvTime);
        tvAnswers = findViewById(R.id.tvAnswers);
        btnFindWorkers = findViewById(R.id.btnFindWorkers);
        ivServiceIcon = findViewById(R.id.ivServiceIcon);
        headerLayout = findViewById(R.id.headerLayout);
        reviewIconLayout = findViewById(R.id.reviewIconLayout);
        step1 = findViewById(R.id.step1);
        step2 = findViewById(R.id.step2);
        step3 = findViewById(R.id.step3);
        step4 = findViewById(R.id.step4);
        reviewIconCard = findViewById(R.id.reviewIconCard);
        servicePng = findViewById(R.id.servicePng);
        serviceTitle = findViewById(R.id.serviceTitle);
        serviceCategory = findViewById(R.id.serviceCategory);

        final int initialPaddingTop = headerLayout.getPaddingTop();
        final int initialPaddingBottom = headerLayout.getPaddingBottom();
        final int initialPaddingLeft = headerLayout.getPaddingLeft();
        final int initialPaddingRight = headerLayout.getPaddingRight();

        ViewCompat.setOnApplyWindowInsetsListener(headerLayout, (v, insets) -> {
            int topInset = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top;
            v.setPadding(initialPaddingLeft, initialPaddingTop + topInset, initialPaddingRight, initialPaddingBottom);
            return WindowInsetsCompat.CONSUMED;
        });

        ViewCompat.setOnApplyWindowInsetsListener(btnFindWorkers, (v, insets) -> {
            int bottomInset = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom;
            v.setPadding(v.getPaddingLeft(), v.getPaddingTop(), v.getPaddingRight(), bottomInset + v.getPaddingBottom());
            return insets;
        });

        tvServiceTitle.setText(serviceTitleExtra);
        serviceTitle.setText(serviceTitleExtra);
        serviceCategory.setText(skill);
        setServiceTheme();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);

        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.getDefault());
        tvDate.setText(dateFormat.format(calendar.getTime()));

        if (timeSlot != null) {
            tvTime.setText(timeSlot);
        } else {
            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            tvTime.setText(timeFormat.format(calendar.getTime()));
        }

        StringBuilder answersText = new StringBuilder();
        for (Map.Entry<String, Object> entry : answers.entrySet()) {
            answersText.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        tvAnswers.setText(answersText.toString());

        btnFindWorkers.setOnClickListener(v -> createJobPosting());

        Animation scaleUpDown = AnimationUtils.loadAnimation(this, R.anim.scale_up_down);
        reviewIconCard.startAnimation(scaleUpDown);
    }

    private void createJobPosting() {
        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "Please login again", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = auth.getUid();

        db.collection("users").document(uid).get()
                .addOnSuccessListener(userDoc -> {
                    if (!userDoc.exists()) {
                        Toast.makeText(this, "User profile not found. Please set location.", Toast.LENGTH_LONG).show();
                        return;
                    }

                    String customerName = userDoc.getString("name");
                    String address = userDoc.getString("address");
                    Double lat = userDoc.getDouble("latitude");
                    Double lng = userDoc.getDouble("longitude");

                    if (lat == null || lng == null || address == null) {
                        Toast.makeText(this, "Please set your location first", Toast.LENGTH_LONG).show();
                        return;
                    }

                    GeoPoint locationGeoPoint = new GeoPoint(lat, lng);

                    Map<String, Object> job = new HashMap<>();
                    job.put("serviceTitle", serviceTitleExtra);
                    job.put("serviceCategory", skill);
                    job.put("serviceId", serviceId);
                    job.put("customerId", uid);
                    job.put("customerName", customerName != null ? customerName : "Customer");
                    job.put("locationName", address);
                    job.put("location", locationGeoPoint);
                    job.put("scheduledTime", date);
                    job.put("timeSlot", timeSlot);
                    job.put("status", "open");
                    job.put("answers", answers);
                    job.put("createdAt", System.currentTimeMillis());

                    db.collection("jobPostings")
                            .add(job)
                            .addOnSuccessListener(docRef -> {
                                String jobId = docRef.getId();
                                Toast.makeText(this, "Service request created", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(ReviewPostingActivity.this, WorkerListActivity.class);
                                intent.putExtra("jobId", jobId);
                                startActivity(intent);
                                finish();
                            })
                            .addOnFailureListener(e -> Toast.makeText(this, "Failed to create job", Toast.LENGTH_SHORT).show());
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to fetch user profile", Toast.LENGTH_SHORT).show());
    }

    private void setServiceTheme() {
        if (skill == null || skill.isEmpty()) {
            return;
        }

        int iconResId = 0;
        int gradientResId = 0;
        int statusBarColorResId = 0;

        switch (skill.toLowerCase()) {
            case "electrician":
                iconResId = R.drawable.ic_electrician;
                gradientResId = R.drawable.bg_orange_gradient;
                statusBarColorResId = R.color.orange_dark;
                break;
            case "plumber":
                iconResId = R.drawable.ic_plumber;
                gradientResId = R.drawable.bg_blue_gradient;
                statusBarColorResId = R.color.blue_dark;
                break;
            case "carpenter":
                iconResId = R.drawable.ic_carpenter;
                gradientResId = R.drawable.bg_purple_gradient;
                statusBarColorResId = R.color.purple_dark;
                break;
            case "painter":
                iconResId = R.drawable.ic_painter;
                gradientResId = R.drawable.bg_red_gradient;
                statusBarColorResId = R.color.red_dark;
                break;
            case "welder":
                iconResId = R.drawable.ic_welder;
                gradientResId = R.drawable.bg_green_gradient;
                statusBarColorResId = R.color.green_dark;
                break;
            case "cleaner":
                iconResId = R.drawable.ic_cleaner;
                gradientResId = R.drawable.bg_teal_gradient;
                statusBarColorResId = R.color.teal_dark;
                break;
            case "ac repair":
                iconResId = R.drawable.ic_ac;
                gradientResId = R.drawable.bg_indigo_gradient;
                statusBarColorResId = R.color.indigo_dark;
                break;
            case "2w mechanic":
                iconResId = R.drawable.ic_bike;
                gradientResId = R.drawable.bg_orange_gradient;
                statusBarColorResId = R.color.orange_dark;
                break;
            case "4w mechanic":
                iconResId = R.drawable.ic_car;
                gradientResId = R.drawable.bg_blue_gradient;
                statusBarColorResId = R.color.blue_dark;
                break;
            case "event helper":
                iconResId = R.drawable.ic_helper;
                gradientResId = R.drawable.bg_purple_gradient;
                statusBarColorResId = R.color.purple_dark;
                break;
        }

        if (iconResId != 0) {
            ivServiceIcon.setImageResource(iconResId);
            servicePng.setImageResource(iconResId);
        }

        if (gradientResId != 0) {
            headerLayout.setBackgroundResource(gradientResId);
            reviewIconLayout.setBackgroundResource(gradientResId);
        }

        if (statusBarColorResId != 0) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, statusBarColorResId));
        }

        // Set stepper
        step1.setBackgroundResource(R.drawable.bg_progress_step_active);
        step2.setBackgroundResource(R.drawable.bg_progress_step_active);
        step3.setBackgroundResource(R.drawable.bg_progress_step_active);
        step4.setBackgroundResource(R.drawable.bg_progress_step_active);
        tvStepTitle.setText("Step 4: Review");
    }
}
