package com.example.project1;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.gridlayout.widget.GridLayout;

import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class SelectTimeActivity extends AppCompatActivity {

    private Button btnNext;
    private TextView tvServiceTitle, tvStepTitle;
    private ImageView ivServiceIcon;
    private LinearLayout headerLayout;
    private View step1, step2, step3, step4;
    private MaterialCardView morningCard, afternoonCard, eveningCard;
    private GridLayout availableTimesGrid;

    private String skill, serviceId, serviceTitle;
    private HashMap<String, Object> answers;
    private long date;
    private String selectedTimeSlot = "";
    private TextView selectedTimeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_select_time);

        skill = getIntent().getStringExtra("skill");
        serviceId = getIntent().getStringExtra("serviceId");
        serviceTitle = getIntent().getStringExtra("serviceTitle");
        answers = (HashMap<String, Object>) getIntent().getSerializableExtra("answers");
        date = getIntent().getLongExtra("date", 0);

        btnNext = findViewById(R.id.btnNext);
        tvServiceTitle = findViewById(R.id.tvServiceTitle);
        tvStepTitle = findViewById(R.id.tvStepTitle);
        ivServiceIcon = findViewById(R.id.ivServiceIcon);
        headerLayout = findViewById(R.id.headerLayout);
        step1 = findViewById(R.id.step1);
        step2 = findViewById(R.id.step2);
        step3 = findViewById(R.id.step3);
        step4 = findViewById(R.id.step4);
        morningCard = findViewById(R.id.morningCard);
        afternoonCard = findViewById(R.id.afternoonCard);
        eveningCard = findViewById(R.id.eveningCard);
        availableTimesGrid = findViewById(R.id.availableTimesGrid);

        morningCard.setOnClickListener(v -> {
            selectedTimeSlot = "Morning";
            morningCard.setChecked(true);
            afternoonCard.setChecked(false);
            eveningCard.setChecked(false);
            populateTimeSlots(8, 12);
        });

        afternoonCard.setOnClickListener(v -> {
            selectedTimeSlot = "Afternoon";
            morningCard.setChecked(false);
            afternoonCard.setChecked(true);
            eveningCard.setChecked(false);
            populateTimeSlots(12, 18);
        });

        eveningCard.setOnClickListener(v -> {
            selectedTimeSlot = "Evening";
            morningCard.setChecked(false);
            afternoonCard.setChecked(false);
            eveningCard.setChecked(true);
            populateTimeSlots(18, 22);
        });

        // Default selection
        morningCard.performClick();

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

        btnNext.setOnClickListener(v -> {
            // Pass the selected time to the next activity
            Intent intent = new Intent(SelectTimeActivity.this, ReviewPostingActivity.class);
            intent.putExtra("skill", skill);
            intent.putExtra("serviceId", serviceId);
            intent.putExtra("serviceTitle", serviceTitle);
            intent.putExtra("answers", answers);
            intent.putExtra("date", date);
            if (selectedTimeTextView != null) {
                intent.putExtra("timeSlot", selectedTimeTextView.getText().toString());
            }
            startActivity(intent);
        });
    }

    private void populateTimeSlots(int startHour, int endHour) {
        availableTimesGrid.removeAllViews();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, startHour);
        calendar.set(Calendar.MINUTE, 0);

        while (calendar.get(Calendar.HOUR_OF_DAY) < endHour) {
            TextView timeSlotView = (TextView) LayoutInflater.from(this).inflate(R.layout.item_time_slot, availableTimesGrid, false);
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            timeSlotView.setText(sdf.format(calendar.getTime()));
            timeSlotView.setOnClickListener(v -> {
                if (selectedTimeTextView != null) {
                    selectedTimeTextView.setSelected(false);
                }
                v.setSelected(true);
                selectedTimeTextView = (TextView) v;
            });
            availableTimesGrid.addView(timeSlotView);
            calendar.add(Calendar.MINUTE, 30);
        }
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
        step2.setBackgroundResource(R.drawable.bg_progress_step_active);
        step3.setBackgroundResource(R.drawable.bg_progress_step_active);
        step4.setBackgroundResource(R.drawable.bg_progress_step_inactive);
        tvStepTitle.setText("Step 3: Select Time");
    }
}
