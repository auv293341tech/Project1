package com.example.project1;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditJobActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private DocumentReference jobRef;

    private EditText etJobTitle, etJobDescription, etPay;
    private Button btnUpdateJob;

    private String documentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_job);

        db = FirebaseFirestore.getInstance();

        etJobTitle = findViewById(R.id.etJobTitle);
        etJobDescription = findViewById(R.id.etJobDescription);
        etPay = findViewById(R.id.etPay);
        btnUpdateJob = findViewById(R.id.btnUpdateJob);

        documentId = getIntent().getStringExtra("documentId");
        if (documentId == null) {
            finish(); // Finish if no document ID is provided
            return;
        }

        jobRef = db.collection("job_postings").document(documentId);

        loadJobDetails();

        btnUpdateJob.setOnClickListener(v -> updateJob());
    }

    private void loadJobDetails() {
        jobRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                etJobTitle.setText(documentSnapshot.getString("title"));
                etJobDescription.setText(documentSnapshot.getString("description"));
                etPay.setText(documentSnapshot.getString("pay"));
            } else {
                Toast.makeText(this, "Job not found", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to load job details", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void updateJob() {
        String title = etJobTitle.getText().toString().trim();
        String description = etJobDescription.getText().toString().trim();
        String pay = etPay.getText().toString().trim();

        if (title.isEmpty() || pay.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        jobRef.update("title", title,
                        "description", description,
                        "pay", pay)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Job updated successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error updating job: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
