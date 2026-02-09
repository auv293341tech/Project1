package com.example.project1;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class MyJobsPagerAdapter extends FragmentStateAdapter {

    public MyJobsPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new AppliedJobsFragment();
            case 1:
                return new AssignedJobsFragment();
            default:
                return new AppliedJobsFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
