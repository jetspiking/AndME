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
    private App app;

    public AppTile(Builder builder) {
        super(builder);
    }

    public App getApp() {
        return app;
    }

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

    public static class Builder<T> extends Tile.Builder<Builder<T>> {
        private App app;

        public Builder(String name) {
            super(name);
        }

        public Builder<T> withApp(App app) {
            this.app = app;
            return getThis();
        }

        public AppTile build() {
            AppTile appTile = new AppTile(this);
            appTile.app = this.app;
            return appTile;
        }

        @Override
        public Builder<T> getThis() {
            return this;
        }
    }
}
