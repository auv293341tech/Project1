package com.example.project1;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class WorkerRegisterActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_CODE = 101;
    private static final int STORAGE_PERMISSION_CODE = 102;
    private static final String TAG = "WorkerRegisterActivity";

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    private EditText nameEt, phoneEt, emailEt;
    private TextInputEditText passEt;
    private AutoCompleteTextView skillDropdown, experienceDropdown;
    private ImageView ivProfilePic, ivEditPencil;

    private Uri imageUri;

    private final ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    imageUri = uri;
                    ivProfilePic.setImageURI(imageUri);
                }
            });

    private final ActivityResultLauncher<Intent> mTakePicture = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Bundle extras = result.getData().getExtras();
                    if (extras != null) {
                        imageUri = result.getData().getData();
                        ivProfilePic.setImageBitmap((android.graphics.Bitmap) extras.get("data"));
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_register);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        nameEt = findViewById(R.id.worker_name);
        emailEt = findViewById(R.id.worker_email);
        phoneEt = findViewById(R.id.worker_number);
        passEt = findViewById(R.id.worker_password);
        skillDropdown = findViewById(R.id.workerSkillDropdown);
        experienceDropdown = findViewById(R.id.workerExperienceDropdown);
        ivProfilePic = findViewById(R.id.ivProfilePic);
        ivEditPencil = findViewById(R.id.ivEditPencil);

        // ✅ Skill List
        String[] skills = {"Electrician", "Plumber", "Carpenter", "Painter", "Welder", "Cleaner", "AC Repair", "2W Mechanic", "4W Mechanic", "Event Helper"};
        ArrayAdapter<String> skillAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, skills);
        skillDropdown.setAdapter(skillAdapter);

        // ✅ Experience List
        String[] exp = {"Fresher", "1-2 Years", "3-5 Years", "5+ Years"};
        ArrayAdapter<String> expAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, exp);
        experienceDropdown.setAdapter(expAdapter);

        ivEditPencil.setOnClickListener(v -> showImagePickerDialog());

        findViewById(R.id.workerRegisterBtn).setOnClickListener(v -> registerWorker());
    }

    private void showImagePickerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Profile Picture");
        builder.setItems(new CharSequence[]{"Choose from Gallery", "Take a Picture"},
                (dialog, which) -> {
                    switch (which) {
                        case 0:
                            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                                mGetContent.launch("image/*");
                            } else {
                                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                            }
                            break;
                        case 1:
                            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                mTakePicture.launch(takePictureIntent);
                            } else {
                                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
                            }
                            break;
                    }
                });
        builder.create().show();
    }

    private void registerWorker() {
        String name = nameEt.getText().toString().trim();
        String email = emailEt.getText().toString().trim();
        String phone = phoneEt.getText().toString().trim();
        String password = passEt.getText().toString().trim();
        String skill = skillDropdown.getText().toString().trim();
        String experience = experienceDropdown.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty() ||
                skill.isEmpty() || experience.isEmpty()) {
            Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imageUri == null) {
            Toast.makeText(this, "Please upload a profile picture", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> {
                    FirebaseUser user = auth.getCurrentUser();
                    if (user == null) {
                        Toast.makeText(this, "Registration failed, please try again.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String uid = user.getUid();

                    if (imageUri != null) {
                        uploadImageToFirebase(uid, name, email, phone, skill, experience);
                    } else {
                        saveUserData(uid, name, email, phone, skill, experience, null);
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Registration failed: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }

    private void uploadImageToFirebase(String uid, String name, String email, String phone, String skill, String experience) {
        StorageReference profilePicRef = storageReference.child("profile_pictures/" + uid);

        profilePicRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> profilePicRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    saveUserData(uid, name, email, phone, skill, experience, uri.toString());
                }))
                .addOnFailureListener(e -> {
                    Toast.makeText(WorkerRegisterActivity.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    saveUserData(uid, name, email, phone, skill, experience, null); // Save data even if image upload fails
                });
    }

    private void saveUserData(String uid, String name, String email, String phone, String skill, String experience, String profileImageUrl) {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            String token = null;
            if (task.isSuccessful()) {
                token = task.getResult();
            } else {
                Log.w(TAG, "Fetching FCM registration token failed", task.getException());
            }

            Map<String, Object> data = new HashMap<>();
            data.put("name", name);
            data.put("email", email);
            data.put("phone", phone);
            data.put("skill", skill.toLowerCase(Locale.ROOT));
            data.put("experience", experience);
            data.put("role", "worker");
            data.put("fcmToken", token); // This will be null if the token fetch failed, which is acceptable.
            if (profileImageUrl != null) {
                data.put("profileImageUrl", profileImageUrl);
            }

            db.collection("users").document(uid).set(data)
                    .addOnSuccessListener(unused -> {
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                .build();
                        
                        FirebaseUser currentUser = auth.getCurrentUser();
                        if(currentUser != null) {
                            currentUser.updateProfile(profileUpdates);
                        }

                        Intent i = new Intent(this, MapActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        i.putExtra("role", "worker");
                        startActivity(i);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to save user data.", Toast.LENGTH_SHORT).show();
                        // Optional: Consider deleting the auth user if saving to Firestore fails
                        // auth.getCurrentUser().delete();
                        Log.e(TAG, "Failed to save user data.", e);
                    });
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                mTakePicture.launch(takePictureIntent);
            } else {
                Toast.makeText(this, "Camera permission is required to take a picture.", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mGetContent.launch("image/*");
            } else {
                Toast.makeText(this, "Storage permission is required to choose an image.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
