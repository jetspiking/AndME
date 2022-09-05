package com.dustinhendriks.andme.views;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.Guideline;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.dustinhendriks.andme.MainActivity;
import com.dustinhendriks.andme.R;

public class LauncherTilesHomeFragment extends Fragment {
    private static final String TAG = "LauncherTilesHomeFragme";
    private View view;
    public LauncherTilesFragment mLauncherTilesFragment;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: Creating LauncherTilesHomeFragment view.");
        View view = inflater.inflate(R.layout.fragment_launcher_tilehome, container, false);
        this.view = view;
        FrameLayout frame = view.findViewById(R.id.fragment_launcher_tilehome_fl_tiles);
        frame.setId(R.id.fragment_launcher_tilehome_fl_tiles);

        if (savedInstanceState == null) {
            mLauncherTilesFragment = new LauncherTilesFragment();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.add(R.id.fragment_launcher_tilehome_fl_tiles, mLauncherTilesFragment).commit();
        }

        //hideSearchButton();

        ImageView navigationView = view.findViewById( R.id.fragment_launcher_tilehome_iv_arrowbutton);
        navigationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.backToApps();
            }
        });

        return view;
    }

    public void hideSearchButton() {
        Guideline guideline = this.view.findViewById(R.id.fragment_launcher_tilehome_gl_guideline);
        guideline.setGuidelineEnd(20);
    }

    public View getView() {
        return view;
    }
}
