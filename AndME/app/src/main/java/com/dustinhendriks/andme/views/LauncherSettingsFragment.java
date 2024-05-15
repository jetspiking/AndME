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
import com.dustinhendriks.andme.utils.DefaultIconPack;
import com.dustinhendriks.andme.utils.IconPack;
import com.dustinhendriks.andme.utils.IconPackLoader;
import com.dustinhendriks.andme.utils.SerializationUtils;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Handles creating and displaying the application settings and handling actions.
 */
public class LauncherSettingsFragment extends Fragment {
    // Color name and value mappings
    private static final String COLOR_DARK = "Dark";
    private static final String COLOR_LIGHT = "Light";
    private static final String COLOR_MAGENTA = "Magenta";
    private static final String COLOR_PURPLE = "Purple";
    private static final String COLOR_TEAL = "Teal";
    private static final String COLOR_LIME = "Lime";
    private static final String COLOR_BROWN = "Brown";
    private static final String COLOR_PINK = "Pink";
    private static final String COLOR_ORANGE = "Orange";
    private static final String COLOR_BLUE = "Blue";
    private static final String COLOR_RED = "Red";
    private static final String COLOR_GREEN = "Green";
    private static final String HEX_DARK = "FF000000";
    private static final String HEX_LIGHT = "FFFFFFFF";
    private static final String HEX_MAGENTA = "FFFF0097";
    private static final String HEX_PURPLE = "FFA200FF";
    private static final String HEX_TEAL = "FF00ABA9";
    private static final String HEX_LIME = "FF8CBF26";
    private static final String HEX_BROWN = "FF996600";
    private static final String HEX_PINK = "FFFF0097";
    private static final String HEX_ORANGE = "FFF09609";
    private static final String HEX_BLUE = "FF1BA1E2";
    private static final String HEX_RED = "FFE51400";
    private static final String HEX_GREEN = "FF339933";
    private static final String BOOLEAN_TRUE = "True";
    private static final String BOOLEAN_FALSE = "False";

    private View mSettingsView;
    private String mIconPack;

    private boolean mInitialSetupCall = true;

    private final Map<String, String> colorNameToHexMap = new HashMap<>();

    public LauncherSettingsFragment() {
        initializeColorMaps();
    }

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

        mIconPack = AppMiscDefaults.APPLIED_ICON_PACK_BINDING;

        View tileCountSettingsView = mSettingsView.findViewById(R.id.fragment_launcher_content_is_tilecount);
        View accentColorSettingsView = mSettingsView.findViewById(R.id.fragment_launcher_content_is_accentcolor);
        View backgroundColorSettingsView = mSettingsView.findViewById(R.id.fragment_launcher_content_is_backgroundcolor);
        View textColorSettingsView = mSettingsView.findViewById(R.id.fragment_launcher_content_is_textcolor);
        View showAppTilesSettingsView = mSettingsView.findViewById(R.id.fragment_launcher_content_is_showapptiles);
        View showNavigationBarSettingsView = mSettingsView.findViewById(R.id.fragment_launcher_content_is_shownavigationbar);
        View showSystemWallpaperSettingsView = mSettingsView.findViewById(R.id.fragment_launcher_content_is_showsystemwallpaper);
        View iconPackSettingsView = mSettingsView.findViewById(R.id.fragment_launcher_content_is_iconpack);
        Spinner iconPackSpinner = iconPackSettingsView.findViewById(R.id.item_launcher_inputsetting_sp_option);

