package com.example.project1;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashActivity extends AppCompatActivity {

    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 1002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(this::checkUserStatus, 2000);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                    PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_REQUEST_CODE);
            }
        }
    }

    private void checkUserStatus() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is logged in, check their role and redirect
            String uid = user.getUid();
            FirebaseFirestore.getInstance().collection("users").document(uid).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String role = documentSnapshot.getString("role");
                            if ("worker".equals(role)) {
                                startActivity(new Intent(SplashActivity.this, WorkerHomeActivity.class));
                            } else if ("recruiter".equals(role)) {
                                startActivity(new Intent(SplashActivity.this, RecruiterHomeActivity.class));
                            } else {
                                // If role is not defined, redirect to RoleSelectActivity
                                startActivity(new Intent(SplashActivity.this, RoleSelectActivity.class));
                            }
                        } else {
                            // If user document doesn't exist, redirect to RoleSelectActivity
                            startActivity(new Intent(SplashActivity.this, RoleSelectActivity.class));
                        }
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        // On failure, redirect to LoginActivity as a fallback
                        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                        finish();
                    });
        } else {
            // User is not logged in, redirect to LoginActivity
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            finish();
        }
    }
}
