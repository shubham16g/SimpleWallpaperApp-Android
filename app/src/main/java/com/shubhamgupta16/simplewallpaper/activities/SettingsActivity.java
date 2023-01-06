package com.shubhamgupta16.simplewallpaper.activities;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;

import com.shubhamgupta16.simplewallpaper.R;


public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(view -> finish());
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @SuppressLint("SwitchIntDef")
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            final String [] themesLatest = getResources().getStringArray(R.array.theme_values_latest);
            final String [] themes = getResources().getStringArray(R.array.theme_values);
            final String [] themeEntries = getResources().getStringArray(R.array.theme_entries);
            final String [] themeEntriesLatest = getResources().getStringArray(R.array.theme_entries_latest);

            final ListPreference themePref = findPreference("theme");
            assert themePref != null;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                themePref.setEntries(themeEntriesLatest);
                themePref.setEntryValues(themesLatest);
            } else {
                themePref.setEntries(themeEntries);
                themePref.setEntryValues(themes);
            }
            switch (AppCompatDelegate.getDefaultNightMode()) {
                case AppCompatDelegate.MODE_NIGHT_NO:
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    themePref.setValue(themesLatest[0]);
                    break;
                case AppCompatDelegate.MODE_NIGHT_YES:
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    themePref.setValue(themesLatest[1]);
                    break;
                case AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY:
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY);
                    themePref.setValue(themesLatest[3]);
                    break;
                case AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM:
                case AppCompatDelegate.MODE_NIGHT_UNSPECIFIED:
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                    themePref.setValue(themesLatest[2]);
                    break;
            }
            themePref.setOnPreferenceChangeListener((preference, newValue) -> {
                String newVal = (String) newValue;
                if (newVal.equals(themesLatest[0])){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                } else if (newVal.equals(themesLatest[1])){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else if (newVal.equals(themesLatest[2])){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                }else if (newVal.equals(themesLatest[3])){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY);
                }
                ((SettingsActivity) requireActivity()).getDelegate().applyDayNight();
                return true;
            });
        }
    }
}