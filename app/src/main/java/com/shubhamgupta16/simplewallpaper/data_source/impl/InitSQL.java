package com.shubhamgupta16.simplewallpaper.data_source.impl;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.shubhamgupta16.simplewallpaper.BuildConfig;
import com.shubhamgupta16.simplewallpaper.data_source.PreserveOldFav;
import com.shubhamgupta16.simplewallpaper.data_source.SQLCategories;
import com.shubhamgupta16.simplewallpaper.models.CategoryPOJO;
import com.shubhamgupta16.simplewallpaper.models.WallsPOJO;
import com.shubhamgupta16.simplewallpaper.utils.SQLFav;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public abstract class InitSQL {
    public static void applyWallpapers(Context context, InternalSQLWallpapers sqlWallpapers, SQLFav sqlFav) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        int version = prefs.getInt("version", 0);
        if (version != BuildConfig.VERSION_CODE) {
            sqlWallpapers.clearAll();
            setupWallpapers(context, sqlWallpapers);
            PreserveOldFav.apply(context, sqlFav);
            filterFavorites(sqlFav, sqlWallpapers);
            prefs.edit().putInt("version", BuildConfig.VERSION_CODE).apply();
        }
    }

    public static void applyCategories(Context context, InternalSQLWallpapers sqlWallpapers, SQLCategories sqlCategories) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        int version = prefs.getInt("version", 0);
        if (version != BuildConfig.VERSION_CODE) {
            sqlCategories.clearAll();
            setupCategories(context, sqlCategories, sqlWallpapers);
            prefs.edit().putInt("version", BuildConfig.VERSION_CODE).apply();
        }
    }

    private static void filterFavorites(SQLFav fav, InternalSQLWallpapers sqlWallpapers) {
        for (WallsPOJO pojo : fav.getAllWallpapers()) {
            if (!sqlWallpapers.isExist(pojo.getUrl())) {
                fav.toggleFavorite(pojo, false);
            }
        }
    }

    private static void setupCategories(Context context, SQLCategories sqlCategories, InternalSQLWallpapers sqlWallpapers) {
        try {
            JSONArray array = new JSONArray(readJSONFromAsset(context,"categories"));
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                CategoryPOJO pojo = new CategoryPOJO(
                        object.getString("name"),
                        object.getString("preview1"),
                        object.getString("preview2"),
                        object.getString("preview3"),
                        sqlWallpapers.getWallsInCategoryCount(object.getString("name")));
                sqlCategories.insertCategory(pojo);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static void setupWallpapers(Context context, InternalSQLWallpapers sqlWallpapers) {
        try {
            JSONArray array = new JSONArray(readJSONFromAsset(context,"wallpapers"));
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                WallsPOJO pojo = new WallsPOJO(
                        object.getString("url"),
                        object.getString("name"),
                        object.getString("previewUrl"),
                        object.getString("categories"),
                        object.optBoolean("premium", false)
                );
                sqlWallpapers.insertWallpaper(pojo);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static String readJSONFromAsset(Context context, String fileName) {
        String json;
        try {
            InputStream is = context.getAssets().open(fileName + ".json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
