package com.shubhamgupta16.simplewallpaper.data_source.impl;

import static com.shubhamgupta16.simplewallpaper.data_source.DataService.PER_PAGE_ITEM;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.shubhamgupta16.simplewallpaper.models.WallsPOJO;

import java.util.ArrayList;

public class InternalSQLWallpapers extends SQLiteOpenHelper {
    public static final String DB_NAME = "itsWallpapers";
    public static final String WALLPAPERS = "wallpapers";


    public enum QueryType {
        NONE,
        CATEGORY,
        SEARCH
    }

    public InternalSQLWallpapers(@Nullable Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + WALLPAPERS + "(id INTEGER PRIMARY KEY AUTOINCREMENT, url VARCHAR, previewUrl VARCHAR, name VARCHAR, categories VARCHAR, premium INTEGER, color VARCHAR, colorCode VARCHAR);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void clearAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(WALLPAPERS, null, null);
        try {
            db.delete("categories", null, null);
            Cursor c = db.rawQuery("DROP TABLE IF EXISTS categories", null);
            c.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isExist(String url) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + WALLPAPERS + " WHERE url='" + url + "'", null);
        boolean isExist = cursor.getCount() > 0;
        cursor.close();

        return isExist;
    }

    public void insertWallpaper(WallsPOJO pojo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("previewUrl", pojo.getPreviewUrl());
        contentValues.put("name", pojo.getName());
        contentValues.put("categories", pojo.getCategories());
        contentValues.put("premium", pojo.isPremium() ? 1 : 0);
        if (checkAvailableWallpaper(pojo.getUrl())) {
            db.update(WALLPAPERS, contentValues, "url='" + pojo.getUrl() + "'", null);
        } else {
            contentValues.put("url", pojo.getUrl());
            db.insert(WALLPAPERS, null, contentValues);
        }
    }

    private boolean checkAvailableWallpaper(String url) {
        SQLiteDatabase db = this.getWritableDatabase();
        @SuppressLint("Recycle") Cursor c = db.rawQuery("SELECT url FROM " + WALLPAPERS + " WHERE url='" + url + "'", null);
        return c.moveToFirst();
    }


     int getWallsInCategoryCount(String name) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor;
        cursor = db.rawQuery("SELECT count(*) FROM " + WALLPAPERS + " WHERE categories LIKE '%" + name + "%'", null);
        int res;
        if (cursor.moveToNext()) {
            res = cursor.getInt(0);
        } else {
            res = 0;
        }
        cursor.close();

        return res;
    }



    public ArrayList<WallsPOJO> getWallpapers(int page, QueryType type, String string) {
        return getListByPages(page, getWhereStatement(type, string));
    }


    private String getWhereStatement(QueryType type, String string) {
        switch (type) {
            case CATEGORY:
                if (string == null) return null;
                return " WHERE categories LIKE '%" + string + "%'";
            case SEARCH:
                if (string == null) return null;
                return " WHERE (name LIKE '%" + string + "%' OR categories LIKE '%" + string + "%')";
            default:
                return "";
        }
    }

    private ArrayList<WallsPOJO> getListByPages(int page, String extras) {
        int offset = (page - 1) * PER_PAGE_ITEM;

        ArrayList<WallsPOJO> list = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("SELECT * FROM " + WALLPAPERS + extras + " LIMIT " + offset + ", " + PER_PAGE_ITEM, null);
        while (cursor.moveToNext()) {
            list.add(new WallsPOJO(cursor.getString(1), cursor.getString(3), cursor.getString(2), cursor.getString(4), cursor.getInt(5) != 0));
        }
        return list;
    }

}