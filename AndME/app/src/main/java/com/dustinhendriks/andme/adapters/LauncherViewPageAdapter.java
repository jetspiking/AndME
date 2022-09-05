package com.dustinhendriks.andme.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.dustinhendriks.andme.interfaces.SettingUpdater;
import com.dustinhendriks.andme.views.LauncherApplistFragment;
import com.dustinhendriks.andme.views.LauncherTilesHomeFragment;

public class LauncherViewPageAdapter extends FragmentPagerAdapter implements SettingUpdater {
    private static final String TAG = "LauncherViewPageAdapter";
    public static int NUMBER_OF_PAGES = 2;
    public static int PAGE_HOME = 0;
    public LauncherTilesHomeFragment launcherTilesHomeFragment;
    public LauncherApplistFragment launcherApplistFragment;

    public LauncherViewPageAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                if (launcherTilesHomeFragment == null) {
                    launcherTilesHomeFragment = new LauncherTilesHomeFragment();
                }
                return launcherTilesHomeFragment;
            case 1:
                if (launcherApplistFragment == null) {
                    launcherApplistFragment = new LauncherApplistFragment();
                }
                return launcherApplistFragment;
        }
        return null;
    }

    @Override
    public int getCount() {
        return NUMBER_OF_PAGES;
    }

    @Override
    public void notifyUpdate() {
    }
}
