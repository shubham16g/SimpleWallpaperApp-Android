package com.wallpaperapp.com;

import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.getDefaultNightMode());
        final String[] themesLatest = getResources().getStringArray(R.array.theme_values_latest);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String newVal = prefs.getString("theme", null);
        if (newVal == null) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.getDefaultNightMode());
        } else if (newVal.equals(themesLatest[0])) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else if (newVal.equals(themesLatest[1])) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else if (newVal.equals(themesLatest[2])) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        } else if (newVal.equals(themesLatest[3])) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY);
        }
    }

}
