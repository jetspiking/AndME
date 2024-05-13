package com.dustinhendriks.andme.models;

import android.content.ComponentName;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

import com.dustinhendriks.andme.utils.Utilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;

/**
 * Represents an application installed on the device. And keeps track of required data and arguments.
 */
public class App implements Serializable {
    private CharSequence name;
    private CharSequence appPackage;
    private transient Drawable appIcon;
    private String componentPackage;
    private String componentClass;
    private int profileIndex;
    private String iconLocation;

    /**
     * Get app name.
     * @return App name.
     */
    public CharSequence getName() {
        return name;
    }

    /**
     * Set app name.
     * @param name App name.
     */
    public void setName(CharSequence name) {
        this.name = name;
    }

    /**
     * Get app package name.
     * @return App package name.
     */
    public CharSequence getAppPackage() {
        return appPackage;
    }

    /**
     * Set app package name.
     * @param appPackage App package name.
     */
    public void setAppPackage(CharSequence appPackage) {
        this.appPackage = appPackage;
    }

    /**
     * Get app icon.
     * @return App icon.
     */
    public Drawable getAppIcon() {
        return appIcon;
    }

    /**
     * Set app icon.
     * @param appIcon App icon.
     */
    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }

    /**
     * Cache the app icon.
     * @param context Application context.
     */
    public void cacheIcon(Context context) {
        if (iconLocation==null)
            new File(context.getApplicationInfo().dataDir+"/cachedApps/").mkdirs();
        if (appIcon!=null) {
            iconLocation = context.getApplicationInfo().dataDir+"/cachedApps/"+appPackage+name;
            FileOutputStream fileOutputStream = null;
            try{
                fileOutputStream = new FileOutputStream(iconLocation);
            }catch(FileNotFoundException e){
                e.printStackTrace();
            }
            if (fileOutputStream!=null){
                Utilities.drawableToBitmap(appIcon).compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                try{
                    fileOutputStream.flush();
                    fileOutputStream.close();
                }catch(IOException e){
                    e.printStackTrace();
                }
            }else
                iconLocation = null;
        }
    }

    /**
     * Get the cached app icon.
     * @return Cached app icon.
     */
    public Bitmap getCachedIcon() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inDither = true;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        File cachedIcon = iconLocation!=null ? new File(iconLocation) : null;
        return (cachedIcon!=null && cachedIcon.exists()) ? BitmapFactory.decodeFile(iconLocation) : null;
    }

    /**
     * Get component name.
     * @return Component name.
     */
    public ComponentName getComponentName() {
        return new ComponentName(this.componentPackage, this.componentClass);
    }

    /**
     * Set component name.
     * @param componentName Component name.
     */
    public void setComponentName(ComponentName componentName) {
        this.componentClass = componentName.getClassName();
        this.componentPackage = componentName.getPackageName();
    }

    /**
     * Get the profile index.
     * @return Profile index.
     */
    public int getProfileIndex() {
        return profileIndex;
    }

    /**
     * Set the profile index.
     * @param profileIndex Profile index.
     */
    public void setProfileIndex(int profileIndex) {
        this.profileIndex = profileIndex;
    }
}
