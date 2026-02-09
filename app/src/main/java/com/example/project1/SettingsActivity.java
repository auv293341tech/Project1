package com.example.project1;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Find and set click listeners for all the items
        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(this);

        LinearLayout notificationsItem = findViewById(R.id.notifications_item);
        notificationsItem.setOnClickListener(this);

        LinearLayout languageItem = findViewById(R.id.language_item);
        languageItem.setOnClickListener(this);

        LinearLayout soundItem = findViewById(R.id.sound_item);
        soundItem.setOnClickListener(this);

        LinearLayout privacyItem = findViewById(R.id.privacy_item);
        privacyItem.setOnClickListener(this);

        LinearLayout paymentItem = findViewById(R.id.payment_item);
        paymentItem.setOnClickListener(this);

        LinearLayout dataItem = findViewById(R.id.data_item);
        dataItem.setOnClickListener(this);

        LinearLayout helpItem = findViewById(R.id.help_item);
        helpItem.setOnClickListener(this);

        // Dark Mode Switch
        SwitchCompat darkModeSwitch = findViewById(R.id.darkModeSwitch);
        darkModeSwitch.setChecked((getResources().getConfiguration().uiMode & android.content.res.Configuration.UI_MODE_NIGHT_MASK) == android.content.res.Configuration.UI_MODE_NIGHT_YES);
        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.backButton) {
            onBackPressed();
        } else if (id == R.id.notifications_item) {
            showToast("Notifications clicked");
        } else if (id == R.id.language_item) {
            showToast("Language clicked");
        } else if (id == R.id.sound_item) {
            showToast("Sound & Vibration clicked");
        } else if (id == R.id.privacy_item) {
            showToast("Privacy & Security clicked");
        } else if (id == R.id.payment_item) {
            showToast("Payment Methods clicked");
        } else if (id == R.id.data_item) {
            showToast("Data & Storage clicked");
        } else if (id == R.id.help_item) {
            showToast("Help & Support clicked");
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}