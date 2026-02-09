package com.example.project1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    EditText email, password;
    Button loginBtn, registerBtn;
    TextView tvForgotPassword;
    FirebaseAuth auth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainlogin2);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        loginBtn = findViewById(R.id.loginBtn);
        registerBtn = findViewById(R.id.registerBtn);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        loginBtn.setOnClickListener(v -> login());

        registerBtn.setOnClickListener(v -> {
            Intent i = new Intent(LoginActivity.this, RoleSelectActivity.class);
            startActivity(i);
        });

        tvForgotPassword.setOnClickListener(v -> {
            Intent i = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(i);
        });
    }

    private void login() {

        String e = email.getText().toString().trim();
        String p = password.getText().toString().trim();

        if (e.isEmpty() || p.isEmpty()) {
            Toast.makeText(this, "Enter email & password", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.signInWithEmailAndPassword(e, p)
                .addOnSuccessListener(r -> {
                    FirebaseUser user = auth.getCurrentUser();
                    if (user == null) {
                        Toast.makeText(this, "Login failed, please try again.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String uid = user.getUid();

                    getAndStoreFcmToken(uid); // Get and store FCM token

                    db.collection("users").document(uid).get()
                            .addOnSuccessListener(documentSnapshot -> {
                                String role = null;
                                if (documentSnapshot.exists()) {
                                    role = documentSnapshot.getString("role");
                                }

                                Intent i;
                                if ("worker".equals(role)) {
                                    i = new Intent(LoginActivity.this, WorkerHomeActivity.class);
                                } else if ("recruiter".equals(role)) {
                                    i = new Intent(LoginActivity.this, RecruiterHomeActivity.class);
                                } else {
                                    // Fallback for null role, non-existent document, or other roles
                                    i = new Intent(LoginActivity.this, RoleSelectActivity.class);
                                }

                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
                                finish();
                            })
                            .addOnFailureListener(e1 -> {
                                Toast.makeText(LoginActivity.this, "Failed to fetch user data. Please try again.", Toast.LENGTH_SHORT).show();
                                auth.signOut();
                            });
                })
                .addOnFailureListener(err ->
                        Toast.makeText(this, "Login failed: " + err.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void getAndStoreFcmToken(String userId) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                        return;
                    }

                    // Get new FCM registration token
                    String token = task.getResult();

                    // Log and toast
                    String msg = "FCM Registration Token: " + token;
                    Log.d(TAG, msg);

                    // Store the token in Firestore
                    db.collection("users").document(userId)
                            .update("fcmToken", token)
                            .addOnSuccessListener(aVoid -> Log.d(TAG, "FCM token updated for user: " + userId))
                            .addOnFailureListener(e -> Log.w(TAG, "Error updating FCM token", e));
                });
    }
}
