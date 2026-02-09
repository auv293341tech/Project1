package com.example.project1;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MapActivity extends AppCompatActivity {

    private static final int LOCATION_REQUEST = 101;

    private MapView map;
    private EditText addressEdit;
    private Button confirmBtn;

    private double lat = 0, lng = 0;

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private Marker marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Configuration.getInstance().setUserAgentValue(getPackageName());
        setContentView(R.layout.activity_map);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        map = findViewById(R.id.map);
        addressEdit = findViewById(R.id.addressEdit);
        confirmBtn = findViewById(R.id.confirmBtn);

        map.setMultiTouchControls(true);

        checkPermissionAndLoadLocation();

        confirmBtn.setOnClickListener(v -> saveLocation());
    }

    // ================= PERMISSION =================
    private void checkPermissionAndLoadLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_REQUEST
            );
            return;
        }
        loadCurrentLocation();
    }

    // ================= LOCATION =================
    private void loadCurrentLocation() {
        try {
            LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);

            if (lm == null) {
                Toast.makeText(this, "Location service unavailable", Toast.LENGTH_SHORT).show();
                return;
            }

            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location == null) {
                location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }

            if (location == null) {
                lat = 19.0760;   // fallback Mumbai
                lng = 72.8777;
            } else {
                lat = location.getLatitude();
                lng = location.getLongitude();
            }

            GeoPoint point = new GeoPoint(lat, lng);
            map.getController().setZoom(16.0);
            map.getController().setCenter(point);

            addMarker(point);
            fetchAddress();

        } catch (Exception e) {
            Toast.makeText(this, "Location error", Toast.LENGTH_SHORT).show();
        }
    }

    private void addMarker(GeoPoint point) {
        map.getOverlays().clear();

        marker = new Marker(map);
        marker.setPosition(point);
        marker.setTitle("Drag to adjust location");
        marker.setDraggable(true);

        marker.setOnMarkerDragListener(new Marker.OnMarkerDragListener() {
            @Override public void onMarkerDrag(Marker marker) {}

            @Override
            public void onMarkerDragEnd(Marker marker) {
                lat = marker.getPosition().getLatitude();
                lng = marker.getPosition().getLongitude();
                fetchAddress();
            }

            @Override public void onMarkerDragStart(Marker marker) {}
        });

        map.getOverlays().add(marker);
        map.invalidate();
    }

    private void fetchAddress() {
        try {
            Geocoder geo = new Geocoder(this, Locale.getDefault());
            List<Address> list = geo.getFromLocation(lat, lng, 1);

            if (list != null && !list.isEmpty()) {
                addressEdit.setText(list.get(0).getAddressLine(0));
            }
        } catch (Exception ignored) {}
    }

    // ================= SAVE LOCATION =================
    private void saveLocation() {

        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = auth.getCurrentUser().getUid();
        String address = addressEdit.getText().toString().trim();

        if (address.isEmpty()) {
            Toast.makeText(this, "Address required", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("latitude", lat);
        data.put("longitude", lng);
        data.put("address", address);

        // ✅ ALWAYS SAVE INSIDE USERS
        db.collection("users")
                .document(uid)
                .set(data, SetOptions.merge())
                .addOnSuccessListener(unused -> {

                    startActivity(new Intent(this, WorkerHomeActivity.class));
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this,
                                "Firestore error: " + e.getMessage(),
                                Toast.LENGTH_LONG).show());
    }

    // ================= PERMISSION RESULT =================
    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_REQUEST &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            loadCurrentLocation();
        }
    }

    // ================= MAP LIFECYCLE =================
    @Override
    protected void onResume() {
        super.onResume();
        map.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        map.onPause();
    }
}