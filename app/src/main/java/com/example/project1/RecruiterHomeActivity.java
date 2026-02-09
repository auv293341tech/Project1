package com.example.project1; // Ensure this matches your project's package name

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class RecruiterHomeActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    private TextView tvUserName;
    private TextView tvLocation;
    private EditText etSearch;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recruiter_home);

        tvUserName = findViewById(R.id.tvUserName);
        tvLocation = findViewById(R.id.tvLocation);
        etSearch = findViewById(R.id.etSearch);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNav);

        findViewById(R.id.catElectrician).setOnClickListener(this);
        findViewById(R.id.catPlumber).setOnClickListener(this);
        findViewById(R.id.catCarpenter).setOnClickListener(this);
        findViewById(R.id.catPainter).setOnClickListener(this);
        findViewById(R.id.catWelder).setOnClickListener(this);
        findViewById(R.id.catCleaner).setOnClickListener(this);
        findViewById(R.id.catAC).setOnClickListener(this);
        findViewById(R.id.catMechanic).setOnClickListener(this);
        findViewById(R.id.catOther).setOnClickListener(this);
        findViewById(R.id.catEventHelper).setOnClickListener(this);
        findViewById(R.id.imgNotification).setOnClickListener(this);

        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    (event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                String searchQuery = etSearch.getText().toString().trim();
                if (!searchQuery.isEmpty()) {
                    Intent intent = new Intent(RecruiterHomeActivity.this, ServiceCatalogActivity.class);
                    intent.putExtra("skill", searchQuery);
                    startActivity(intent);
                }
                return true;
            }
            return false;
        });

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        loadUserData();

        BottomNavigationHelper.setupBottomNavigation(bottomNavigationView, "recruiter", this, R.id.nav_home);
    }

    @Override
    public void onClick(View v) {
        String skill = "";
        int viewId = v.getId();
        if (viewId == R.id.catElectrician) {
            skill = "electrician";
        } else if (viewId == R.id.catPlumber) {
            skill = "plumber";
        } else if (viewId == R.id.catCarpenter) {
            skill = "carpenter";
        } else if (viewId == R.id.catPainter) {
            skill = "painter";
        } else if (viewId == R.id.catWelder) {
            skill = "welder";
        } else if (viewId == R.id.catCleaner) {
            skill = "cleaner";
        } else if (viewId == R.id.catAC) {
            skill = "ac repair";
        } else if (viewId == R.id.catMechanic) {
            skill = "2w mechanic";
        } else if (viewId == R.id.catOther) {
            skill = "4w mechanic";
        } else if (viewId == R.id.catEventHelper) {
            skill = "event helper";
        } else if (viewId == R.id.imgNotification) {
            startActivity(new Intent(this, NotificationsActivity.class));
            return;
        }

        if (!skill.isEmpty()) {
            Intent intent = new Intent(RecruiterHomeActivity.this, ServiceCatalogActivity.class);
            intent.putExtra("skill", skill);
            startActivity(intent);
        }
    }

    private void loadUserData() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            // Load user name
            String userName = currentUser.getDisplayName();
            if (userName != null && !userName.isEmpty()) {
                String[] nameParts = userName.split(" ");
                String firstName = nameParts.length > 0 ? nameParts[0] : userName;
                tvUserName.setText(firstName + " 👋");
            } else {
                tvUserName.setText(currentUser.getEmail());
            }

            // Load location from Firestore
            String userId = currentUser.getUid();
            db.collection("users").document(userId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String location = documentSnapshot.getString("location");
                            if (location != null && !location.isEmpty()) {
                                tvLocation.setText("📍 " + location);
                            } else {
                                // If location is not in the database, get it from the device
                                loadUserLocation();
                            }
                        } else {
                            // If user document doesn't exist, get location from the device
                            loadUserLocation();
                        }
                    })
                    .addOnFailureListener(e -> {
                        // If fetching from Firestore fails, get location from the device
                        loadUserLocation();
                    });
        } else {
            Intent intent = new Intent(RecruiterHomeActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    private void loadUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        try {
                            Geocoder geocoder = new Geocoder(RecruiterHomeActivity.this, Locale.getDefault());
                            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            if (addresses != null && !addresses.isEmpty()) {
                                Address address = addresses.get(0);
                                String locationString = address.getLocality() + ", " + address.getCountryName();
                                tvLocation.setText("📍 " + locationString);
                            } else {
                                tvLocation.setText("📍 Location not found");
                            }
                        } catch (IOException e) {
                            tvLocation.setText("📍 Cannot get location address");
                        }
                    } else {
                        tvLocation.setText("📍 Turn on location services");
                    }
                })
                .addOnFailureListener(this, e -> tvLocation.setText("📍 Location access failed"));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadUserLocation();
            } else {
                Toast.makeText(this, "Location permission is required to show your location", Toast.LENGTH_LONG).show();
                tvLocation.setText("📍 Location permission denied");
            }
        }
    }
}
