package com.example.project1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class SelectDateActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private Button btnNext;
    private TextView tvServiceTitle, tvStepTitle, tvSelectedDate;
    private ImageView ivServiceIcon;
    private LinearLayout headerLayout;
    private View step1, step2, step3, step4;

    private String skill, serviceId, serviceTitle;
    private HashMap<String, Object> answers;
    private long selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_select_date);

        skill = getIntent().getStringExtra("skill");
        serviceId = getIntent().getStringExtra("serviceId");
        serviceTitle = getIntent().getStringExtra("serviceTitle");
        answers = (HashMap<String, Object>) getIntent().getSerializableExtra("answers");

        calendarView = findViewById(R.id.calendarView);
        btnNext = findViewById(R.id.btnNext);
        tvServiceTitle = findViewById(R.id.tvServiceTitle);
        tvStepTitle = findViewById(R.id.tvStepTitle);
        ivServiceIcon = findViewById(R.id.ivServiceIcon);
        headerLayout = findViewById(R.id.headerLayout);
        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        step1 = findViewById(R.id.step1);
        step2 = findViewById(R.id.step2);
        step3 = findViewById(R.id.step3);
        step4 = findViewById(R.id.step4);

        Calendar calendar = Calendar.getInstance();
        calendarView.setMinDate(calendar.getTimeInMillis());
        calendar.add(Calendar.DAY_OF_MONTH, 30);
        calendarView.setMaxDate(calendar.getTimeInMillis());

        selectedDate = calendarView.getDate();
        updateSelectedDateText(selectedDate);

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Calendar c = Calendar.getInstance();
            c.set(year, month, dayOfMonth);
            selectedDate = c.getTimeInMillis();
            updateSelectedDateText(selectedDate);
        });

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
            Intent intent = new Intent(SelectDateActivity.this, SelectTimeActivity.class);
            intent.putExtra("skill", skill);
            intent.putExtra("serviceId", serviceId);
            intent.putExtra("serviceTitle", serviceTitle);
            intent.putExtra("answers", answers);
            intent.putExtra("date", selectedDate);
            startActivity(intent);
        });
    }

    private void updateSelectedDateText(long dateInMillis) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.getDefault());
        tvSelectedDate.setText(sdf.format(dateInMillis));
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
        step3.setBackgroundResource(R.drawable.bg_progress_step_inactive);
        step4.setBackgroundResource(R.drawable.bg_progress_step_inactive);
        tvStepTitle.setText("Step 2: Select Date");
    }
}
