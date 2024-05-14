package com.dustinhendriks.andme.views;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.dustinhendriks.andme.MainActivity;
import com.dustinhendriks.andme.R;
import com.dustinhendriks.andme.models.AppSerializableData;
import com.dustinhendriks.andme.utils.AppMiscDefaults;
import com.dustinhendriks.andme.utils.IconPack;
import com.dustinhendriks.andme.utils.IconPackLoader;
import com.dustinhendriks.andme.utils.SerializationUtils;
import com.google.gson.Gson;

import java.util.List;
import java.util.Objects;

/**
 * Handles creating and displaying the application settings and handling actions.
 */
public class LauncherSettingsFragment extends Fragment {
    private View mSettingsView;
    private String mIconPack;

    private boolean mInitialSetupCall = true;

    /**
     * Overridden onCreateView method called when creating the view.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mSettingsView = inflater.inflate(R.layout.fragment_launcher_settings, container, false);
        loadSettings();
        return mSettingsView;
    }

    /**
     * Load application settings.
     */
    private void loadSettings() {
        mSettingsView.setBackgroundColor(Color.BLACK);

        AppSerializableData appSerializableData = SerializationUtils.DeserializedData(Objects.requireNonNull(getContext()));
        if (appSerializableData != null)
            AppMiscDefaults.RestoreFromSerializedData(appSerializableData);

        View tileCountSettingsView = mSettingsView.findViewById(R.id.fragment_launcher_content_is_tilecount);
        View accentColorSettingsView = mSettingsView.findViewById(R.id.fragment_launcher_content_is_accentcolor);
        View backgroundColorSettingsView = mSettingsView.findViewById(R.id.fragment_launcher_content_is_backgroundcolor);
        View textColorSettingsView = mSettingsView.findViewById(R.id.fragment_launcher_content_is_textcolor);
        View showAppTilesSettingsView = mSettingsView.findViewById(R.id.fragment_launcher_content_is_showapptiles);
        View showNavigationBarSettingsView = mSettingsView.findViewById(R.id.fragment_launcher_content_is_shownavigationbar);
        View showSystemWallpaperSettingsView = mSettingsView.findViewById(R.id.fragment_launcher_content_is_showsystemwallpaper);
        Spinner iconPackSpinner = mSettingsView.findViewById(R.id.fragment_launcher_content_sp_iconpack);

        setViewTitleAndInput(accentColorSettingsView, getString(R.string.settings_accent_color), Integer.toHexString(AppMiscDefaults.ACCENT_COLOR));
        setViewTitleAndInput(backgroundColorSettingsView, getString(R.string.settings_background_color), Integer.toHexString(AppMiscDefaults.BACKGROUND_COLOR));
        setViewTitleAndInput(textColorSettingsView, getString(R.string.settings_text_color), Integer.toHexString((AppMiscDefaults.TEXT_COLOR)));

        setViewTitleAndInput(tileCountSettingsView, getString(R.string.settings_tile_span), String.valueOf(AppMiscDefaults.TILE_SPAN_COUNT));
        setViewTitleAndInput(showAppTilesSettingsView, getString(R.string.settings_show_icons_app_list), String.valueOf(AppMiscDefaults.SHOW_ICONS_IN_APPS_LIST));
        setViewTitleAndInput(showNavigationBarSettingsView, getString(R.string.settings_show_navigation_bar), String.valueOf(AppMiscDefaults.SHOW_NAVIGATION_BAR));
        setViewTitleAndInput(showSystemWallpaperSettingsView, getString(R.string.settings_show_system_wallpaper), String.valueOf(AppMiscDefaults.SHOW_SYSTEM_WALLPAPER));

        List<String> iconPacks = IconPackLoader.getAvailableIconPacks();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, iconPacks);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        iconPackSpinner.setAdapter(adapter);

        for (int i = 0; i < iconPacks.size(); i++)
            if (AppMiscDefaults.APPLIED_ICON_PACK_NAME.equals(iconPacks.get(i))) {
                iconPackSpinner.setSelection(i);
                break;
            }

        iconPackSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mInitialSetupCall) {
                    mInitialSetupCall = false;
                    return;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(getString(R.string.settings_enter_theme_mapping));

                final EditText input = new EditText(getContext());
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                builder.setView(input);

                builder.setPositiveButton(getString(R.string.settings_ok_theme_mapping), (dialog, which) -> {
                    mIconPack = input.getText().toString();
                });

                builder.setNegativeButton(R.string.cancel_settings, (dialog, which) -> dialog.cancel());

                builder.show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        Button saveAndReload = mSettingsView.findViewById(R.id.fragment_launcher_content_bu_saveandreload);
        saveAndReload.setTextColor(Color.WHITE);
        saveAndReload.setOnClickListener(view -> {
            LongPressDialogFragment longPressDialogFragment = new LongPressDialogFragment(getContext(), getString(R.string.settings_save_and_overwrite));
            longPressDialogFragment.subscribeOption1(v -> {
                AppMiscDefaults.TILE_SPAN_COUNT = Integer.parseInt(getViewInput(tileCountSettingsView));
                AppMiscDefaults.ACCENT_COLOR = getColorFromHex(getViewInput(accentColorSettingsView));
                AppMiscDefaults.BACKGROUND_COLOR = getColorFromHex(getViewInput(backgroundColorSettingsView));
                AppMiscDefaults.TEXT_COLOR = getColorFromHex(getViewInput(textColorSettingsView));
                AppMiscDefaults.SHOW_ICONS_IN_APPS_LIST = Boolean.parseBoolean(getViewInput(showAppTilesSettingsView));
                AppMiscDefaults.SHOW_NAVIGATION_BAR = Boolean.parseBoolean(getViewInput(showNavigationBarSettingsView));
                AppMiscDefaults.SHOW_SYSTEM_WALLPAPER = Boolean.parseBoolean(getViewInput(showSystemWallpaperSettingsView));
                AppMiscDefaults.APPLIED_ICON_PACK_NAME = iconPackSpinner.getSelectedItem().toString();
                AppMiscDefaults.APPLIED_ICON_PACK_BINDING = mIconPack;
                MainActivity.serializeData();
                MainActivity.reloadLauncher();
            });
            longPressDialogFragment.show(0);
        });

        Button cancelAndClose = mSettingsView.findViewById(R.id.fragment_launcher_content_is_cancelandclose);
        cancelAndClose.setTextColor(Color.WHITE);
        cancelAndClose.setOnClickListener(view -> MainActivity.scrollToHome());
    }

    /**
     * Retrieve color from hexadecimal value.
     */
    private int getColorFromHex(String hexValue) {
        return (Integer.parseInt(hexValue.substring(0, 2), 16) << 24) + Integer.parseInt(hexValue.substring(2), 16);
    }

    /**
     * Set color and text for item in settings menu.
     */
    private void setViewTitleAndInput(View view, String title, String input) {
        TextView titleTextView = view.findViewById(R.id.item_launcher_inputsetting_tv_title);
        TextView inputTextView = view.findViewById(R.id.item_launcher_inputsetting_et_input);
        // We do not want to display accent and configured colors, to prevent messing up the settings screen by wrongly configured parameters
        // (for example when background color equals text color no settings could be adjusted)
        titleTextView.setText(title);
        titleTextView.setTextColor(Color.WHITE);
        inputTextView.setText(input);
        inputTextView.setTextColor(Color.WHITE);
    }

    /**
     * Get String from input field.
     */
    private String getViewInput(View view) {
        TextView inputTextView = view.findViewById(R.id.item_launcher_inputsetting_et_input);
        return inputTextView.getText().toString();
    }
}
