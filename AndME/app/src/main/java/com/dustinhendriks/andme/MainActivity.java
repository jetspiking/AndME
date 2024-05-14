package com.dustinhendriks.andme;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.dustinhendriks.andme.adapters.LauncherViewPageAdapter;
import com.dustinhendriks.andme.models.AppSerializableData;
import com.dustinhendriks.andme.utils.AppMiscDefaults;
import com.dustinhendriks.andme.utils.SerializationUtils;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

/**
 * Contains the application data and acts as facade class by providing some handling methods.
 */
public class MainActivity extends AppCompatActivity {
    private ViewPager mViewPager;
    public static AppWidgetManager mAppWidgetManager;
    public LauncherViewPageAdapter mPageAdapter;
    public static MainActivity MAIN_ACTIVITY;

    public static final int REQUEST_EXTERNAL_STORAGE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppSerializableData appSerializableData = SerializationUtils.DeserializedData(this);
        if (appSerializableData != null)
            AppMiscDefaults.RestoreFromSerializedData(appSerializableData);
        if (!AppMiscDefaults.SHOW_NAVIGATION_BAR)
            requestFullscreen();
        setContentView(R.layout.fragment_launcher_content_pager);
        ViewPager viewPager = findViewById(R.id.fragment_launcher_content_vp_viewpager);
        LauncherViewPageAdapter adapter = new LauncherViewPageAdapter(getSupportFragmentManager(), FragmentPagerAdapter.POSITION_UNCHANGED);
        mViewPager = viewPager;
        mViewPager.setOffscreenPageLimit(LauncherViewPageAdapter.NUMBER_OF_PAGES);
        mPageAdapter = adapter;
        viewPager.setBackgroundColor(AppMiscDefaults.BACKGROUND_COLOR);
        viewPager.setAdapter(adapter);
        mViewPager.setCurrentItem(LauncherViewPageAdapter.PAGE_HOME);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            getWindow().setNavigationBarColor(AppMiscDefaults.BACKGROUND_COLOR);
        initializeWidgetHostManager();
        MAIN_ACTIVITY = this;
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        reloadLauncher();
    }

    public static void reloadLauncher() {
        Intent intent = MAIN_ACTIVITY.getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        MAIN_ACTIVITY.finish();
        MAIN_ACTIVITY.startActivity(intent);

        //MAIN_ACTIVITY.finish();
        //MAIN_ACTIVITY.startActivity(MAIN_ACTIVITY.getIntent());
    }

    public static void scrollToApps() {
        MAIN_ACTIVITY.mViewPager.setCurrentItem(LauncherViewPageAdapter.PAGE_HOME + 1, true);
    }

    public static void scrollToHome() {
        MAIN_ACTIVITY.mViewPager.setCurrentItem(LauncherViewPageAdapter.PAGE_HOME, true);
    }

    public static void scrollToSettings() {
        MAIN_ACTIVITY.mViewPager.setCurrentItem(LauncherViewPageAdapter.PAGE_HOME + 2, true);
    }

    public static void serializeData() {
        MAIN_ACTIVITY.mPageAdapter.launcherTilesHomeFragment.mLauncherTilesFragment.storeData();
    }

    public static void notifyDataSetTilesUpdate() {
        MAIN_ACTIVITY.mPageAdapter.launcherTilesHomeFragment.mLauncherTilesFragment.notifyDataSetUpdate();
    }

    public static void notifyDataSetAppListUpdate() {
        MAIN_ACTIVITY.mPageAdapter.launcherAppListFragment.notifyUpdatedUI();
    }

    /**
     * Hide the application navigation bar, by requesting fullscreen.
     */
    public void requestFullscreen() {
        View decorView = getWindow().getDecorView();
        int uiOptions = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        } else uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        decorView.setSystemUiVisibility(uiOptions);
    }

    private void initializeWidgetHostManager() {
        mAppWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
    }

    @Override
    public void onBackPressed() {
        scrollToHome();
    }
}
