package com.example.bacakomik;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class PopularTabsAdapter extends FragmentStateAdapter {

    private static final int NUM_TABS = 3;

    public PopularTabsAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return PopularListFragment.newInstance("weekly");
            case 1:
                return PopularListFragment.newInstance("monthly");
            case 2:
                return PopularListFragment.newInstance("all");
            default:
                throw new IllegalStateException("Invalid position " + position);
        }
    }

    @Override
    public int getItemCount() {
        return NUM_TABS;
    }
}