package com.dustinhendriks.andme.views;

import android.content.Context;
import android.content.Intent;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.LauncherApps;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.UserHandle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dustinhendriks.andme.R;
import com.dustinhendriks.andme.adapters.LauncherApplistingAdapter;
import com.dustinhendriks.andme.interfaces.OnItemClickedListener;
import com.dustinhendriks.andme.models.App;
import com.dustinhendriks.andme.utils.AppDefaults;
import com.dustinhendriks.andme.utils.EqualSpaceDecorator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class LauncherApplistFragment extends Fragment implements OnItemClickedListener {
    private static final String TAG = "LauncherApplistFragment";
    private RecyclerView mApplistRecycler;
    private LauncherApplistingAdapter mLauncherApplistingAdapter;
    private ArrayList<App> mApps = new ArrayList<>();
    private ArrayList<App> mInstalledApps = new ArrayList<>();
    private PackageManager mPackageManager;
    private int mSelectedProfile = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_launcher_applist, container, false);
        mPackageManager = Objects.requireNonNull(getActivity()).getPackageManager();
        initAppsList(view);
        loadApps();

        ToggleButton profileToggleSwitch = view.findViewById(R.id.fragment_launcher_applist_tb_profile_toggle_switch);
        profileToggleSwitch.setVisibility(View.INVISIBLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            LauncherApps launcher = (LauncherApps) getActivity().getSystemService(Context.LAUNCHER_APPS_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (launcher.getProfiles().size() > 1)
                    profileToggleSwitch.setVisibility(View.VISIBLE);
            }
        }

        profileToggleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mSelectedProfile = b ? 1 : 0;
                loadApps();
            }
        });

        return view;
    }

    private void initAppsList(View view) {
        ConstraintLayout constraintLayout = view.findViewById(R.id.fragment_launcher_applist_cl_constraintlayout);
        mApplistRecycler = view.findViewById(R.id.fragment_launcher_applist_rv_recyclerview);
        mLauncherApplistingAdapter = new LauncherApplistingAdapter(getContext(), mApps, this);
        mApplistRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mApplistRecycler.setAdapter(mLauncherApplistingAdapter);
        mApplistRecycler.addItemDecoration(new EqualSpaceDecorator(AppDefaults.TILE_LIST_MARGIN));
        EditText searchField = view.findViewById(R.id.fragment_launcher_applist_et_search);
        searchField.setHighlightColor(AppDefaults.THEME_ACCENT_COLOR);
        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals("")) {
                    filter(s.toString());
                } else {
                    loadApps();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void filter(String search) {
        mApps.clear();
        for (App app : mInstalledApps)
            // Do not use trim. Java does not handle the removing of whitespaces correctly. Use the 'replace' function instead... ò_ó .
            if (app.getName().toString().toLowerCase().replace(" ", "").contains(search.toLowerCase().replace(" ", "")))
                mApps.add(app);
        mLauncherApplistingAdapter.notifyDataSetChanged();
    }

    private void loadApps() {
        mInstalledApps.clear();
        mApps.clear();

        // Handle for modern Android versions that include work profile apps.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            LauncherApps launcher = (LauncherApps) Objects.requireNonNull(getActivity()).getSystemService(Context.LAUNCHER_APPS_SERVICE);
            UserHandle profile = launcher.getProfiles().get(mSelectedProfile);
            List<LauncherActivityInfo> launcherActivityInfos = launcher.getActivityList(null, profile);
            launcherActivityInfos.forEach(activityInfo -> {
                App app = new App();
                app.setAppPackage(activityInfo.getActivityInfo().packageName);
                app.setComponentName(activityInfo.getComponentName());
                app.setProfileIndex(mSelectedProfile);
                String unicodeIdEmoji = mSelectedProfile == 0 ? "" : "\uD83D\uDCBC ";
                app.setName(unicodeIdEmoji+activityInfo.getActivityInfo().loadLabel(mPackageManager));
                app.setAppIcon(activityInfo.getActivityInfo().loadIcon(mPackageManager));
                app.setAppIcon(activityInfo.getIcon(0));
                mInstalledApps.add(app);
            });
        }
        // Handle for older Android versions that do not include work profile apps.
        else {
            Intent intent = new Intent(Intent.ACTION_MAIN, null);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            List<ResolveInfo> availableActivities = mPackageManager.queryIntentActivities(intent, 0);
            for (ResolveInfo resolveInfo : availableActivities) {
                App app = new App();
                app.setAppPackage(resolveInfo.activityInfo.packageName);
                app.setName(resolveInfo.loadLabel(mPackageManager));
                app.setAppIcon(resolveInfo.loadIcon(mPackageManager));
                mInstalledApps.add(app);
            }
        }

        mApps.addAll(mInstalledApps);
        Collections.sort(mApps, new Comparator<App>() {
            @Override
            public int compare(App o1, App o2) {
                return o1.getName().toString().compareTo(o2.getName().toString());
            }
        });

        mLauncherApplistingAdapter.notifyDataSetChanged();
    }

    @Override
    public void clickedItem(Context context, Object object, int position) {
        if (object instanceof App) {
            App app = (App) object;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            {
                LauncherApps launcher = (LauncherApps) Objects.requireNonNull(getActivity()).getSystemService(Context.LAUNCHER_APPS_SERVICE);
                UserHandle profile = launcher.getProfiles().get(app.getProfileIndex());
                launcher.startMainActivity(app.getComponentName(), profile, null, null);
            }
            else {
                Intent launchIntent = mPackageManager.getLaunchIntentForPackage(app.getAppPackage().toString());
                startActivity(launchIntent);
            }
        }
    }

    @Override
    public void longClickedItem(Context context, Object object, int position) {
        if (object instanceof App) {

        }
    }
}
