package com.shubhamgupta16.simplewallpaper.utils;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.shubhamgupta16.simplewallpaper.models.WallsPOJO;

import java.util.ArrayList;

public class SQLFav extends SQLiteOpenHelper {
    private static final String FAVORITES = "favs";
    private static final String DB_NAME = "fav012";

    private static final int PER_PAGE_ITEM = 16;


    public SQLFav(@Nullable Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + FAVORITES + "(id INTEGER PRIMARY KEY, url VARCHAR, previewUrl VARCHAR, name VARCHAR, categories VARCHAR, premium INTEGER);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public ArrayList<WallsPOJO> getWallpapers(int page) {
        int offset = (page - 1) * PER_PAGE_ITEM;

        ArrayList<WallsPOJO> list = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("SELECT * FROM " + FAVORITES + " LIMIT " + offset + ", " + PER_PAGE_ITEM, null);
        while (cursor.moveToNext()) {
            list.add(new WallsPOJO(cursor.getInt(0), cursor.getString(3), cursor.getString(2), cursor.getString(1), cursor.getString(4), cursor.getInt(5) != 0));
        }
        return list;
    }

    public int getPagesCount() {
        SQLiteDatabase db = this.getWritableDatabase();
        @SuppressLint("Recycle") Cursor c = db.rawQuery("SELECT count(*) FROM " + FAVORITES, null);
        if (c.moveToFirst()) {
            return (int) Math.ceil(c.getInt(0) / (float) PER_PAGE_ITEM);
        } else return 0;
    }


    public void toggleFavorite(WallsPOJO wall, boolean favorite) {
        SQLiteDatabase db = this.getWritableDatabase();
        boolean isFavorite = isFavorite(wall.getId());
        if (isFavorite == favorite) {
            return;
        }
        if (!favorite) {
            db.delete(FAVORITES, "id=" + wall.getId(), null);
            return;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put("previewUrl", wall.getPreviewUrl());
        contentValues.put("id", wall.getId());
        contentValues.put("name", wall.getName());
        contentValues.put("categories", wall.getCategories());
        contentValues.put("premium", wall.isPremium() ? 1 : 0);
        contentValues.put("url", wall.getUrl());
        db.insert(FAVORITES, null, contentValues);

    }

    public boolean isFavorite(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT count(*) FROM " + FAVORITES + " WHERE id=" + id, null);
        if (c.moveToFirst()) {
            return c.getInt(0) != 0;
        }
        c.close();
        return false;
    }


}
