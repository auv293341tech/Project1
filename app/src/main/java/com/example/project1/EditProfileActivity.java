package com.example.project1;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditProfileActivity extends AppCompatActivity {

    private ImageView ivProfileImage;
    private TextView tvProfileName, tvEmail, tvPhone, tvLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        ivProfileImage = findViewById(R.id.ivProfileImage);
        tvProfileName = findViewById(R.id.tvProfileName);
        tvEmail = findViewById(R.id.tvEmail);
        tvPhone = findViewById(R.id.tvPhone);
        tvLocation = findViewById(R.id.tvLocation);

        MaterialCardView btnPersonalInfo = findViewById(R.id.btnPersonalInfo);
        MaterialCardView btnAddress = findViewById(R.id.btnAddress);
        MaterialCardView btnProfessionalDetails = findViewById(R.id.btnProfessionalDetails);
        MaterialCardView btnChangePassword = findViewById(R.id.btnChangePassword);

        loadUserProfile();

        btnPersonalInfo.setOnClickListener(v -> {
            // TODO: Implement personal info update logic
            Toast.makeText(this, "Update Personal Info Clicked", Toast.LENGTH_SHORT).show();
        });

        btnAddress.setOnClickListener(v -> {
            // TODO: Implement address update logic
            Toast.makeText(this, "Update Address Clicked", Toast.LENGTH_SHORT).show();
        });

        btnProfessionalDetails.setOnClickListener(v -> {
            // TODO: Implement professional details update logic
            Toast.makeText(this, "Update Professional Details Clicked", Toast.LENGTH_SHORT).show();
        });

        btnChangePassword.setOnClickListener(v -> {
            // TODO: Implement change password logic
            Toast.makeText(this, "Change Password Clicked", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadUserProfile() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return;

        FirebaseFirestore.getInstance().collection("users")
                .document(currentUser.getUid()).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        // Fetch the name from the 'name' field
                        String name = doc.getString("name");
                        tvProfileName.setText(name != null ? name : "Name not set");

                        String email = doc.getString("email");
                        tvEmail.setText(email != null ? email : "Email not set");

                        String phone = doc.getString("phone");
                        tvPhone.setText(phone != null ? phone : "Phone not set");

                        String address = doc.getString("address");
                        tvLocation.setText(address != null ? address : "Location not set");

                        String profileImageUrl = doc.getString("profileImageUrl");
                        if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                            Glide.with(this)
                                 .load(profileImageUrl)
                                 .placeholder(R.drawable.ic_profile_placeholder)
                                 .into(ivProfileImage);
                        }
                    }
                });
    }
}
