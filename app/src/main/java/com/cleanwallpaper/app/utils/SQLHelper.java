package com.cleanwallpaper.app.utils;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.cleanwallpaper.app.models.CategoryPOJO;
import com.cleanwallpaper.app.models.WallsPOJO;

import java.util.ArrayList;

public class SQLHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "myWalls2";
    public static final String WALLPAPERS = "wallpapers";
    public static final String CATEGORIES = "categories";
    private static final int PER_PAGE_ITEM = 16;

    public static final int TYPE_NONE = 0;
    public static final int TYPE_CATEGORY = 1;
    public static final int TYPE_FAVORITE = 2;
    public static final int TYPE_QUERY = 3;
    public static final int TYPE_FAVORITE_QUERY = 4;


    public SQLHelper(@Nullable Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + WALLPAPERS + "(url VARCHAR PRIMARY KEY, previewUrl VARCHAR, name VARCHAR, categories VARCHAR, favorite INTEGER);");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + CATEGORIES + "(name VARCHAR PRIMARY KEY, preview1 VARCHAR, preview2 VARCHAR, preview3 VARCHAR);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void insertCategory(CategoryPOJO pojo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("preview1", pojo.getPreview1());
        contentValues.put("preview2", pojo.getPreview2());
        contentValues.put("preview3", pojo.getPreview3());
        if (checkAvailableCategory(pojo.getName())) {
            db.update(CATEGORIES, contentValues, "name='" + pojo.getName() + "'", null);
        } else {
            contentValues.put("name", pojo.getName());
            db.insert(CATEGORIES, null, contentValues);
        }
    }

    public void insertWallpaper(WallsPOJO pojo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("previewUrl", pojo.getPreviewUrl());
        contentValues.put("name", pojo.getName());
        contentValues.put("categories", pojo.getCategories());
        if (checkAvailableWallpaper(pojo.getUrl())) {
            db.update(WALLPAPERS, contentValues, "url='" + pojo.getUrl() + "'", null);
        } else {
            contentValues.put("url", pojo.getUrl());
            db.insert(WALLPAPERS, null, contentValues);
        }
    }

    public void toggleFavorite(String url, boolean favorite) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("favorite", favorite ? 1 : 0);
        db.update(WALLPAPERS, contentValues, "url='" + url + "'", null);
    }

    public boolean checkAvailableWallpaper(String url) {
        SQLiteDatabase db = this.getWritableDatabase();
        @SuppressLint("Recycle") Cursor c = db.rawQuery("SELECT url FROM " + WALLPAPERS + " WHERE url='" + url + "'", null);
        return c.moveToFirst();
    }

    public boolean checkAvailableCategory(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        @SuppressLint("Recycle") Cursor c = db.rawQuery("SELECT name FROM " + CATEGORIES + " WHERE name='" + name + "'", null);
        return c.moveToFirst();
    }

    @SuppressLint("Recycle")
    public ArrayList<CategoryPOJO> getCategories(String query) {
        ArrayList<CategoryPOJO> list = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor;
        if (query == null) {
            cursor = db.rawQuery("SELECT * FROM " + CATEGORIES, null);
        } else {
            cursor = db.rawQuery("SELECT * FROM " + CATEGORIES + " WHERE name LIKE '%" + query + "%'", null);
        }
        while (cursor.moveToNext()) {
            list.add(new CategoryPOJO(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), getWallsInCategoryCount(cursor.getString(0))));
        }
        return list;
    }

    @SuppressLint("Recycle")
    private int getWallsInCategoryCount(String name) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor;
                cursor = db.rawQuery("SELECT count(*) FROM " + WALLPAPERS + " WHERE categories LIKE '%" + name + "%'", null);
        if (cursor.moveToNext()) {
            return cursor.getInt(0);
        } else {
            return 0;
        }
    }


    public ArrayList<WallsPOJO> getWallpapers(int page, int type, String string) {

        return getListByPages(page, getQueryFromType(type, string));
    }

    public int getPagesCount(int type, String string) {
        return getPagesCount(getQueryFromType(type, string));
    }

    private String getQueryFromType(int type, String string) {
        String pagesCountQuery;
        switch (type) {
            case TYPE_FAVORITE:
                pagesCountQuery = " WHERE favorite=1";
                break;
            case TYPE_CATEGORY:
                if (string == null) return null;
                pagesCountQuery = " WHERE categories LIKE '%" + string + "%'";
                break;
            case TYPE_QUERY:
                if (string == null) return null;
                pagesCountQuery = " WHERE name LIKE '%" + string + "%'";
                break;
            case TYPE_FAVORITE_QUERY:
                if (string == null) return null;
                pagesCountQuery = " WHERE name LIKE '%" + string + "%' AND favorite=1";
                break;
            default:
                pagesCountQuery = "";
        }
        return pagesCountQuery;
    }

    private ArrayList<WallsPOJO> getListByPages(int page, String extras) {
        int offset = (page - 1) * PER_PAGE_ITEM;

        ArrayList<WallsPOJO> list = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("SELECT * FROM " + WALLPAPERS + extras + " LIMIT " + offset + ", " + PER_PAGE_ITEM, null);
        while (cursor.moveToNext()) {
            list.add(new WallsPOJO(cursor.getString(2), cursor.getString(1), cursor.getString(0), cursor.getString(3), cursor.getInt(4) != 0));
        }
        return list;
    }

    private int getPagesCount(String extras) {
        SQLiteDatabase db = this.getWritableDatabase();
        @SuppressLint("Recycle") Cursor c = db.rawQuery("SELECT count(*) FROM " + WALLPAPERS + extras, null);
        if (c.moveToFirst()) {
            return (int) Math.ceil(c.getInt(0) / (float) PER_PAGE_ITEM);
        } else return 0;
    }

    public boolean isFavorite(String url) {
        SQLiteDatabase db = this.getWritableDatabase();
        @SuppressLint("Recycle") Cursor c = db.rawQuery("SELECT favorite FROM " + WALLPAPERS + " WHERE url='" + url + "'", null);
        if (c.moveToFirst()) {
            return c.getInt(0) != 0;
        }
        return false;
    }
}
