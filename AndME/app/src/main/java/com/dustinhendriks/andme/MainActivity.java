package com.dustinhendriks.andme;

import android.appwidget.AppWidgetManager;
import android.os.Bundle;
import android.view.View;

import com.dustinhendriks.andme.adapters.LauncherViewPageAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ViewPager mViewPager;
    public static AppWidgetManager mAppWidgetManager;
    private LauncherViewPageAdapter mPageAdapter;
    public static MainActivity MAIN_ACTIVITY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestFullscreen();
        setContentView(R.layout.fragment_launcher_content_pager);
        ViewPager viewPager = findViewById(R.id.fragment_launcher_content_vp_viewpager);
        LauncherViewPageAdapter adapter = new LauncherViewPageAdapter(getSupportFragmentManager(), FragmentPagerAdapter.POSITION_UNCHANGED);
        mViewPager = viewPager;
        mViewPager.setOffscreenPageLimit(LauncherViewPageAdapter.NUMBER_OF_PAGES);
        mPageAdapter = adapter;
        viewPager.setAdapter(adapter);
        mViewPager.setCurrentItem(LauncherViewPageAdapter.PAGE_HOME);
        initializeWidgetHostManager();
        MAIN_ACTIVITY = this;
    }

    public static void backToApps() {
        MAIN_ACTIVITY.mViewPager.setCurrentItem(LauncherViewPageAdapter.PAGE_HOME+1, true);
    }

    public static void serializeData() {
        if (MAIN_ACTIVITY.mPageAdapter.launcherApplistFragment != null)
            MAIN_ACTIVITY.mPageAdapter.launcherTilesHomeFragment.mLauncherTilesFragment.storeData();
    }

    public static void notifyDataSetUpdate() {
        if (MAIN_ACTIVITY.mPageAdapter.launcherApplistFragment != null)
            MAIN_ACTIVITY.mPageAdapter.launcherTilesHomeFragment.mLauncherTilesFragment.notifyDataSetUpdate();
    }

    public void requestFullscreen() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
    }

    private void initializeWidgetHostManager() {
        mAppWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        requestFullscreen();
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onPostResume() {
        requestFullscreen();
        super.onPostResume();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        requestFullscreen();
        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    public void onBackPressed() {
    }
}
