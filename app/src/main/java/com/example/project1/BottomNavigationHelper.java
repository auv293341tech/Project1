package com.example.project1;

import android.app.Activity;
import android.content.Intent;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BottomNavigationHelper {

    public static void setupBottomNavigation(BottomNavigationView bottomNavigationView, String userRole, final Activity activity, int currentItemId) {
        if (userRole == null) return;

        if ("recruiter".equals(userRole)) {
            bottomNavigationView.getMenu().clear();
            bottomNavigationView.inflateMenu(R.menu.bottom_nav_menu_recruiter);
        } else {
            bottomNavigationView.getMenu().clear();
            bottomNavigationView.inflateMenu(R.menu.worker_bottom_nav);
        }

        bottomNavigationView.setSelectedItemId(currentItemId);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == currentItemId) {
                return true;
            }

            Intent intent = null;
            if ("recruiter".equals(userRole)) {
                if (itemId == R.id.nav_home) {
                    intent = new Intent(activity, RecruiterHomeActivity.class);
                } else if (itemId == R.id.nav_my_postings) {
                    intent = new Intent(activity, MyPostingsActivity.class);
                } else if (itemId == R.id.nav_my_bookings) {
                    intent = new Intent(activity, MyBookingsActivity.class);
                } else if (itemId == R.id.nav_profile) {
                    intent = new Intent(activity, ProfileActivity.class);
                }
            } else { // Worker
                if (itemId == R.id.nav_home) {
                    intent = new Intent(activity, WorkerHomeActivity.class);
                } else if (itemId == R.id.nav_my_jobs) {
                    intent = new Intent(activity, MyJobsActivity.class);
                } else if (itemId == R.id.nav_my_bookings) {
                    intent = new Intent(activity, MyBookingsActivity.class);
                } else if (itemId == R.id.nav_account) {
                    intent = new Intent(activity, ProfileActivity.class);
                }
            }

            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                activity.startActivity(intent);
                activity.overridePendingTransition(0, 0); // No animation
                return true;
            }

            return false;
        });
    }
}
