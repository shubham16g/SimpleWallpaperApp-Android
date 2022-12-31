package com.shubhamgupta16.simplewallpaper.utils;

import android.content.Context;

import com.shubhamgupta16.simplewallpaper.models.CategoryPOJO;
import com.shubhamgupta16.simplewallpaper.models.WallsPOJO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class InitSQL {
    private final Context context;
    private final SQLHelper sqlHelper;

    public InitSQL(Context context, SQLHelper sqlHelper) {
        this.context = context;
        this.sqlHelper = sqlHelper;
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
                sqlHelper.insertCategory(pojo);
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
                        0,
                        object.getString("name"),
                        object.getString("previewUrl"),
                        object.getString("url"),
                        object.getString("categories"),
                        object.optBoolean("premium", false)
                );
                sqlHelper.insertWallpaper(pojo);
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
