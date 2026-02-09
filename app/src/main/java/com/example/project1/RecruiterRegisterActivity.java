package com.example.project1;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;

public class RecruiterRegisterActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseFirestore db;

    EditText nameEt, phoneEt, passEt, emailEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recruiter_register);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        nameEt = findViewById(R.id.recruiter_name);
        emailEt = findViewById(R.id.recruiter_email);
        phoneEt = findViewById(R.id.recruiter_number);
        passEt = findViewById(R.id.recruiter_password);

        findViewById(R.id.recruiterRegisterBtn).setOnClickListener(v -> registerRecruiter());
    }

    private void registerRecruiter() {

        String name = nameEt.getText().toString().trim();
        String email = emailEt.getText().toString().trim();
        String phone = phoneEt.getText().toString().trim();
        String password = passEt.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> {

                    FirebaseUser user = auth.getCurrentUser();
                    if (user == null) return;
                    String uid = user.getUid();

                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(name)
                            .build();

                    user.updateProfile(profileUpdates);

                    FirebaseMessaging.getInstance().getToken().addOnSuccessListener(token -> {
                        Map<String, Object> data = new HashMap<>();
                        data.put("name", name);
                        data.put("email", email);
                        data.put("phone", phone);
                        data.put("role", "recruiter");
                        data.put("fcmToken", token); // Add FCM token

                        db.collection("users").document(uid).set(data)
                                .addOnSuccessListener(unused -> {
                                    Intent i = new Intent(this, MapActivity.class);
                                    i.putExtra("role", "recruiter");
                                    startActivity(i);
                                    finish();
                                });
                    });
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show());
    }
}
