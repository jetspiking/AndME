package com.dustinhendriks.andme.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.dustinhendriks.andme.views.LauncherApplistFragment;
import com.dustinhendriks.andme.views.LauncherSettingsFragment;
import com.dustinhendriks.andme.views.LauncherTilesHomeFragment;

/**
 * The LauncherViewPageAdapter handles scrolling through the different pages in the application.
 * Pages:
 * - 1: Homescreen.
 * - 2: Application list.
 * - 3: Settings.
 */
public class LauncherViewPageAdapter extends FragmentPagerAdapter {
    public static int NUMBER_OF_PAGES = 3;
    public static int PAGE_HOME = 0;

    public LauncherTilesHomeFragment launcherTilesHomeFragment;
    public LauncherApplistFragment launcherAppListFragment;
    public LauncherSettingsFragment launcherSettingsFragment;

    public LauncherViewPageAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    /**
     * Retrieve and construct page fragments for the launcher.
     * @param position Page index.
     * @return Constructed fragment.
     */
    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                launcherTilesHomeFragment = new LauncherTilesHomeFragment();
                return launcherTilesHomeFragment;
            case 1:
                launcherAppListFragment = new LauncherApplistFragment();
                return launcherAppListFragment;
            case 2:
                launcherSettingsFragment = new LauncherSettingsFragment();
                return launcherSettingsFragment;
        }
        return null;
    }

    /**
     * Retrieve the amount of pages.
     * @return Amount of pages.
     */
    @Override
    public int getCount() {
        return NUMBER_OF_PAGES;
    }
}
