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
import java.util.List;

public class SQLFav extends SQLiteOpenHelper {
    private static final String FAVORITES = "favs";
    private static final String DB_NAME = "fav01";

    public SQLFav(@Nullable Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + FAVORITES + "(id INTEGER PRIMARY KEY, favorite INTEGER);");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public List<Integer> favorites() {
        ArrayList<Integer> list = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM " + FAVORITES, null);
        while (cursor.moveToNext()) {
            list.add(cursor.getInt(0));
        }
        cursor.close();
        return list;
    }


    public void toggleFavorite(int id, boolean favorite) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("favorite", favorite ? 1 : 0);
        db.update(FAVORITES, contentValues, "id=" + id, null);
    }

    public boolean isFavorite(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT favorite FROM " + FAVORITES + " WHERE id=" + id, null);
        if (c.moveToFirst()) {
            return c.getInt(0) != 0;
        }
        c.close();
        return false;
    }


}
