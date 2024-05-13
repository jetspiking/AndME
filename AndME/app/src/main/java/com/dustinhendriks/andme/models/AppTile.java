package com.dustinhendriks.andme.models;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.LauncherApps;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.UserHandle;
import com.dustinhendriks.andme.interfaces.LaunchableTile;
import java.io.Serializable;

/**
 * Includes the launch function for starting an application as new activity.
 */
public class AppTile extends Tile implements LaunchableTile<Activity>, Serializable {
    private final App app;

    /**
     * AppTile to create.
     * @param name Tile name.
     * @param width Tile width.
     * @param height Tile height.
     * @param app Linked app.
     */
    public AppTile(String name, int width, int height, App app) {
        super(name, width, height);
        this.app = app;
    }

    /**
     * Get the app.
     * @return App.
     */
    public App getApp() {
        return app;
    }

    /**
     * Launch the app corresponding to the tile.
     * @param parameter Generic parameter type depending on usage.
     */
    @Override
    public void launch(Activity parameter) {
        PackageManager packageManager = parameter.getPackageManager();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
        {
            LauncherApps launcher = (LauncherApps) parameter.getSystemService(Context.LAUNCHER_APPS_SERVICE);
            UserHandle profile = launcher.getProfiles().get(app.getProfileIndex());
            launcher.startMainActivity(app.getComponentName(), profile, null, null);
        }
        else {
            Intent launchIntent = packageManager.getLaunchIntentForPackage(app.getAppPackage().toString());
            parameter.startActivity(launchIntent);
        }
    }
}
