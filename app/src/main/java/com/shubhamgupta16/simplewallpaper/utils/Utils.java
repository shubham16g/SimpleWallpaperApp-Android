package com.shubhamgupta16.simplewallpaper.utils;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

import com.shubhamgupta16.simplewallpaper.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class Utils {

    public static void initTheme(Context context){
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.getDefaultNightMode());
        final String[] themesLatest = context.getResources().getStringArray(R.array.theme_values_latest);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
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

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static boolean save(Context context, Bitmap bm, String appDir, String name) {
        Log.d("TAG", "save: called");
        OutputStream stream = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, name);
            values.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
            values.put(
                    MediaStore.MediaColumns.RELATIVE_PATH,
                    Environment.DIRECTORY_PICTURES + "/" + appDir
            );
            Uri uri = context.getContentResolver().insert(
                    MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY),
                    values);
            if (uri == null) return false;
            try {
                stream = context.getContentResolver().openOutputStream(uri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || context.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                File imagesDir =
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/" + appDir);
                if (!imagesDir.exists()) {
                    boolean b = imagesDir.mkdirs();
                }
                File image = new File(imagesDir, name + ".jpg");
                try {
                    stream = new FileOutputStream(image);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        if (stream == null) return false;
        bm.compress(Bitmap.CompressFormat.JPEG, 95, stream);
        return true;
    }
}
