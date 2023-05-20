package com.shubhamgupta16.simplewallpaper.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.shubhamgupta16.simplewallpaper.models.WallsPOJO;

import java.util.ArrayList;
import java.util.List;

public class SQLFav extends SQLiteOpenHelper {
    private static final String FAVORITES = "favs";
    private static final String DB_NAME = "000Fav";

    private static final int PER_PAGE_ITEM = 16;


    public SQLFav(@Nullable Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + FAVORITES + "(url VARCHAR PRIMARY KEY, previewUrl VARCHAR, name VARCHAR, categories VARCHAR, premium INTEGER, color VARCHAR, colorCode VARCHAR);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    public List<WallsPOJO> getAllWallpapers() {
        SQLiteDatabase db = getWritableDatabase();
        ArrayList<WallsPOJO> list = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + FAVORITES, null);
        while (cursor.moveToNext()) {
            list.add(new WallsPOJO(cursor.getString(0), cursor.getString(2), cursor.getString(1), cursor.getString(3), cursor.getInt(5) != 0));
        }
        cursor.close();
        return list;
    }

    public ArrayList<WallsPOJO> getWallpapers(int page) {
        int offset = (page - 1) * PER_PAGE_ITEM;

        ArrayList<WallsPOJO> list = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + FAVORITES + " LIMIT " + offset + ", " + PER_PAGE_ITEM, null);
        while (cursor.moveToNext()) {
            list.add(new WallsPOJO(cursor.getString(0), cursor.getString(2), cursor.getString(1), cursor.getString(3), cursor.getInt(5) != 0));
        }
        cursor.close();
        return list;
    }

    public int getPagesCount() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT count(*) FROM " + FAVORITES, null);
        if (c.moveToFirst()) {
            int count = c.getInt(0);
            c.close();

            return (int) Math.ceil(count / (float) PER_PAGE_ITEM);
        } else {
            c.close();
            return 0;
        }
    }


    public void toggleFavorite(WallsPOJO wall, boolean favorite) {
        SQLiteDatabase db = this.getWritableDatabase();
        boolean isFavorite = isFavorite(wall.getUrl());
        if (isFavorite == favorite) {
            return;
        }
        if (!favorite) {
            db.delete(FAVORITES, "url='" + wall.getUrl() + "'", null);
            return;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put("previewUrl", wall.getPreviewUrl());
        contentValues.put("name", wall.getName());
        contentValues.put("categories", wall.getCategories());
        contentValues.put("premium", wall.isPremium() ? 1 : 0);
        contentValues.put("url", wall.getUrl());
        db.insert(FAVORITES, null, contentValues);

    }

    public boolean isFavorite(String url) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT count(*) FROM " + FAVORITES + " WHERE url='" + url + "'", null);
        if (c.moveToFirst()) {
            int count = c.getInt(0);
            c.close();

            return count != 0;
        }
        c.close();
        return false;
    }


}
