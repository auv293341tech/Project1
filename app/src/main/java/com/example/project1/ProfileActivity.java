package com.example.project1;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ProfileActivity extends AppCompatActivity {

    // Views
    private ImageView ivProfilePic;
    private TextView tvUserName, tvUserEmail, tvPostingsCount, tvBookingsCount, tvRatingValue, tvCompletedJobsCount;
    private MaterialCardView myPostingsCard, myBookingsCard, editProfileCard, settingsCard;
    private LinearLayout statsCard, postingsContainer, bookingsContainer, ratingContainer, completedJobsContainer;
    private View ratingSeparator, bookingsSeparator, completedJobsSeparator;
    private BottomNavigationView bottomNavigationView;

    // Firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private StorageReference storageReference;

    // State
    private String userRole;
    private Uri imageUri;

    // ActivityResultLaunchers
    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    dispatchTakePictureIntent();
                } else {
                    Toast.makeText(this, "Camera permission is required to take a picture.", Toast.LENGTH_SHORT).show();
                }
            });

    private final ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    imageUri = uri;
                    ivProfilePic.setImageURI(imageUri);
                    uploadImageToFirebase();
                }
            });

    private final ActivityResultLauncher<Uri> takePictureLauncher = registerForActivityResult(
            new ActivityResultContracts.TakePicture(),
            result -> {
                if (result && imageUri != null) {
                    ivProfilePic.setImageURI(imageUri);
                    uploadImageToFirebase();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize UI
        ivProfilePic = findViewById(R.id.ivProfilePic);
        tvUserName = findViewById(R.id.tvUserName);
        tvUserEmail = findViewById(R.id.tvUserEmail);
        tvPostingsCount = findViewById(R.id.tvPostingsCount);
        tvBookingsCount = findViewById(R.id.tvBookingsCount);
        tvRatingValue = findViewById(R.id.tvRatingValue);
        myPostingsCard = findViewById(R.id.myPostingsCard);
        myBookingsCard = findViewById(R.id.myBookingsCard);
        editProfileCard = findViewById(R.id.editProfileCard);
        settingsCard = findViewById(R.id.settingsCard);
        statsCard = findViewById(R.id.statsCard);
        postingsContainer = findViewById(R.id.postingsContainer);
        bookingsContainer = findViewById(R.id.bookingsContainer);
        ratingContainer = findViewById(R.id.ratingContainer);
        bookingsSeparator = findViewById(R.id.bookingsSeparator);
        ratingSeparator = findViewById(R.id.ratingSeparator);
        bottomNavigationView = findViewById(R.id.bottomNav);
        Button btnLogout = findViewById(R.id.btn_logout);
        tvCompletedJobsCount = findViewById(R.id.tvCompletedJobsCount);
        completedJobsContainer = findViewById(R.id.completed_jobs_container);
        completedJobsSeparator = findViewById(R.id.completedJobsSeparator);

        // Initialize Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        loadUserProfile();

        // Set Click Listeners
        ivProfilePic.setOnClickListener(v -> showImageSourceDialog());

        myPostingsCard.setOnClickListener(v -> {
            if ("recruiter".equals(userRole)) {
                startActivity(new Intent(ProfileActivity.this, MyPostingsActivity.class));
            } else if ("worker".equals(userRole)) {
                startActivity(new Intent(ProfileActivity.this, MyJobsActivity.class));
            }
        });

        myBookingsCard.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, MyBookingsActivity.class);
            startActivity(intent);
        });

        editProfileCard.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            startActivity(intent);
        });

        settingsCard.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, SettingsActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            firebaseAuth.signOut();
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void showImageSourceDialog() {
        final CharSequence[] items = {"Camera", "Gallery"};
        new AlertDialog.Builder(this)
                .setTitle("Select Image Source")
                .setItems(items, (dialog, item) -> {
                    if (items[item].equals("Camera")) {
                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                            dispatchTakePictureIntent();
                        } else {
                            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
                        }
                    } else {
                        mGetContent.launch("image/*");
                    }
                })
                .show();
    }

    private void dispatchTakePictureIntent() {
        try {
            File photoFile = createImageFile();
            imageUri = FileProvider.getUriForFile(this, "com.example.project1.provider", photoFile);
            takePictureLauncher.launch(imageUri);
        } catch (IOException ex) {
            Toast.makeText(this, "Error creating image file.", Toast.LENGTH_SHORT).show();
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    private void loadUserProfile() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            // Not logged in, go to login screen
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        tvUserName.setText(currentUser.getDisplayName() != null ? currentUser.getDisplayName() : "Name not set");
        tvUserEmail.setText(currentUser.getEmail());

        db.collection("users").document(currentUser.getUid()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        userRole = documentSnapshot.getString("role");
                        String profileImageUrl = documentSnapshot.getString("profileImageUrl");
                        if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                            Glide.with(this).load(profileImageUrl).placeholder(R.drawable.ic_profile_placeholder).into(ivProfilePic);
                        }

                        // Centralized navigation setup
                        if (userRole != null) {
                            bottomNavigationView.setVisibility(View.VISIBLE);
                            BottomNavigationHelper.setupBottomNavigation(bottomNavigationView, userRole, this, R.id.nav_account);
                        } else {
                            bottomNavigationView.setVisibility(View.GONE);
                        }

                        // Role-specific UI setup
                        if ("recruiter".equals(userRole)) {
                            statsCard.setVisibility(View.VISIBLE);
                            myPostingsCard.setVisibility(View.VISIBLE);
                            myBookingsCard.setVisibility(View.VISIBLE);
                            ratingContainer.setVisibility(View.GONE);
                            ratingSeparator.setVisibility(View.GONE);
                            bookingsContainer.setVisibility(View.VISIBLE);
                            bookingsSeparator.setVisibility(View.VISIBLE);
                            completedJobsContainer.setVisibility(View.GONE);
                            completedJobsSeparator.setVisibility(View.GONE);
                            loadRecruiterStats(currentUser.getUid()); // Fetch stats for recruiter
                        } else if ("worker".equals(userRole)) {
                            statsCard.setVisibility(View.VISIBLE);
                            myPostingsCard.setVisibility(View.VISIBLE);
                            TextView tvMyPostings = myPostingsCard.findViewById(R.id.my_postings_title);
                            tvMyPostings.setText("My Jobs");
                            TextView tvMyPostingsSubtitle = myPostingsCard.findViewById(R.id.my_postings_subtitle);
                            tvMyPostingsSubtitle.setText("View your assigned jobs");
                            myBookingsCard.setVisibility(View.GONE);
                            ratingContainer.setVisibility(View.VISIBLE);
                            ratingSeparator.setVisibility(View.VISIBLE);
                            bookingsContainer.setVisibility(View.GONE);
                            bookingsSeparator.setVisibility(View.GONE);
                            ((TextView)findViewById(R.id.postings_label)).setText("Jobs");
                            completedJobsContainer.setVisibility(View.VISIBLE);
                            completedJobsSeparator.setVisibility(View.VISIBLE);

                            Double rating = documentSnapshot.getDouble("rating"); // Get rating from user doc
                            loadWorkerStats(currentUser.getUid(), rating); // Fetch stats for worker

                        } else {
                            statsCard.setVisibility(View.GONE);
                            myPostingsCard.setVisibility(View.GONE);
                            myBookingsCard.setVisibility(View.GONE);
                        }
                    }
                });
    }

    private void loadRecruiterStats(String userId) {
        // Query for Postings
        db.collection("jobPostings").whereEqualTo("customerId", userId).get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                tvPostingsCount.setText(String.valueOf(queryDocumentSnapshots.size()));
            })
            .addOnFailureListener(e -> tvPostingsCount.setText("0"));

        // Query for Bookings
        db.collection("bookings").whereEqualTo("customerId", userId).get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                tvBookingsCount.setText(String.valueOf(queryDocumentSnapshots.size()));
            })
            .addOnFailureListener(e -> tvBookingsCount.setText("0"));
    }

    private void loadWorkerStats(String userId, Double rating) {
        // Query for Jobs
        db.collection("jobPostings").whereEqualTo("assignedWorkerId", userId).get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                tvPostingsCount.setText(String.valueOf(queryDocumentSnapshots.size()));
            })
            .addOnFailureListener(e -> tvPostingsCount.setText("0"));

        // Query for Completed Jobs
        db.collection("jobPostings").whereEqualTo("assignedWorkerId", userId).whereEqualTo("status", "Completed").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    tvCompletedJobsCount.setText(String.valueOf(queryDocumentSnapshots.size()));
                })
                .addOnFailureListener(e -> tvCompletedJobsCount.setText("0"));

        // Set Rating
        if (rating != null) {
            tvRatingValue.setText(String.format(Locale.US, "%.1f", rating));
        } else {
            tvRatingValue.setText("N/A");
        }
    }


    private void uploadImageToFirebase() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (imageUri != null && currentUser != null) {
            StorageReference profilePicRef = storageReference.child("profile_pictures/" + currentUser.getUid());
            profilePicRef.putFile(imageUri).continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return profilePicRef.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    updateProfileUrlInFirestore(downloadUri);
                } else {
                    Toast.makeText(ProfileActivity.this, "Upload failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void updateProfileUrlInFirestore(Uri uri) {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            db.collection("users").document(currentUser.getUid())
                    .update("profileImageUrl", uri.toString())
                    .addOnSuccessListener(aVoid -> Toast.makeText(this, "Profile picture updated.", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(this, "Failed to update profile URL.", Toast.LENGTH_SHORT).show());
        }
    }
}
