package com.example.project1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ServiceCatalogActivity extends AppCompatActivity {

    private RecyclerView rvServices;
    private TextView tvSkillTitle;
    private TextView tvServiceCount;
    private ImageView ivServiceIcon;
    private LinearLayout headerLayout;
    private ServiceAdapter adapter;
    private List<Service> serviceList = new ArrayList<>();

    private FirebaseFirestore db;
    private String skill;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_service_catalog);

        skill = getIntent().getStringExtra("skill");

        tvSkillTitle = findViewById(R.id.tvSkillTitle);
        tvServiceCount = findViewById(R.id.tvServiceCount);
        ivServiceIcon = findViewById(R.id.ivServiceIcon);
        headerLayout = findViewById(R.id.headerLayout);
        rvServices = findViewById(R.id.rvServices);

        final int initialPaddingTop = headerLayout.getPaddingTop();
        final int initialPaddingBottom = headerLayout.getPaddingBottom();
        final int initialPaddingLeft = headerLayout.getPaddingLeft();
        final int initialPaddingRight = headerLayout.getPaddingRight();

        ViewCompat.setOnApplyWindowInsetsListener(headerLayout, (v, insets) -> {
            int topInset = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top;
            v.setPadding(initialPaddingLeft, initialPaddingTop + topInset, initialPaddingRight, initialPaddingBottom);
            return WindowInsetsCompat.CONSUMED;
        });

        tvSkillTitle.setText("for " + skill);
        setServiceTheme();

        db = FirebaseFirestore.getInstance();

        setupRecyclerView();
        loadServices();
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
    }

    private void setupRecyclerView() {
        adapter = new ServiceAdapter(serviceList, service -> {
            Intent intent = new Intent(ServiceCatalogActivity.this, JobQuestionnaireActivity.class);
            intent.putExtra("skill", skill);
            intent.putExtra("serviceId", service.getServiceId());
            intent.putExtra("serviceTitle", service.getTitle());
            startActivity(intent);
        });
        rvServices.setLayoutManager(new LinearLayoutManager(this));
        rvServices.setAdapter(adapter);
    }

    private void loadServices() {
        if (skill == null || skill.isEmpty()) {
            Toast.makeText(this, "Skill not provided", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("service_catalog").document(skill.toLowerCase()).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            List<Map<String, Object>> services = (List<Map<String, Object>>) document.get("services");
                            if (services != null) {
                                serviceList.clear(); // Clear the list before adding new items
                                for (Map<String, Object> serviceMap : services) {
                                    Service service = new Service(
                                            (String) serviceMap.get("serviceId"),
                                            (String) serviceMap.get("title"),
                                            (String) serviceMap.get("description")
                                    );
                                    serviceList.add(service);
                                }
                                adapter.notifyDataSetChanged();
                                tvServiceCount.setText(serviceList.size() + " options");
                            } else {
                                tvServiceCount.setText("0 options");
                            }
                        } else {
                            Toast.makeText(ServiceCatalogActivity.this, "No services found for this skill.", Toast.LENGTH_SHORT).show();
                            tvServiceCount.setText("0 options");
                        }
                    } else {
                        Toast.makeText(ServiceCatalogActivity.this, "Error loading services.", Toast.LENGTH_SHORT).show();
                        tvServiceCount.setText("0 options");
                    }
                });
    }
}
