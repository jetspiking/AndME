package com.dustinhendriks.andme.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.dustinhendriks.andme.MainActivity;
import com.dustinhendriks.andme.R;

import java.util.Objects;

/**
 * Handles creating the fragment responsible for showing the pinned applications and displays added sidebar buttons.
 */
public class LauncherTilesHomeFragment extends Fragment {
    private View view;
    public LauncherTilesFragment mLauncherTilesFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_launcher_tilehome, container, false);
        this.view = view;

        if (savedInstanceState == null) {
            mLauncherTilesFragment = new LauncherTilesFragment();
            FragmentTransaction ft = Objects.requireNonNull(getFragmentManager()).beginTransaction();
            ft.add(R.id.fragment_launcher_tilehome_fl_tiles, mLauncherTilesFragment).commit();
        }

        ImageView navigationView = view.findViewById(R.id.fragment_launcher_tilehome_iv_arrowbutton);
        navigationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.scrollToApps();
            }
        });

        return view;
    }

    public View getView() {
        return view;
    }
}
