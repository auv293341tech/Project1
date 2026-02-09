package com.example.project1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JobQuestionnaireActivity extends AppCompatActivity {

    private RecyclerView rvQuestions;
    private TextView tvServiceTitle, tvStepTitle;
    private ImageView ivServiceIcon;
    private LinearLayout headerLayout;
    private Button btnNext;
    private QuestionAdapter adapter;
    private List<Question> questionList = new ArrayList<>();
    private View step1, step2, step3, step4;

    private FirebaseFirestore db;
    private String skill, serviceId, serviceTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_job_questionnaire);

        skill = getIntent().getStringExtra("skill");
        serviceId = getIntent().getStringExtra("serviceId");
        serviceTitle = getIntent().getStringExtra("serviceTitle");

        tvServiceTitle = findViewById(R.id.tvServiceTitle);
        tvStepTitle = findViewById(R.id.tvStepTitle);
        ivServiceIcon = findViewById(R.id.ivServiceIcon);
        headerLayout = findViewById(R.id.headerLayout);
        rvQuestions = findViewById(R.id.rvQuestions);
        btnNext = findViewById(R.id.btnNext);
        step1 = findViewById(R.id.step1);
        step2 = findViewById(R.id.step2);
        step3 = findViewById(R.id.step3);
        step4 = findViewById(R.id.step4);

        final int initialPaddingTop = headerLayout.getPaddingTop();
        final int initialPaddingBottom = headerLayout.getPaddingBottom();
        final int initialPaddingLeft = headerLayout.getPaddingLeft();
        final int initialPaddingRight = headerLayout.getPaddingRight();

        ViewCompat.setOnApplyWindowInsetsListener(headerLayout, (v, insets) -> {
            int topInset = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top;
            v.setPadding(initialPaddingLeft, initialPaddingTop + topInset, initialPaddingRight, initialPaddingBottom);
            return WindowInsetsCompat.CONSUMED;
        });

        ViewCompat.setOnApplyWindowInsetsListener(btnNext, (v, insets) -> {
            int bottomInset = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom;
            v.setPadding(v.getPaddingLeft(), v.getPaddingTop(), v.getPaddingRight(), bottomInset + v.getPaddingBottom());
            return insets;
        });

        tvServiceTitle.setText(serviceTitle);
        setServiceTheme();

        db = FirebaseFirestore.getInstance();

        setupRecyclerView();
        loadQuestions();

        btnNext.setOnClickListener(v -> {
            HashMap<String, Object> answers = adapter.getAnswers();
            Intent intent = new Intent(JobQuestionnaireActivity.this, SelectDateActivity.class);
            intent.putExtra("skill", skill);
            intent.putExtra("serviceId", serviceId);
            intent.putExtra("serviceTitle", serviceTitle);
            intent.putExtra("answers", answers);
            startActivity(intent);
        });
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
        }

        if (gradientResId != 0) {
            headerLayout.setBackgroundResource(gradientResId);
        }

        if (statusBarColorResId != 0) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, statusBarColorResId));
        }

        // Set stepper
        step1.setBackgroundResource(R.drawable.bg_progress_step_active);
        step2.setBackgroundResource(R.drawable.bg_progress_step_inactive);
        step3.setBackgroundResource(R.drawable.bg_progress_step_inactive);
        step4.setBackgroundResource(R.drawable.bg_progress_step_inactive);
        tvStepTitle.setText("Step 1: Details");
    }

    private void setupRecyclerView() {
        adapter = new QuestionAdapter(this, questionList);
        rvQuestions.setLayoutManager(new LinearLayoutManager(this));
        rvQuestions.setAdapter(adapter);
    }

    private void loadQuestions() {
        if (skill == null || serviceId == null) {
            Toast.makeText(this, "Skill or Service ID not provided", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("service_catalog").document(skill.toLowerCase()).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            List<Map<String, Object>> services = (List<Map<String, Object>>) document.get("services");
                            if (services != null) {
                                for (Map<String, Object> serviceMap : services) {
                                    if (serviceId.equals(serviceMap.get("serviceId"))) {
                                        List<Map<String, Object>> questions = (List<Map<String, Object>>) serviceMap.get("questions");
                                        if (questions != null) {
                                            for (Map<String, Object> questionMap : questions) {
                                                Question question = new Question(
                                                        (String) questionMap.get("questionId"),
                                                        (String) questionMap.get("text"),
                                                        (String) questionMap.get("type"),
                                                        (List<String>) questionMap.get("options")
                                                );
                                                questionList.add(question);
                                            }
                                            adapter.notifyDataSetChanged();
                                        }
                                        break; // Stop after finding the right service
                                    }
                                }
                            }
                        } else {
                            Toast.makeText(JobQuestionnaireActivity.this, "No services found for this skill.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(JobQuestionnaireActivity.this, "Error loading questions.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
