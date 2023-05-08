package com.shubhamgupta16.simplewallpaper.data_source;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.shubhamgupta16.simplewallpaper.BuildConfig;
import com.shubhamgupta16.simplewallpaper.models.CategoryPOJO;
import com.shubhamgupta16.simplewallpaper.models.WallsPOJO;
import com.shubhamgupta16.simplewallpaper.utils.SQLFav;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class InitSQL {
    private final Context context;
    private final SQLWallpapers sqlWallpapers;

    public static void apply(Context context, SQLWallpapers sqlWallpapers, SQLFav sqlFav) {
        new InitSQL(context, sqlWallpapers, sqlFav);
    }
    private InitSQL(Context context, SQLWallpapers sqlWallpapers, SQLFav sqlFav) {
        this.context = context;
        this.sqlWallpapers = sqlWallpapers;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        int version = prefs.getInt("version",0);
        if (version != BuildConfig.VERSION_CODE) {
            sqlWallpapers.clearAll();
            setupCategories();
            setupWallpapers();
            PreserveOldFav.apply(context, sqlFav);
            filterFavorites(sqlFav);
            prefs.edit().putInt("version", BuildConfig.VERSION_CODE).apply();
        }
    }

    public void filterFavorites(SQLFav fav) {
        for (WallsPOJO pojo : fav.getAllWallpapers()) {
            if (!sqlWallpapers.isExist(pojo.getUrl())) {
                fav.toggleFavorite(pojo, false);
            }
        }
    }

    public void setupCategories() {
        try {
            JSONArray array = new JSONArray(readJSONFromAsset("categories"));
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                CategoryPOJO pojo = new CategoryPOJO(
                        object.getString("name"),
                        object.getString("preview1"),
                        object.getString("preview2"),
                        object.getString("preview3"),
                        0);
                sqlWallpapers.insertCategory(pojo);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setupWallpapers() {
        try {
            JSONArray array = new JSONArray(readJSONFromAsset("wallpapers"));
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
    private String readJSONFromAsset(String fileName) {
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