        setViewTitleAndInput(accentColorSettingsView, getString(R.string.settings_accent_color), getColorNames(), Integer.toHexString(AppMiscDefaults.ACCENT_COLOR));
        setViewTitleAndInput(backgroundColorSettingsView, getString(R.string.settings_background_color), getColorNames(), Integer.toHexString(AppMiscDefaults.BACKGROUND_COLOR));
        setViewTitleAndInput(textColorSettingsView, getString(R.string.settings_text_color), getColorNames(), Integer.toHexString(AppMiscDefaults.TEXT_COLOR));
        setViewTitleAndInput(tileCountSettingsView, getString(R.string.settings_tile_span), getTileSpanOptions(), String.valueOf(AppMiscDefaults.TILE_SPAN_COUNT));
        setViewTitleAndInput(showAppTilesSettingsView, getString(R.string.settings_show_icons_app_list), getBooleanOptions(), String.valueOf(AppMiscDefaults.SHOW_ICONS_IN_APPS_LIST));
        setViewTitleAndInput(showNavigationBarSettingsView, getString(R.string.settings_show_navigation_bar), getBooleanOptions(), String.valueOf(AppMiscDefaults.SHOW_NAVIGATION_BAR));
        setViewTitleAndInput(showSystemWallpaperSettingsView, getString(R.string.settings_show_system_wallpaper), getBooleanOptions(), String.valueOf(AppMiscDefaults.SHOW_SYSTEM_WALLPAPER));
        setViewTitleAndInput(iconPackSettingsView, getString(R.string.settings_select_icon_pack), getIconPackOptions(), AppMiscDefaults.APPLIED_ICON_PACK_NAME);

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
                if (!AppMiscDefaults.APPLIED_ICON_PACK_BINDING.isEmpty())
                    input.setText(AppMiscDefaults.APPLIED_ICON_PACK_BINDING);
                else input.setText(DefaultIconPack.getDefaultIconPack());

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
                String accentColor = colorNameToHexMap.get(getViewInput(accentColorSettingsView));
                String backgroundColor = colorNameToHexMap.get(getViewInput(backgroundColorSettingsView));
                String textColor = colorNameToHexMap.get(getViewInput(textColorSettingsView));
                AppMiscDefaults.TILE_SPAN_COUNT = Integer.parseInt(getViewInput(tileCountSettingsView));
                AppMiscDefaults.ACCENT_COLOR = getColorFromHex(accentColor);
                AppMiscDefaults.BACKGROUND_COLOR = getColorFromHex(backgroundColor);
                AppMiscDefaults.TEXT_COLOR = getColorFromHex(textColor);
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
        if (hexValue == null || hexValue.length() != 8) {
            throw new IllegalArgumentException("Invalid hex color value");
        }
        return (Integer.parseInt(hexValue.substring(0, 2), 16) << 24) + Integer.parseInt(hexValue.substring(2), 16);
    }

    /**
     * Set color and text for item in settings menu.
     */
    private void setViewTitleAndInput(View view, String title, String[] options, String currentValue) {
        TextView titleTextView = view.findViewById(R.id.item_launcher_inputsetting_tv_title);
        Spinner inputSpinner = view.findViewById(R.id.item_launcher_inputsetting_sp_option);

        titleTextView.setText(title);
        titleTextView.setTextColor(Color.WHITE);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(Objects.requireNonNull(getActivity()), android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        inputSpinner.setAdapter(adapter);

        // Resolve the current value to a color name if it's a hex value
        String resolvedValue = getColorNameFromHex(currentValue.toUpperCase());

        if (resolvedValue == null) resolvedValue = currentValue;

        // Set spinner selection to resolvedValue
        for (int i = 0; i < options.length; i++) {
            if (options[i].equalsIgnoreCase(resolvedValue)) {
                inputSpinner.setSelection(i);
                break;
            }
        }
    }

    /**
     * Get String from input field.
     */
    private String getViewInput(View view) {
        Spinner inputSpinner = view.findViewById(R.id.item_launcher_inputsetting_sp_option);
        return inputSpinner.getSelectedItem().toString();
    }

    /**
     * Get boolean options for the spinner.
     */
    private String[] getBooleanOptions() {
        return new String[]{BOOLEAN_TRUE, BOOLEAN_FALSE};
    }

    /**
     * Get tile span options for the spinner.
     */
    private String[] getTileSpanOptions() {
        return new String[]{"1", "2", "3", "4", "5"};
    }

    /**
     * Get color names for the spinner.
     */
    private String[] getColorNames() {
        return new String[]{
                COLOR_DARK, COLOR_LIGHT,
                COLOR_MAGENTA, COLOR_PURPLE, COLOR_TEAL, COLOR_LIME,
                COLOR_BROWN, COLOR_PINK, COLOR_ORANGE, COLOR_BLUE,
                COLOR_RED, COLOR_GREEN
        };
    }

    /**
     * Get icon pack options for the spinner.
     */
    private String[] getIconPackOptions() {
        return IconPackLoader.getAvailableIconPacks().toArray(new String[0]);
    }

    /**
     * Initialize the color name to hex map.
     */
    private void initializeColorMaps() {
        colorNameToHexMap.put(COLOR_DARK, HEX_DARK);
        colorNameToHexMap.put(COLOR_LIGHT, HEX_LIGHT);
        colorNameToHexMap.put(COLOR_BLUE, HEX_BLUE);
        colorNameToHexMap.put(COLOR_BROWN, HEX_BROWN);
        colorNameToHexMap.put(COLOR_GREEN, HEX_GREEN);
        colorNameToHexMap.put(COLOR_LIME, HEX_LIME);
        colorNameToHexMap.put(COLOR_MAGENTA, HEX_MAGENTA);
        colorNameToHexMap.put(COLOR_ORANGE, HEX_ORANGE);
        colorNameToHexMap.put(COLOR_PINK, HEX_PINK);
        colorNameToHexMap.put(COLOR_PURPLE, HEX_PURPLE);
        colorNameToHexMap.put(COLOR_RED, HEX_RED);
        colorNameToHexMap.put(COLOR_TEAL, HEX_TEAL);
    }

    /**
     * Retrieve the color name corresponding to the given hex value.
     */
    private String getColorNameFromHex(String hexValue) {
        for (Map.Entry<String, String> entry : colorNameToHexMap.entrySet()) {
            if (entry.getValue().equalsIgnoreCase(hexValue)) {
                return entry.getKey();
            }
        }
        return null;
    }
}
